package xhsun.gw2app.steve.backend.database.storage;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.util.List;

import me.xhsun.guildwars2wrapper.GuildWars2;
import me.xhsun.guildwars2wrapper.SynchronousRequest;
import me.xhsun.guildwars2wrapper.error.GuildWars2Exception;
import timber.log.Timber;
import xhsun.gw2app.steve.backend.data.AccountData;
import xhsun.gw2app.steve.backend.data.vault.WardrobeData;
import xhsun.gw2app.steve.backend.data.vault.item.WardrobeItemData;
import xhsun.gw2app.steve.backend.database.account.AccountWrapper;
import xhsun.gw2app.steve.backend.database.common.SkinWrapper;

/**
 * for manipulate wardrobe item
 *
 * @author xhsun
 * @since 2017-05-04
 */
public class WardrobeWrapper extends StorageWrapper<WardrobeData, WardrobeItemData> {
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
	public List<WardrobeData> update(String api) throws GuildWars2Exception {
		Timber.i("Start updating wardrobe info for %s", api);
		try {
			List<Integer> ids = request.getUnlockedSkins(api);
			List<WardrobeItemData> known = Stream.of(get(api))
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
					accountWrapper.markInvalid(new AccountData(api));
			}
		}

		return get(api);
	}

	private void startUpdate(String api, List<WardrobeItemData> known, List<Integer> ids) {
		for (Integer b : ids) {
			if (isCancelled) return;
			updateRecord(known, new WardrobeItemData(api, b));
		}

		//remove all outdated storage item from database
		for (WardrobeItemData i : known) {
			if (isCancelled) return;
			delete(i);
		}
	}

	private void updateRecord(List<WardrobeItemData> known, WardrobeItemData info) {
		if (known.contains(info)) known.remove(info);//remove this item, so it don't get removed
		else updateDatabase(info, true);
	}

	@Override
	protected void updateDatabase(WardrobeItemData info, boolean isItemSeen) {
		if (isCancelled) return;
		if (skinWrapper.get(info.getSkinData().getId()) == null)
			skinWrapper.update(info.getSkinData().getId());

		replace(info);//add
	}
}
