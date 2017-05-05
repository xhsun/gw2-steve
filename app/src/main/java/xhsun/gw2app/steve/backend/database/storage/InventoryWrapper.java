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

public class InventoryWrapper extends StorageWrapper {
	private GuildWars2 wrapper;
	private InventoryDB inventoryDB;
	private AccountWrapper accountWrapper;
	private CharacterWrapper characterWrapper;

	@Inject
	public InventoryWrapper(GuildWars2 wrapper, AccountWrapper account,
	                        CharacterWrapper characterWrapper, ItemWrapper itemWrapper,
	                        SkinWrapper skinWrapper, InventoryDB inventory) {
		super(itemWrapper, skinWrapper, inventory);
		this.inventoryDB = inventory;
		this.wrapper = wrapper;
		this.characterWrapper = characterWrapper;
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
	public List<StorageInfo> update(CharacterInfo character) throws GuildWars2Exception {
		final String api = character.getApi();
		final String name = character.getName();
		Timber.i("Start updating character inventory info for %s", name);
		try {
			CharacterInventory stuff = wrapper.getCharacterInventory(api, name);
			List<Inventory> inventory = new ArrayList<>();
			for (Bag bag : stuff.getBags()) {
				if (isCancelled) break;
				if (bag == null) continue;
				inventory.addAll(bag.getInventory());
			}
			_update(inventory, api, name);
		} catch (GuildWars2Exception e) {
			Timber.e(e, "Error occurred when trying to get inventory information for %s", name);
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
		return get(name);
	}

	private void _update(List<Inventory> storage, String api, String name) {
		List<StorageInfo> items = get(name);
		List<StorageInfo> seen = new ArrayList<>();
		for (Inventory s : storage) {
			if (isCancelled) return;
			if (s == null) continue;//nothing here, move on
			updateStorage(items, seen, new StorageInfo(s, api, name));
		}
		//remove all outdated storage item from database
		for (StorageInfo i : items) {
			if (isCancelled) return;
			inventoryDB.delete(i.getId());
		}
	}

	//get bank or inventory info
	private List<StorageInfo> get(String name) {
		return inventoryDB.get(name);
	}
}
