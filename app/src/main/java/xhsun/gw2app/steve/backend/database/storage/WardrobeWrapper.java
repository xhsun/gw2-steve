package xhsun.gw2app.steve.backend.database.storage;

import java.util.List;

import timber.log.Timber;
import xhsun.gw2api.guildwars2.GuildWars2;
import xhsun.gw2api.guildwars2.err.GuildWars2Exception;
import xhsun.gw2app.steve.backend.database.account.AccountInfo;
import xhsun.gw2app.steve.backend.database.account.AccountWrapper;
import xhsun.gw2app.steve.backend.database.common.ItemWrapper;
import xhsun.gw2app.steve.backend.database.common.SkinWrapper;
import xhsun.gw2app.steve.backend.util.items.StorageType;

/**
 * for manipulate wardrobe item
 *
 * @author xhsun
 * @since 2017-05-04
 */
public class WardrobeWrapper extends StorageWrapper {
	private GuildWars2 wrapper;
	private WardrobeDB wardrobeDB;
	private AccountWrapper accountWrapper;

	public WardrobeWrapper(GuildWars2 wrapper, AccountWrapper accountWrapper, ItemWrapper itemWrapper,
	                       SkinWrapper skinWrapper, WardrobeDB wardrobeDB) {
		super(itemWrapper, skinWrapper, wardrobeDB, StorageType.WARDROBE);
		this.wrapper = wrapper;
		this.accountWrapper = accountWrapper;
		this.wardrobeDB = wardrobeDB;
	}

	/**
	 * update wardrobe info for given account
	 *
	 * @param api API key
	 * @return updated list of skins for this account
	 * @throws GuildWars2Exception error when interacting with server
	 */
	public List<StorageInfo> update(String api) throws GuildWars2Exception {
		Timber.i("Start updating wardrobe info for %s", api);
		try {
			List<Long> ids = wrapper.getUnlockedSkins(api);
			List<StorageInfo> known = get(api);

			for (Long b : ids) {
				if (isCancelled) return get(api);
				StorageInfo s = new StorageInfo(b, api);
				if (known.contains(s)) known.remove(s);//remove this item, so it don't get removed
				else add(s);
			}

			//remove all outdated storage item from database
			for (StorageInfo i : known) {
				if (isCancelled) return get(api);
				wardrobeDB.delete(i.getSkinInfo().getId(), i.getApi());
			}
		} catch (GuildWars2Exception e) {
			Timber.e(e, "Error occurred when trying to get bank information for %s", api);
			switch (e.getErrorCode()) {
				case Server:
				case Limit:
				case Network:
					throw e;
				case Key://mark account invalid
					accountWrapper.markInvalid(new AccountInfo(api));
			}
		}

		return get(api);
	}

	private void add(StorageInfo skin) {
		if (isCancelled) return;
		if (skinWrapper.get(skin.getSkinInfo().getId()) == null)
			skinWrapper.update(skin.getSkinInfo().getId());

		wardrobeDB.replace(skin); //add
	}
}
