package xhsun.gw2app.steve.backend.database.storage;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;
import xhsun.gw2api.guildwars2.GuildWars2;
import xhsun.gw2api.guildwars2.err.GuildWars2Exception;
import xhsun.gw2api.guildwars2.model.account.Material;
import xhsun.gw2app.steve.backend.data.AccountData;
import xhsun.gw2app.steve.backend.data.vault.MaterialStorageData;
import xhsun.gw2app.steve.backend.data.vault.item.Countable;
import xhsun.gw2app.steve.backend.data.vault.item.MaterialItemData;
import xhsun.gw2app.steve.backend.database.account.AccountWrapper;
import xhsun.gw2app.steve.backend.database.common.ItemWrapper;

/**
 * for manipulate material storage item
 *
 * @author xhsun
 * @since 2017-05-04
 */

public class MaterialWrapper extends StorageWrapper<MaterialStorageData, MaterialItemData> {
	private GuildWars2 wrapper;
	private ItemWrapper itemWrapper;
	private AccountWrapper accountWrapper;

	public MaterialWrapper(GuildWars2 wrapper, AccountWrapper accountWrapper, ItemWrapper itemWrapper,
	                       MaterialDB materialDB) {
		super(materialDB);
		this.wrapper = wrapper;
		this.accountWrapper = accountWrapper;
		this.itemWrapper = itemWrapper;
	}

	/**
	 * update material info for given account
	 *
	 * @param api API key
	 * @return updated list of materials for this account
	 * @throws GuildWars2Exception error when interacting with server
	 */
	public List<MaterialStorageData> update(String api) throws GuildWars2Exception {
		Timber.i("Start updating material storage info for %s", api);
		try {
			startUpdate(api,
					Stream.of(get(api)).flatMap(m -> Stream.of(m.getItems())).collect(Collectors.toList()),
					wrapper.getMaterialStorage(api));
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
	private void startUpdate(String api, List<MaterialItemData> original, List<Material> bank) {
		List<Countable> known = new ArrayList<>(original);
		List<Countable> seen = new ArrayList<>();
		for (Material b : bank) {
			if (isCancelled) return;
			if (b == null || b.getCount() == 0) continue;//nothing here, move on
			updateRecord(known, seen, new MaterialItemData(api, b));
		}

		//remove all outdated storage item from database
		for (Countable i : known) {
			if (isCancelled) return;
			delete((MaterialItemData) i);
		}
	}

	@Override
	protected void updateDatabase(MaterialItemData info, boolean isItemSeen) {
		if (isCancelled) return;
		//insert item if needed
		if (!isItemSeen && itemWrapper.get(info.getItemData().getId()) == null)
			itemWrapper.update(info.getItemData().getId());
		replace(info);//update
	}
}
