package xhsun.gw2app.steve.backend.data.wrapper.storage;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import me.xhsun.guildwars2wrapper.GuildWars2;
import me.xhsun.guildwars2wrapper.SynchronousRequest;
import me.xhsun.guildwars2wrapper.error.GuildWars2Exception;
import timber.log.Timber;
import xhsun.gw2app.steve.backend.data.model.AccountModel;
import xhsun.gw2app.steve.backend.data.model.ItemModel;
import xhsun.gw2app.steve.backend.data.model.SkinModel;
import xhsun.gw2app.steve.backend.data.model.vault.item.InventoryItemModel;
import xhsun.gw2app.steve.backend.data.wrapper.account.AccountWrapper;
import xhsun.gw2app.steve.backend.data.wrapper.character.CharacterWrapper;
import xhsun.gw2app.steve.backend.data.wrapper.common.ItemWrapper;
import xhsun.gw2app.steve.backend.data.wrapper.common.SkinWrapper;

/**
 * For manipulate storage items
 *
 * @author xhsun
 * @since 2017-03-29
 */

public class InventoryWrapper extends StorageWrapper<InventoryItemModel, InventoryItemModel> {
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
	public List<InventoryItemModel> update(String key) throws GuildWars2Exception {
		String[] value = key.split("\n");
		String api = value[0], name = value[1];
		if (value.length != 2) return new ArrayList<>();
		Timber.d("Start updating character inventory info for %s", name);
		try {
			List<InventoryItemModel> original = get(name), inventory = new ArrayList<>();
			Set<InventoryItemModel> seen = new HashSet<>();

			Stream.of(request.getCharacterInventory(api, name))
					.flatMap(c -> Stream.of(c.getBags())).filterNot(b -> b == null)
					.flatMap(b -> Stream.of(b.getInventory())).filterNot(i -> i == null)
					.filterNot(i -> i.getCount() < 1)
					.forEach(i -> {
						InventoryItemModel current = new InventoryItemModel(api, name, i);
						if (!seen.contains(current)) {
							inventory.add(current);
							seen.add(current);
						} else {
							InventoryItemModel old = inventory.get(inventory.indexOf(current));
							old.setCount(old.getCount() + current.getCount());
						}
					});

			if (original.size() < 1) startInsert(inventory);
			else startUpdate(original, inventory);
		} catch (GuildWars2Exception e) {
			Timber.e(e, "Error occurred when trying to get inventory information for %s", name);
			switch (e.getErrorCode()) {
				case Server:
				case Limit:
				case Network:
					throw e;
				case Key://mark account invalid and remove character from database
					accountWrapper.markInvalid(new AccountModel(api));
				case Character://remove character from database
					characterWrapper.delete(name);
			}
		}
		return get(name);
	}

	@Override
	protected void checkBaseItem(List<InventoryItemModel> data) {
		List<Integer> oItem = Stream.of(itemWrapper.getAll()).map(ItemModel::getId).collect(Collectors.toList()),
				oSkin = Stream.of(skinWrapper.getAll()).map(SkinModel::getId).collect(Collectors.toList());

		itemWrapper.bulkInsert(Stream.of(data).filterNot(i -> oItem.contains(i.getItemModel().getId()))
				.map(i -> i.getItemModel().getId()).mapToInt(Integer::intValue).toArray());

		skinWrapper.bulkInsert(Stream.of(data).filter(i -> i.getSkinModel() != null)
				.filterNot(i -> oSkin.contains(i.getSkinModel().getId()))
				.map(i -> i.getSkinModel().getId()).mapToInt(Integer::intValue).toArray());
	}

	@Override
	protected void checkOriginal(InventoryItemModel old, InventoryItemModel current) {
		updateIfDifferent(old, current);
	}
}
