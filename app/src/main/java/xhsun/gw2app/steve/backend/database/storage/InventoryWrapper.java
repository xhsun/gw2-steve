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
import xhsun.gw2app.steve.backend.data.AccountInfo;
import xhsun.gw2app.steve.backend.data.StorageInfo;
import xhsun.gw2app.steve.backend.database.account.AccountWrapper;
import xhsun.gw2app.steve.backend.database.character.CharacterWrapper;
import xhsun.gw2app.steve.backend.database.common.ItemWrapper;
import xhsun.gw2app.steve.backend.database.common.SkinWrapper;
import xhsun.gw2app.steve.backend.util.vault.VaultType;

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
		super(itemWrapper, skinWrapper, inventory, VaultType.INVENTORY);
		this.inventoryDB = inventory;
		this.wrapper = wrapper;
		this.characterWrapper = characterWrapper;
		accountWrapper = account;
	}

	/**
	 * update inventory info for given character
	 *
	 * @param key that should contain API key and character name, separated by \n
	 * @return inventory info for this character | empty if there is nothing
	 * @throws GuildWars2Exception error when interacting with server
	 */
	public List<StorageInfo> update(String key) throws GuildWars2Exception {
		String[] value = key.split("\n");
		if (value.length != 2) return new ArrayList<>();
		Timber.d("Start updating character inventory info for %s", value[1]);
		try {
			CharacterInventory stuff = wrapper.getCharacterInventory(value[0], value[1]);
			List<Inventory> inventory = new ArrayList<>();
			for (Bag bag : stuff.getBags()) {
				if (isCancelled) break;
				if (bag == null) continue;
				inventory.addAll(bag.getInventory());
			}
			_update(inventory, value[0], value[1]);
		} catch (GuildWars2Exception e) {
			Timber.e(e, "Error occurred when trying to get inventory information for %s", value[1]);
			switch (e.getErrorCode()) {
				case Server:
				case Limit:
				case Network:
					throw e;
				case Key://mark account invalid and remove character from database
					accountWrapper.markInvalid(new AccountInfo(value[0]));
				case Character://remove character from database
					characterWrapper.delete(value[1]);
			}
		}
		return get(value[1]);
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
}
