package xhsun.gw2app.steve.backend.data.wrapper.common;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.annimon.stream.Stream;

import java.util.ArrayList;
import java.util.List;

import me.xhsun.guildwars2wrapper.model.v2.Item;
import timber.log.Timber;
import xhsun.gw2app.steve.backend.data.model.ItemModel;
import xhsun.gw2app.steve.backend.data.wrapper.Database;

/**
 * This handle all the database transactions for items
 *
 * @author xhsun
 * @since 2017-03-30
 */

public class ItemDB extends Database<ItemModel> {
	public static final String TABLE_NAME = "items";
	public static final String ID = "item_id";
	public static final String NAME = "item_name";
	public static final String CHAT_LINK = "item_chatLink";
	public static final String ICON = "item_icon";
	public static final String RARITY = "item_rarity";
	public static final String LEVEL = "item_level";
	public static final String DESCRIPTION = "item_description";

	public ItemDB(Context context) {
		super(context);
	}

	public static String createTable() {
		return "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
				ID + " INTEGER PRIMARY KEY NOT NULL," +
				NAME + " TEXT NOT NULL," +
				CHAT_LINK + " TEXT NOT NULL," +
				ICON + " TEXT NOT NULL," +
				RARITY + " TEXT NOT NULL," +
				LEVEL + " INTEGER NOT NULL," +
				DESCRIPTION + " TEXT DEFAULT '');";
	}

	/**
	 * Insert or update item entry
	 *
	 * @param id       item id
	 * @param name     item name
	 * @param chatLink chat link
	 * @param icon     icon
	 * @param rarity   rarity
	 * @param level    level required
	 * @param desc     description | empty if not apply
	 * @return true on success, false otherwise
	 */
	boolean replace(int id, String name, String chatLink, String icon,
	                Item.Rarity rarity, int level, String desc) {
		Timber.d("Start insert or replace item entry for %s", name);
		return replace(TABLE_NAME, populateValue(id, name, chatLink, icon, rarity, level, desc)) == 0;
	}

	/**
	 * bulk insert or update items into database
	 *
	 * @param data items to insert or update
	 */
	void bulkReplace(List<Item> data) {
		Timber.d("Start bulk insert item entries");
		List<ContentValues> values = new ArrayList<>();
		Stream.of(data).forEach(i -> values.add(populateValue(i.getId(), i.getName(), i.getChatLink(),
				i.getIcon(), i.getRarity(), i.getLevel(), i.getDescription())));
		bulkReplace(TABLE_NAME, values);
	}

	/**
	 * remove item from database
	 *
	 * @param id item id
	 * @return true on success, false otherwise
	 */
	boolean delete(int id) {
		Timber.d("Start deleting item (%d)", id);
		String selection = ID + " = ?";
		String[] selectionArgs = {Integer.toString(id)};
		return delete(TABLE_NAME, selection, selectionArgs);
	}

	/**
	 * all item in the database
	 *
	 * @return list of item info | empty if not find
	 */
	List<ItemModel> getAll() {
		return __get(TABLE_NAME, "");
	}

	/**
	 * get item base on id
	 *
	 * @param id item id
	 * @return item info | null if not find
	 */
	ItemModel get(int id) {
		List<ItemModel> list;
		if ((list = super.__get(TABLE_NAME, " WHERE " + ID + " = " + id)).isEmpty())
			return null;
		return list.get(0);
	}

	@Override
	protected List<ItemModel> __parseGet(Cursor cursor) {
		List<ItemModel> items = new ArrayList<>();
		if (cursor.moveToFirst())
			while (!cursor.isAfterLast()) {
				ItemModel item = new ItemModel(cursor.getInt(cursor.getColumnIndex(ID)));
				item.setName(cursor.getString(cursor.getColumnIndex(NAME)));
				item.setChatLink(cursor.getString(cursor.getColumnIndex(CHAT_LINK)));
				item.setIcon(cursor.getString(cursor.getColumnIndex(ICON)));
				try {
					item.setRarity(Item.Rarity.valueOf(cursor.getString(cursor.getColumnIndex(RARITY))));
				} catch (IllegalArgumentException e) {
					item.setRarity(Item.Rarity.Basic);
				}
				item.setLevel(cursor.getInt(cursor.getColumnIndex(LEVEL)));
				item.setDescription(cursor.getString(cursor.getColumnIndex(DESCRIPTION)));
				items.add(item);
				cursor.moveToNext();
			}
		return items;
	}

	private ContentValues populateValue(int id, String name, String chatLink, String icon,
	                                    Item.Rarity rarity, int level, String desc) {
		ContentValues values = new ContentValues();
		values.put(ID, id);
		values.put(NAME, name);
		values.put(CHAT_LINK, chatLink);
		values.put(ICON, icon);
		values.put(RARITY, rarity.name());
		values.put(LEVEL, level);
		if (desc != null && !desc.equals("")) values.put(DESCRIPTION, desc);
		return values;
	}
}
