package xhsun.gw2app.steve.backend.data.wrapper.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.xhsun.guildwars2wrapper.GuildWars2;
import me.xhsun.guildwars2wrapper.SynchronousRequest;
import me.xhsun.guildwars2wrapper.error.GuildWars2Exception;
import me.xhsun.guildwars2wrapper.model.v2.Item;
import xhsun.gw2app.steve.backend.data.model.ItemModel;

import static xhsun.gw2app.steve.backend.util.Utility.ID_LIMIT;

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
	 */
	public void update(int id) {
		try {
			List<Item> origin = request.getItemInfo(new int[]{id});
			if (origin.size() < 1) return;

			Item item = origin.get(0);
			itemDB.replace(item.getId(), item.getName(), item.getChatLink(),
					item.getIcon(), item.getRarity(), item.getLevel(), item.getDescription());
		} catch (GuildWars2Exception ignored) {
		}
	}

	public void bulkInsert(int[] ids) {
		int size = ids.length;
		if (size < 1) return;

		List<Item> origin = new ArrayList<>();
		try {
			if (size <= ID_LIMIT) origin = request.getItemInfo(ids);
			else {
				int count = 0, limit = (size / ID_LIMIT) + 1;
				for (int i = 1; i <= limit; i++) {
					int[] newIds = Arrays.copyOfRange(ids, ID_LIMIT * (count++), (i == limit) ? size : i * ID_LIMIT);
					origin.addAll(request.getItemInfo(newIds));
				}
			}
			if (origin.size() >= 1) itemDB.bulkReplace(origin);
		} catch (GuildWars2Exception ignored) {
		}
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
	public List<ItemModel> getAll() {
		return itemDB.getAll();
	}

	/**
	 * get item info
	 *
	 * @param id item if
	 * @return item info | null if not find
	 */
	public ItemModel get(int id) {
		return itemDB.get(id);
	}
}
