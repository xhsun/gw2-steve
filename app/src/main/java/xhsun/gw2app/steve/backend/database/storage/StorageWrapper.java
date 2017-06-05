package xhsun.gw2app.steve.backend.database.storage;

import java.util.List;

import me.xhsun.guildwars2wrapper.error.GuildWars2Exception;
import xhsun.gw2app.steve.backend.data.AbstractData;
import xhsun.gw2app.steve.backend.data.AccountData;
import xhsun.gw2app.steve.backend.data.vault.item.Countable;
import xhsun.gw2app.steve.backend.data.vault.item.VaultItemData;

/**
 * template for manipulate tables related to storage
 *
 * @author xhsun
 * @since 2017-05-04
 */

public abstract class StorageWrapper<I extends AbstractData, S extends VaultItemData> {
	private final int TRUE = 1;
	private StorageDB<I, S> storageDB;
	protected boolean isCancelled = false;

	StorageWrapper(StorageDB<I, S> storageDB) {
		this.storageDB = storageDB;
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
	public List<I> get(String value) {
		if (isCancelled) return null;
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

	public abstract List<I> update(String key) throws GuildWars2Exception;

	public String concatCharacterName(String api, String name) {
		return api + "\n" + name;
	}

	protected long replace(S data) {
		return storageDB.replace(data);
	}

	protected boolean delete(S data) {
		return storageDB.delete(data);
	}

	protected abstract void updateDatabase(S info, boolean isItemSeen);

	void updateRecord(List<Countable> known, List<Countable> seen, Countable info) {
		long[] result = updateCountableRecord(known, seen, info);
		info.setCount(result[2]);
		info.setId(result[3]);
		if (info.getCount() > 0 && result[1] == TRUE)
			//noinspection unchecked
			updateDatabase((S) info, result[0] == TRUE);
	}

	private long[] updateCountableRecord(List<Countable> known, List<Countable> seen, Countable data) {
		int FALSE = 0;
		long[] result = {FALSE, TRUE, data.getCount(), data.getId()};
		if (!seen.contains(data)) {//haven't see this item
			seen.add(data);
			//item is already in the database, update id, so that correct item will get updated
			if (known.contains(data)) {
				if (known.get(known.indexOf(data)).getCount() == data.getCount()) result[1] = FALSE;
				else result[0] = TRUE;
				result[3] = known.get(known.indexOf(data)).getId();
			}
			result[2] = data.getCount();
			known.remove(data);//remove this item, so it don't get removed
		} else {//already see this item, update count
			result[0] = TRUE;
			Countable old = seen.get(seen.indexOf(data));
			//update count to new + old count
			old.setCount(old.getCount() + data.getCount());
			result[2] = old.getCount();
			result[3] = old.getId();
		}
		return result;
	}
}
