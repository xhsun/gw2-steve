package xhsun.gw2app.steve.backend.database.storage;

import java.util.List;

import xhsun.gw2api.guildwars2.err.GuildWars2Exception;
import xhsun.gw2app.steve.backend.data.AccountData;
import xhsun.gw2app.steve.backend.data.StorageData;
import xhsun.gw2app.steve.backend.database.common.ItemWrapper;
import xhsun.gw2app.steve.backend.database.common.SkinWrapper;
import xhsun.gw2app.steve.backend.util.vault.VaultType;

/**
 * template for manipulate tables related to storage
 *
 * @author xhsun
 * @since 2017-05-04
 */

public abstract class StorageWrapper {
	private ItemWrapper itemWrapper;
	protected SkinWrapper skinWrapper;
	private StorageDB storageDB;
	private VaultType type;
	protected boolean isCancelled = false;

	StorageWrapper(ItemWrapper itemWrapper, SkinWrapper skinWrapper, StorageDB storageDB, VaultType type) {
		this.itemWrapper = itemWrapper;
		this.skinWrapper = skinWrapper;
		this.storageDB = storageDB;
		this.type = type;
	}

	/**
	 * get all storage info
	 *
	 * @return list of account info | empty if not find
	 */
	public List<AccountData> getAll() {
		return storageDB.getAll();
	}

	/**
	 * get storage info for given account
	 *
	 * @param value character name | API key
	 * @return list of storage info | empty if not find
	 */
	public List<StorageData> get(String value) {
		return storageDB.get(value);
	}

	/**
	 * set interrupt to stop any loops
	 *
	 * @param cancelled true to cancel
	 */
	public void setCancelled(boolean cancelled) {
		isCancelled = cancelled;
	}

	public abstract List<StorageData> update(String key) throws GuildWars2Exception;

	public String concatCharacterName(String api, String name) {
		return api + "\n" + name;
	}

	//TODO probably need update
	void updateStorage(List<StorageData> known, List<StorageData> seen, StorageData info) {
		boolean isItemSeen = false, shouldUpdate = true;
		if (!seen.contains(info)) {//haven't see this item
			seen.add(info);
			//item is already in the database, update id, so that correct item will get updated
			if (known.contains(info)) {
				if (known.get(known.indexOf(info)).getCount() == info.getCount()) shouldUpdate = false;
				else {
					isItemSeen = true;
					info.setId(known.get(known.indexOf(info)).getId());
				}
			}
			known.remove(info);//remove this item, so it don't get removed
		} else {//already see this item, update count
			isItemSeen = true;
			StorageData old = seen.get(seen.indexOf(info));
			//update count to new + old count
			old.setCount(old.getCount() + info.getCount());
			info = old;
		}
		if (info.getCount() > 0 && shouldUpdate) __update(info, isItemSeen);
	}

	//update or add storage item
	private void __update(StorageData info, boolean isItemSeen) {
		if (isCancelled) return;
		//insert item if needed
		if (!isItemSeen && itemWrapper.get(info.getItemData().getId()) == null)
			itemWrapper.update(info.getItemData().getId());
		//insert skin if needed
		if (type != VaultType.MATERIAL && !isItemSeen && info.getSkinData() != null &&
				info.getSkinData().getId() != 0 && skinWrapper.get(info.getSkinData().getId()) == null)
			skinWrapper.update(info.getSkinData().getId());
		//update
		long result = storageDB.replace(info);
		if (result >= 0) info.setId(result);
	}
}
