package xhsun.gw2app.steve.backend.database.storage;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;
import xhsun.gw2api.guildwars2.GuildWars2;
import xhsun.gw2api.guildwars2.err.GuildWars2Exception;
import xhsun.gw2api.guildwars2.model.character.CharacterInventory;
import xhsun.gw2api.guildwars2.model.util.Bag;
import xhsun.gw2api.guildwars2.model.util.Inventory;
import xhsun.gw2api.guildwars2.model.util.Storage;
import xhsun.gw2app.steve.backend.database.account.AccountInfo;
import xhsun.gw2app.steve.backend.database.account.AccountWrapper;
import xhsun.gw2app.steve.backend.database.character.CharacterInfo;
import xhsun.gw2app.steve.backend.database.character.CharacterWrapper;
import xhsun.gw2app.steve.backend.database.common.ItemWrapper;
import xhsun.gw2app.steve.backend.database.common.SkinWrapper;

/**
 * For manipulate storage items
 *
 * @author xhsun
 * @since 2017-03-29
 */

public class InventoryWrapper {
	private GuildWars2 wrapper;
	private InventoryDB inventoryDB;
	private AccountWrapper accountWrapper;
	private CharacterWrapper characterWrapper;
	private ItemWrapper itemWrapper;
	private SkinWrapper skinWrapper;
	private boolean isCancelled = false;

	@Inject
	public InventoryWrapper(GuildWars2 wrapper, AccountWrapper account,
	                        CharacterWrapper characterWrapper, ItemWrapper itemWrapper,
	                        SkinWrapper skinWrapper, InventoryDB inventory) {
		this.inventoryDB = inventory;
		this.wrapper = wrapper;
		this.characterWrapper = characterWrapper;
		this.itemWrapper = itemWrapper;
		this.skinWrapper = skinWrapper;
		accountWrapper = account;
	}

	/**
	 * get all inventory info
	 * @return list of storage info | empty if not find
	 */
	public List<AccountInfo> getAll() {
		return inventoryDB.getAll();
	}

	/**
	 * update inventory info for given character
	 *
	 * @param character character info
	 * @return inventory info for this character | empty if there is nothing
	 * @throws GuildWars2Exception error when interacting with server
	 */
	public List<StorageInfo> updateInventory(CharacterInfo character) throws GuildWars2Exception {
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
			updateInventory(inventory, api, name);
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
		return getAllInventory(name);
	}

	/**
	 * set interrupt to stop any loops
	 *
	 * @param cancelled true to cancel
	 */
	public void setCancelled(boolean cancelled) {
		isCancelled = cancelled;
	}

	private void updateInventory(List<Storage> storage, String api, String name) {
		List<StorageInfo> items = getAllInventory(name);
		List<StorageInfo> seen = new ArrayList<>();
		for (Storage s : storage) {
			boolean isItemSeen = false;
			if (isCancelled) return;
			if (s == null) continue;//nothing here, move on
			long count;
			StorageInfo info = new StorageInfo((Inventory) s, api, name);
			if (!seen.contains(info)) {//haven't see this item
				seen.add(info);
				//item is already in the database, update id, so that correct item will get updated
				if (items.contains(info) && items.get(items.indexOf(info)).getCount() != info.getCount()) {
					isItemSeen = true;
					info.setId(items.get(items.indexOf(info)).getId());
				}
				items.remove(info);//remove this item, so it don't get removed
				count = info.getCount();
			} else {//already see this item, update count
				isItemSeen = true;
				StorageInfo old = seen.get(seen.indexOf(info));
				old.setCount(old.getCount() + info.getCount());
				info.setId(old.getId());
				count = old.getCount();//update count to new + old count
			}

			update(info, count, isItemSeen);
		}
		//remove all outdated storage item from database
		for (StorageInfo i : items) {
			if (isCancelled) return;
			inventoryDB.delete(i.getId());
		}
	}

	//get bank or inventory info
	private List<StorageInfo> getAllInventory(String name) {
		List<StorageInfo> result = inventoryDB.get(name);
		if (result == null) return new ArrayList<>();
		return result;
	}

	//update or add storage item
	private void update(StorageInfo info, long newCount, boolean isItemSeen) {
		if (isCancelled) return;
		//insert item if needed
		if (!isItemSeen && itemWrapper.get(info.getItemInfo().getId()) == null)
			itemWrapper.update(info.getItemInfo().getId());
		//insert skin if needed
		if (!isItemSeen && info.getSkinInfo().getId() != 0
				&& skinWrapper.get(info.getSkinInfo().getId()) == null)
			skinWrapper.update(info.getSkinInfo().getId());
		//update
		long result = inventoryDB.replace(info.getId(), info.getItemInfo().getId(),
				info.getCharacterName(), info.getApi(), newCount, info.getSkinInfo().getId(), info.getBinding(), info.getBoundTo());
		if (result >= 0) info.setId(result);
	}
}
