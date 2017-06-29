package xhsun.gw2app.steve.backend.data.wrapper.storage;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.xhsun.guildwars2wrapper.GuildWars2;
import me.xhsun.guildwars2wrapper.SynchronousRequest;
import me.xhsun.guildwars2wrapper.error.GuildWars2Exception;
import timber.log.Timber;
import xhsun.gw2app.steve.backend.data.model.AccountModel;
import xhsun.gw2app.steve.backend.data.model.MiscItemModel;
import xhsun.gw2app.steve.backend.data.model.SkinModel;
import xhsun.gw2app.steve.backend.data.model.vault.WardrobeModel;
import xhsun.gw2app.steve.backend.data.model.vault.item.WardrobeItemModel;
import xhsun.gw2app.steve.backend.data.wrapper.account.AccountWrapper;
import xhsun.gw2app.steve.backend.data.wrapper.common.MiscWrapper;
import xhsun.gw2app.steve.backend.data.wrapper.common.SkinWrapper;

/**
 * for manipulate wardrobe item
 *
 * @author xhsun
 * @since 2017-05-04
 */
public class WardrobeWrapper extends StorageWrapper<WardrobeModel, WardrobeItemModel> {
	public enum SelectableType {SKIN, MINI, OUTFIT, MISC}
	private SynchronousRequest request;
	private AccountWrapper accountWrapper;
	private SkinWrapper skinWrapper;
	private MiscWrapper miscWrapper;

	public WardrobeWrapper(GuildWars2 wrapper, AccountWrapper accountWrapper,
	                       SkinWrapper skinWrapper, MiscWrapper miscWrapper, WardrobeDB wardrobeDB) {
		super(wardrobeDB);
		request = wrapper.getSynchronous();
		this.accountWrapper = accountWrapper;
		this.skinWrapper = skinWrapper;
		this.miscWrapper = miscWrapper;
	}

	/**
	 * update wardrobe info for given account
	 *
	 * @param key combination of api and {@link SelectableType}
	 * @return updated list of skins for this account
	 * @throws GuildWars2Exception error when interacting with server
	 */
	public List<WardrobeModel> update(String key) throws GuildWars2Exception {
		SelectableType type;
		String[] value = key.split("\n");
		String api = value[0];
		try {
			type = SelectableType.valueOf(value[1]);
		} catch (IllegalArgumentException e) {
			Timber.e("Invalid type (%s), abort", value[1]);
			return null;
		}

		Timber.i("Start updating wardrobe info for %s", api);
		try {
			List<WardrobeItemModel> wardrobe = new ArrayList<>(), original = Stream.of(get(api))
					.flatMap(w -> Stream.of(w.getData()))
					.flatMap(s -> Stream.of(s.getItems()))
					.collect(Collectors.toList());

			switch (type) {
				case SKIN:
					Stream.of(request.getUnlockedSkins(api)).forEach(w -> wardrobe.add(new WardrobeItemModel(api, w)));
					break;
				case MINI:
					Stream.of(request.getUnlockedMinis(api))
							.forEach(w -> wardrobe.add(new WardrobeItemModel(api, MiscItemModel.MiscItemType.MINI, w)));
					break;
				case OUTFIT:
					Stream.of(request.getUnlockedOutfits(api))
							.forEach(w -> wardrobe.add(new WardrobeItemModel(api, MiscItemModel.MiscItemType.OUTFIT, w)));
					break;
				case MISC:
					Stream.of(request.getUnlockedGliders(api))
							.forEach(w -> wardrobe.add(new WardrobeItemModel(api, MiscItemModel.MiscItemType.GLIDER, w)));
					Stream.of(request.getUnlockedMailCarriers(api))
							.forEach(w -> wardrobe.add(new WardrobeItemModel(api, MiscItemModel.MiscItemType.MAILCARRIER, w)));
					Stream.of(request.getUnlockedFinishers(api))
							.forEach(w -> {
								WardrobeItemModel m = new WardrobeItemModel(api, MiscItemModel.MiscItemType.FINISHER, w.getId());
								if (!w.isPermanent() && w.getQuantity() > 0) m.setCount(w.getQuantity());
								wardrobe.add(m);
							});
					break;
			}

			if (original.size() < 1) startInsert(wardrobe);
			else startUpdate(original, wardrobe);
		} catch (GuildWars2Exception e) {
			Timber.e(e, "Error occurred when trying to get bank information for %s", api);
			switch (e.getErrorCode()) {
				case Server:
				case Limit:
				case Network:
					throw e;
				case Key://mark account invalid
					accountWrapper.markInvalid(new AccountModel(api));
			}
		}

		return get(api);
	}

	void startUpdate(List<WardrobeItemModel> original, List<WardrobeItemModel> data) {
		List<WardrobeItemModel> newItem = new ArrayList<>();

		for (WardrobeItemModel d : data) {
			if (isCancelled) return;
			if (original.contains(d)) checkOriginal(original.get(original.indexOf(d)), d);
			else newItem.add(d);
		}

		startInsert(newItem);
	}

	@Override
	protected void checkBaseItem(List<WardrobeItemModel> data) {
		List<Integer> oSkin = Stream.of(skinWrapper.getAll()).map(SkinModel::getId).collect(Collectors.toList());
		List<String> oMisc = Stream.of(miscWrapper.getAll()).map(MiscItemModel::getCombinedID).collect(Collectors.toList());

		skinWrapper.bulkInsert(Stream.of(data).filter(s -> s.getSkinModel() != null)
				.filterNot(i -> oSkin.contains(i.getSkinModel().getId()))
				.map(i -> i.getSkinModel().getId()).mapToInt(Integer::intValue).toArray());

		Map<MiscItemModel.MiscItemType, List<Integer>> sections = new HashMap<>();
		Stream.of(data).filter(s -> s.getMiscItem() != null).forEach(s -> partition(sections, s));

		for (Map.Entry<MiscItemModel.MiscItemType, List<Integer>> e : sections.entrySet()) {
			miscWrapper.bulkInsert(e.getKey(), Stream.of(e.getValue())
					.filterNot(i -> oMisc.contains(MiscItemModel.formatID(e.getKey(), i)))
					.mapToInt(Integer::intValue).toArray());
		}
	}

	@Override
	protected void checkOriginal(WardrobeItemModel old, WardrobeItemModel current) {
		if (old.getSkinModel() != null || (old.getMiscItem() != null &&
				(old.getMiscItem().getType() != MiscItemModel.MiscItemType.FINISHER ||
						old.getCount() == current.getCount()))) return;

		if (current.getCount() == 0) delete(current);
		else {
			old.setCount(current.getCount());
			updateDB(old);
		}
	}

	private void partition(Map<MiscItemModel.MiscItemType, List<Integer>> sections, WardrobeItemModel m) {
		MiscItemModel.MiscItemType type = m.getMiscItem().getType();

		if (sections.containsKey(type)) {
			sections.get(type).add(m.getMiscItem().getId());
		} else {
			List<Integer> temp = new ArrayList<>();
			temp.add(m.getMiscItem().getId());
			sections.put(type, temp);
		}
	}
}
