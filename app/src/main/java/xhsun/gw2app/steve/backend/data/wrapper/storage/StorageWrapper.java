package xhsun.gw2app.steve.backend.data.wrapper.storage;

import java.util.ArrayList;
import java.util.List;

import me.xhsun.guildwars2wrapper.error.GuildWars2Exception;
import xhsun.gw2app.steve.backend.data.model.AbstractModel;
import xhsun.gw2app.steve.backend.data.model.AccountModel;
import xhsun.gw2app.steve.backend.data.model.vault.item.Countable;
import xhsun.gw2app.steve.backend.data.model.vault.item.VaultItemModel;

/**
 * template for manipulate tables related to storage
 *
 * @author xhsun
 * @since 2017-05-04
 */

public abstract class StorageWrapper<I extends AbstractModel, S extends VaultItemModel> {
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
	public List<AccountModel> getAll() {
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

	long replace(S data) {
		return storageDB.replace(data);
	}

	void startInsert(List<S> data) {
		if (data.size() < 1) return;

		checkBaseItem(data);

		if (isCancelled) return;
		storageDB.bulkInsert(data);
	}

	protected void startUpdate(List<S> original, List<S> data) {
		List<S> newItem = new ArrayList<>(), deleteItem = new ArrayList<>(original);

		for (S d : data) {
			if (isCancelled) return;
			if (original.contains(d)) checkOriginal(original.get(original.indexOf(d)), d);
			else newItem.add(d);
		}

		deleteItem.removeAll(data);

		startInsert(newItem);

		if (deleteItem.size() < 1) return;
		storageDB.bulkDelete(deleteItem);
	}

	void updateIfDifferent(Countable old, Countable current) {
		if (old.getCount() == current.getCount()) return;
		old.setCount(current.getCount());
		//noinspection unchecked
		updateDB((S) old);
	}

	protected void updateDB(S info) {
		if (isCancelled) return;
		replace(info);
	}

	protected abstract void checkBaseItem(List<S> data);

	protected abstract void checkOriginal(S old, S current);
}
