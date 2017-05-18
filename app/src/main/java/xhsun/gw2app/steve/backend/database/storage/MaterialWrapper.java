package xhsun.gw2app.steve.backend.database.storage;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;
import xhsun.gw2api.guildwars2.GuildWars2;
import xhsun.gw2api.guildwars2.err.GuildWars2Exception;
import xhsun.gw2api.guildwars2.model.account.Material;
import xhsun.gw2app.steve.backend.data.AccountData;
import xhsun.gw2app.steve.backend.data.StorageData;
import xhsun.gw2app.steve.backend.database.account.AccountWrapper;
import xhsun.gw2app.steve.backend.database.common.ItemWrapper;
import xhsun.gw2app.steve.backend.database.common.SkinWrapper;
import xhsun.gw2app.steve.backend.util.vault.VaultType;

/**
 * for manipulate material storage item
 *
 * @author xhsun
 * @since 2017-05-04
 */

public class MaterialWrapper extends StorageWrapper {
	private GuildWars2 wrapper;
	private MaterialDB materialDB;
	private AccountWrapper accountWrapper;

	public MaterialWrapper(GuildWars2 wrapper, AccountWrapper accountWrapper, ItemWrapper itemWrapper,
	                       SkinWrapper skinWrapper, MaterialDB materialDB) {
		super(itemWrapper, skinWrapper, materialDB, VaultType.MATERIAL);
		this.wrapper = wrapper;
		this.accountWrapper = accountWrapper;
		this.materialDB = materialDB;
	}

	/**
	 * update material info for given account
	 *
	 * @param api API key
	 * @return updated list of materials for this account
	 * @throws GuildWars2Exception error when interacting with server
	 */
	public List<StorageData> update(String api) throws GuildWars2Exception {
		Timber.i("Start updating material storage info for %s", api);
		try {
			_update(wrapper.getMaterialStorage(api), api);
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

	//update or add item to material storage
	private void _update(List<Material> bank, String api) {
		List<StorageData> known = get(api);
		List<StorageData> seen = new ArrayList<>();
		for (Material b : bank) {
			if (isCancelled) return;
			if (b == null || b.getCount() == 0) continue;//nothing here, move on
			updateStorage(known, seen, new StorageData(b, api));
		}

		//remove all outdated storage item from database
		for (StorageData i : known) {
			if (isCancelled) return;
			materialDB.delete(i.getId());
		}
	}
}
