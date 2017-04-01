package xhsun.gw2app.steve.backend.database.common;

import java.util.List;

import xhsun.gw2api.guildwars2.GuildWars2;
import xhsun.gw2api.guildwars2.err.GuildWars2Exception;
import xhsun.gw2api.guildwars2.model.Item;

/**
 * For manipulate items
 *
 * @author xhsun
 * @since 2017-03-30
 */

public class ItemWrapper {
	private GuildWars2 wrapper;
	private ItemDB itemDB;

	public ItemWrapper(GuildWars2 wrapper, ItemDB itemDB) {
		this.wrapper = wrapper;
		this.itemDB = itemDB;
	}

	/**
	 * add if the item is not in database | update if the item is already in the database
	 *
	 * @param id item id
	 * @return item info on success | null otherwise
	 */
	public ItemInfo updateOrAdd(long id) {
		try {
			List<Item> origin = wrapper.getItemInfo(new long[]{id});
			if (origin.size() < 1) return null;

			Item item = origin.get(0);
			if (itemDB.replace(item.getId(), item.getName(), item.getChat_link(),
					item.getIcon(), item.getRarity(), item.getLevel(), item.getDescription()))
				return new ItemInfo(item);
		} catch (GuildWars2Exception ignored) {
		}
		return null;
	}

	/**
	 * remove item from database
	 *
	 * @param id item id
	 */
	public void delete(long id) {
		itemDB.delete(id);
	}

	/**
	 * get all item that is in the database
	 *
	 * @return list of all item info | empty if not find
	 */
	public List<ItemInfo> getAll() {
		return itemDB.getAll();
	}

	/**
	 * get item info
	 *
	 * @param id item if
	 * @return item info | null if not find
	 */
	public ItemInfo get(long id) {
		return itemDB.get(id);
	}
}
