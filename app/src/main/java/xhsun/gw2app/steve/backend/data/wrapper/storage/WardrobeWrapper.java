package xhsun.gw2app.steve.backend.data.wrapper.storage;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.util.ArrayList;
import java.util.List;

import me.xhsun.guildwars2wrapper.GuildWars2;
import me.xhsun.guildwars2wrapper.SynchronousRequest;
import me.xhsun.guildwars2wrapper.error.GuildWars2Exception;
import timber.log.Timber;
import xhsun.gw2app.steve.backend.data.model.AccountModel;
import xhsun.gw2app.steve.backend.data.model.SkinModel;
import xhsun.gw2app.steve.backend.data.model.vault.WardrobeModel;
import xhsun.gw2app.steve.backend.data.model.vault.item.WardrobeItemModel;
import xhsun.gw2app.steve.backend.data.wrapper.account.AccountWrapper;
import xhsun.gw2app.steve.backend.data.wrapper.common.SkinWrapper;

/**
 * for manipulate wardrobe item
 *
 * @author xhsun
 * @since 2017-05-04
 */
public class WardrobeWrapper extends StorageWrapper<WardrobeModel, WardrobeItemModel> {
	private SynchronousRequest request;
	private AccountWrapper accountWrapper;
	private SkinWrapper skinWrapper;

	public WardrobeWrapper(GuildWars2 wrapper, AccountWrapper accountWrapper,
	                       SkinWrapper skinWrapper, WardrobeDB wardrobeDB) {
		super(wardrobeDB);
		request = wrapper.getSynchronous();
		this.accountWrapper = accountWrapper;
		this.skinWrapper = skinWrapper;
	}

	/**
	 * update wardrobe info for given account
	 *
	 * @param api API key
	 * @return updated list of skins for this account
	 * @throws GuildWars2Exception error when interacting with server
	 */
	public List<WardrobeModel> update(String api) throws GuildWars2Exception {
		Timber.i("Start updating wardrobe info for %s", api);
		try {
			List<WardrobeItemModel> wardrobe = new ArrayList<>(), original = Stream.of(get(api))
					.flatMap(w -> Stream.of(w.getData()))
					.flatMap(s -> Stream.of(s.getItems()))
					.collect(Collectors.toList());

			Stream.of(request.getUnlockedSkins(api)).forEach(w -> wardrobe.add(new WardrobeItemModel(api, w)));

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

	protected void startUpdate(List<WardrobeItemModel> original, List<WardrobeItemModel> wardrobe) {
		List<WardrobeItemModel> newItem = Stream.of(wardrobe).filterNot(original::contains)
				.collect(Collectors.toList());

		startInsert(newItem);
	}

	@Override
	protected void checkBaseItem(List<WardrobeItemModel> data) {
		List<Integer> oSkin = Stream.of(skinWrapper.getAll()).map(SkinModel::getId).collect(Collectors.toList());

		skinWrapper.bulkInsert(Stream.of(data).filterNot(i -> oSkin.contains(i.getSkinModel().getId()))
				.map(i -> i.getSkinModel().getId()).mapToInt(Integer::intValue).toArray());
	}

	@Override
	protected void checkOriginal(WardrobeItemModel old, WardrobeItemModel current) {
		//wardrobe don't need this
	}
}
