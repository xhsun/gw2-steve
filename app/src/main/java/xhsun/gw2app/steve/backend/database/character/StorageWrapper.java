package xhsun.gw2app.steve.backend.database.character;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;
import xhsun.gw2api.guildwars2.GuildWars2;
import xhsun.gw2api.guildwars2.err.GuildWars2Exception;
import xhsun.gw2api.guildwars2.model.character.CharacterInventory;
import xhsun.gw2api.guildwars2.model.util.Bag;
import xhsun.gw2api.guildwars2.model.util.Storage;
import xhsun.gw2app.steve.backend.database.account.AccountInfo;
import xhsun.gw2app.steve.backend.database.account.AccountWrapper;
import xhsun.gw2app.steve.backend.database.common.ItemWrapper;

/**
 * For manipulate storage items
 *
 * @author xhsun
 * @since 2017-03-29
 */

public class StorageWrapper {
	private GuildWars2 wrapper;
	private StorageDB storageDB;
	private AccountWrapper accountWrapper;
	private CharacterWrapper characterWrapper;
	private ItemWrapper itemWrapper;
	private boolean isCancelled = false;

	@Inject
	public StorageWrapper(GuildWars2 wrapper, AccountWrapper account, CharacterWrapper characterWrapper, ItemWrapper itemWrapper, StorageDB storage) {
		this.storageDB = storage;
		this.wrapper = wrapper;
		this.characterWrapper = characterWrapper;
		this.itemWrapper = itemWrapper;
		accountWrapper = account;
	}

	/**
	 * get all storage info base
	 * @param value character name if getting inventory info | api if getting bank info
	 * @param isBank true to get bank info | false to get inventory info
	 * @return list of storage info | empty if not find
	 */
	public List<StorageInfo> getAll(String value, boolean isBank) {
		List<StorageInfo> storage;
		if (isBank) storage = storageDB.getAllByAPI(value);
		else storage = storageDB.getAllByHolder(value);
		for (StorageInfo s : storage) s.setItemInfo(itemWrapper.get(s.getItemInfo().getId()));
		return storage;
	}

	/**
	 * update inventory info for given character
	 *
	 * @param character character info
	 * @return list of item that is in this character | empty if there is nothing
	 * @throws GuildWars2Exception error when interacting with server
	 */
	public List<StorageInfo> updateInventoryInfo(CharacterInfo character) throws GuildWars2Exception {
		final String api = character.getApi();
		final String name = character.getName();
		Timber.i("Start updating character inventory info for %s", name);
		try {
			CharacterInventory stuff = wrapper.getCharacterInventory(api, name);
			List<Storage> inventory = new ArrayList<>();
			for (Bag bag : stuff.getBags()) {
				if (isCancelled) break;
				if (bag == null) continue;
				inventory.addAll(bag.getInventory());
			}
			updateStorage(inventory, api, name, false);
		} catch (GuildWars2Exception e) {
			Timber.e(e, "Error occurred when trying to get storage information for %s", name);
			switch (e.getErrorCode()) {
				case Server:
				case Limit:
				case Network:
					throw e;
				case Key://mark account invalid and remove character from database
					accountWrapper.markInvalid(new AccountInfo(api));
				case Character://remove character from database
					characterWrapper.delete(name);
			}
		}
		return getAll(name, false);
	}

	public void setCancelled(boolean cancelled) {
		isCancelled = cancelled;
	}

	//if item is in bank: value -> category name; else: value -> character name
	private void updateStorage(List<Storage> storage, String api, String value, boolean isBank) {
		List<StorageInfo> items = (isBank) ? storageDB.getAllByAPI(api) : storageDB.getAllByHolder(value);
		List<StorageInfo> seen = new ArrayList<>();
		for (Storage s : storage) {
			if (isCancelled) return;
			if (s == null) continue;//nothing here, move on
			long count;
			StorageInfo info = new StorageInfo(s, api, value, isBank);
			if (!seen.contains(info)) {//haven't see this item
				seen.add(info);
				if (items.contains(info)) info.setId(items.get(items.indexOf(info)).getId());
				items.remove(info);
				count = info.getCount();
			} else {//already see this item, update count
				StorageInfo old = seen.get(seen.indexOf(info));
				old.setCount(old.getCount() + info.getCount());
				info.setId(old.getId());
				count = old.getCount();
			}

			update(info, count, isBank);
		}
		//remove all outdated storage item from database
		for (StorageInfo i : items) storageDB.delete(i.getId(), isBank);
	}

	//update or add storage item
	private void update(StorageInfo info, long newCount, boolean isBank) {
		if (isCancelled) return;
		if (itemWrapper.get(info.getItemInfo().getId()) == null)
			itemWrapper.updateOrAdd(info.getItemInfo().getId());
		long result = storageDB.replace(info.getId(), info.getItemInfo().getId(), info.getCharacterName(), info.getApi(),
				newCount, info.getCategoryName(), info.getBinding(), info.getBoundTo(), isBank);
		if (result > 0) info.setId(result);
	}
}
