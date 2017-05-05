package xhsun.gw2app.steve.backend.database.storage;

import java.util.List;

import xhsun.gw2app.steve.backend.database.account.AccountInfo;
import xhsun.gw2app.steve.backend.database.common.ItemWrapper;
import xhsun.gw2app.steve.backend.database.common.SkinWrapper;
import xhsun.gw2app.steve.backend.util.items.StorageType;

/**
 * template for manipulate tables related to storage
 *
 * @author xhsun
 * @since 2017-05-04
 */

abstract class StorageWrapper {
	private ItemWrapper itemWrapper;
	protected SkinWrapper skinWrapper;
	private StorageDB storageDB;
	private StorageType type;
	protected boolean isCancelled = false;

	StorageWrapper(ItemWrapper itemWrapper, SkinWrapper skinWrapper, StorageDB storageDB, StorageType type) {
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
	public List<AccountInfo> getAll() {
		return storageDB.getAll();
	}

	/**
	 * get storage info for given account
	 *
	 * @param value character name | API key
	 * @return list of storage info | empty if not find
	 */
	public List<StorageInfo> get(String value) {
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

	//TODO probably need update
	void updateStorage(List<StorageInfo> known, List<StorageInfo> seen, StorageInfo info) {
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
			StorageInfo old = seen.get(seen.indexOf(info));
			//update count to new + old count
			old.setCount(old.getCount() + info.getCount());
			info = old;
		}
		if (info.getCount() > 0 && shouldUpdate) __update(info, isItemSeen);
	}

	//update or add storage item
	private void __update(StorageInfo info, boolean isItemSeen) {
		if (isCancelled) return;
		//insert item if needed
		if (!isItemSeen && itemWrapper.get(info.getItemInfo().getId()) == null)
			itemWrapper.update(info.getItemInfo().getId());
		//insert skin if needed
		if (type != StorageType.MATERIAL && !isItemSeen && info.getSkinInfo().getId() != 0
				&& skinWrapper.get(info.getSkinInfo().getId()) == null)
			skinWrapper.update(info.getSkinInfo().getId());
		//update
		long result = storageDB.replace(info);
		if (result >= 0) info.setId(result);
	}
}
