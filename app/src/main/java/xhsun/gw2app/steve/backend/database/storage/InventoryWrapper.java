package xhsun.gw2app.steve.backend.database.storage;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import me.xhsun.guildwars2wrapper.GuildWars2;
import me.xhsun.guildwars2wrapper.SynchronousRequest;
import me.xhsun.guildwars2wrapper.error.GuildWars2Exception;
import me.xhsun.guildwars2wrapper.model.util.Inventory;
import timber.log.Timber;
import xhsun.gw2app.steve.backend.data.AccountData;
import xhsun.gw2app.steve.backend.data.vault.item.Countable;
import xhsun.gw2app.steve.backend.data.vault.item.InventoryItemData;
import xhsun.gw2app.steve.backend.database.account.AccountWrapper;
import xhsun.gw2app.steve.backend.database.character.CharacterWrapper;
import xhsun.gw2app.steve.backend.database.common.ItemWrapper;
import xhsun.gw2app.steve.backend.database.common.SkinWrapper;

/**
 * For manipulate storage items
 *
 * @author xhsun
 * @since 2017-03-29
 */

public class InventoryWrapper extends StorageWrapper<InventoryItemData, InventoryItemData> {
	private SynchronousRequest request;
	private ItemWrapper itemWrapper;
	private SkinWrapper skinWrapper;
	private AccountWrapper accountWrapper;
	private CharacterWrapper characterWrapper;

	@Inject
	public InventoryWrapper(GuildWars2 wrapper, AccountWrapper account,
	                        CharacterWrapper characterWrapper, ItemWrapper itemWrapper,
	                        SkinWrapper skinWrapper, InventoryDB inventory) {
		super(inventory);
		request = wrapper.getSynchronous();
		this.characterWrapper = characterWrapper;
		accountWrapper = account;
		this.itemWrapper = itemWrapper;
		this.skinWrapper = skinWrapper;
	}

	/**
	 * update inventory info for given character
	 *
	 * @param key that should contain API key and character name, separated by \n
	 * @return inventory info for this character | empty if there is nothing
	 * @throws GuildWars2Exception error when interacting with server
	 */
	public List<InventoryItemData> update(String key) throws GuildWars2Exception {
		String[] value = key.split("\n");
		if (value.length != 2) return new ArrayList<>();
		Timber.d("Start updating character inventory info for %s", value[1]);
		try {
			startUpdate(value[0], value[1], Stream.of(request.getCharacterInventory(value[0], value[1]))
					.flatMap(c -> Stream.of(c.getBags())).filterNot(b -> b == null)
					.flatMap(b -> Stream.of(b.getInventory())).filterNot(i -> i == null)
					.collect(Collectors.toList()), get(value[1]));
		} catch (GuildWars2Exception e) {
			Timber.e(e, "Error occurred when trying to get inventory information for %s", value[1]);
			switch (e.getErrorCode()) {
				case Server:
				case Limit:
				case Network:
					throw e;
				case Key://mark account invalid and remove character from database
					accountWrapper.markInvalid(new AccountData(value[0]));
				case Character://remove character from database
					characterWrapper.delete(value[1]);
			}
		}
		return get(value[1]);
	}

	private void startUpdate(String api, String name, List<Inventory> inventory, List<InventoryItemData> original) {
		List<Countable> known = new ArrayList<>(original);
		List<Countable> seen = new ArrayList<>();
		for (Inventory s : inventory) {
			if (isCancelled) return;
			if (s.getCount() == 0) continue;//nothing here, move on
			updateRecord(known, seen, new InventoryItemData(api, name, s));
		}
		//remove all outdated storage item from database
		for (Countable i : known) {
			if (isCancelled) return;
			delete((InventoryItemData) i);
		}
	}

	@Override
	protected void updateDatabase(InventoryItemData info, boolean isItemSeen) {
		if (isCancelled) return;
		//insert item if needed
		if (!isItemSeen && itemWrapper.get(info.getItemData().getId()) == null)
			itemWrapper.update(info.getItemData().getId());
		//insert skin if needed
		if (!isItemSeen && info.getSkinData() != null &&
				info.getSkinData().getId() != 0 && skinWrapper.get(info.getSkinData().getId()) == null)
			skinWrapper.update(info.getSkinData().getId());
		replace(info);//update
	}
}
