package xhsun.gw2app.steve.backend.database.common;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;
import xhsun.gw2api.guildwars2.model.Item;
import xhsun.gw2app.steve.backend.database.Database;

/**
 * This handle all the database transactions for items
 *
 * @author xhsun
 * @since 2017-03-30
 */

public class ItemDB extends Database<ItemInfo> {
	public static final String TABLE_NAME = "items";
	public static final String ID = "id";
	private static final String NAME = "name";
	private static final String CHAT_LINK = "chatLink";
	private static final String ICON = "icon";
	private static final String RARITY = "rarity";
	private static final String LEVEL = "level";
	private static final String DESCRIPTION = "description";

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
	boolean replace(long id, String name, String chatLink, String icon,
	                Item.Rarity rarity, int level, String desc) {
		Timber.i("Start insert or replace item entry for %s", name);
		return replace(TABLE_NAME, populateValue(id, name, chatLink, icon, rarity, level, desc)) == 0;
	}

	/**
	 * remove item from database
	 *
	 * @param id item id
	 * @return true on success, false otherwise
	 */
	boolean delete(long id) {
		Timber.i("Start deleting currency (%d)", id);
		String selection = ID + " = ?";
		String[] selectionArgs = {Long.toString(id)};
		return delete(TABLE_NAME, selection, selectionArgs);
	}

	/**
	 * all item in the database
	 *
	 * @return list of item info | empty if not find
	 */
	List<ItemInfo> getAll() {
		return __get(TABLE_NAME, "");
	}

	/**
	 * get item base on id
	 *
	 * @param id item id
	 * @return item info | null if not find
	 */
	ItemInfo get(long id) {
		List<ItemInfo> list;
		if ((list = super.__get(TABLE_NAME, " WHERE " + ID + " = " + id)).isEmpty())
			return null;
		return list.get(0);
	}

	@Override
	protected List<ItemInfo> __parseGet(Cursor cursor) {
		List<ItemInfo> items = new ArrayList<>();
		if (cursor.moveToFirst())
			while (!cursor.isAfterLast()) {
				ItemInfo item = new ItemInfo(cursor.getLong(cursor.getColumnIndex(ID)));
				item.setName(cursor.getString(cursor.getColumnIndex(NAME)));
				item.setChatLink(cursor.getString(cursor.getColumnIndex(CHAT_LINK)));
				item.setIcon(cursor.getString(cursor.getColumnIndex(ICON)));
				item.setRarity(Item.Rarity.valueOf(cursor.getString(cursor.getColumnIndex(RARITY))));
				item.setLevel(cursor.getInt(cursor.getColumnIndex(LEVEL)));
				item.setDescription(cursor.getString(cursor.getColumnIndex(DESCRIPTION)));
				items.add(item);
				cursor.moveToNext();
			}
		return items;
	}

	private ContentValues populateValue(long id, String name, String chatLink, String icon,
	                                    Item.Rarity rarity, int level, String desc) {
		ContentValues values = new ContentValues();
		values.put(ID, id);
		values.put(NAME, name);
		values.put(CHAT_LINK, chatLink);
		values.put(ICON, icon);
		values.put(RARITY, rarity.name());
		values.put(LEVEL, level);
		if (desc != null && !desc.equals("")) values.put(DESCRIPTION, id);
		return values;
	}
}
