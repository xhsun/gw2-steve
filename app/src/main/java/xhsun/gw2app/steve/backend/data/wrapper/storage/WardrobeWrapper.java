package xhsun.gw2app.steve.backend.data.wrapper.storage;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.util.List;

import me.xhsun.guildwars2wrapper.GuildWars2;
import me.xhsun.guildwars2wrapper.SynchronousRequest;
import me.xhsun.guildwars2wrapper.error.GuildWars2Exception;
import timber.log.Timber;
import xhsun.gw2app.steve.backend.data.model.AccountModel;
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
			List<Integer> ids = request.getUnlockedSkins(api);
			List<WardrobeItemModel> known = Stream.of(get(api))
					.flatMap(w -> Stream.of(w.getData()))
					.flatMap(s -> Stream.of(s.getItems()))
					.collect(Collectors.toList());

			startUpdate(api, known, ids);
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

	private void startUpdate(String api, List<WardrobeItemModel> known, List<Integer> ids) {
		for (Integer b : ids) {
			if (isCancelled) return;
			updateRecord(known, new WardrobeItemModel(api, b));
		}

		//remove all outdated storage item from database
		for (WardrobeItemModel i : known) {
			if (isCancelled) return;
			delete(i);
		}
	}

	private void updateRecord(List<WardrobeItemModel> known, WardrobeItemModel info) {
		if (known.contains(info)) known.remove(info);//remove this item, so it don't get removed
		else updateDatabase(info, true);
	}

	@Override
	protected void updateDatabase(WardrobeItemModel info, boolean isItemSeen) {
		if (isCancelled) return;
		if (skinWrapper.get(info.getSkinModel().getId()) == null)
			skinWrapper.update(info.getSkinModel().getId());

		replace(info);//add
	}
}
