package xhsun.gw2app.steve.backend.database.common;

import java.util.List;

import me.xhsun.guildwars2wrapper.GuildWars2;
import me.xhsun.guildwars2wrapper.SynchronousRequest;
import me.xhsun.guildwars2wrapper.error.GuildWars2Exception;
import me.xhsun.guildwars2wrapper.model.v2.Item;
import xhsun.gw2app.steve.backend.data.ItemData;

/**
 * For manipulate items
 *
 * @author xhsun
 * @since 2017-03-30
 */

public class ItemWrapper {
	private SynchronousRequest request;
	private ItemDB itemDB;

	public ItemWrapper(GuildWars2 wrapper, ItemDB itemDB) {
		request = wrapper.getSynchronous();
		this.itemDB = itemDB;
	}

	/**
	 * add if the item is not in database | update if the item is already in the database
	 *
	 * @param id item id
	 * @return item info on success | null otherwise
	 */
	public ItemData update(int id) {
		try {
			List<Item> origin = request.getItemInfo(new int[]{id});
			if (origin.size() < 1) return null;

			Item item = origin.get(0);
			if (itemDB.replace(item.getId(), item.getName(), item.getChatLink(),
					item.getIcon(), item.getRarity(), item.getLevel(), item.getDescription()))
				return new ItemData(item);
		} catch (GuildWars2Exception ignored) {
		}
		return null;
	}

	/**
	 * remove item from database
	 *
	 * @param id item id
	 */
	public void delete(int id) {
		itemDB.delete(id);
	}

	/**
	 * get all item that is in the database
	 *
	 * @return list of all item info | empty if not find
	 */
	public List<ItemData> getAll() {
		return itemDB.getAll();
	}

	/**
	 * get item info
	 *
	 * @param id item if
	 * @return item info | null if not find
	 */
	public ItemData get(int id) {
		return itemDB.get(id);
	}
}
