package xhsun.gw2app.steve.backend.database.character;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;
import xhsun.gw2api.guildwars2.model.util.Storage;
import xhsun.gw2app.steve.backend.database.Database;
import xhsun.gw2app.steve.backend.database.account.AccountDB;
import xhsun.gw2app.steve.backend.database.common.ItemDB;
import xhsun.gw2app.steve.backend.database.common.ItemInfo;

/**
 * This class handles all transaction for storage table
 *
 * @author xhsun
 * @since 2017-03-29
 */

public class StorageDB extends Database<StorageInfo> {
	public static final String INVENTORY_TABLE_NAME = "inventory";
	public static final String BANK_TABLE_NAME = "storage";
	private static final String ID = "id";
	private static final String ITEM_ID = "item_id";
	private static final String CHARACTER_NAME = "name";
	private static final String ACCOUNT_KEY = "api";
	private static final String CATEGORY_NAME = "category_name";
	private static final String COUNT = "count";
	private static final String BINDING = "binding";
	private static final String BOUND_TO = "bound";

	@Inject
	public StorageDB(Context context) {
		super(context);
	}

	public static String createInventoryTable() {
		return "CREATE TABLE IF NOT EXISTS " + INVENTORY_TABLE_NAME + " (" +
				ID + " INTEGER PRIMARY KEY," +
				ITEM_ID + " INTEGER NOT NULL," +
				CHARACTER_NAME + " TEXT," +
				ACCOUNT_KEY + " TEXT NOT NULL," +
				COUNT + " INTEGER NOT NULL CHECK(" + COUNT + " >= 0)," +
				BINDING + " TEXT DEFAULT ''," +
				BOUND_TO + " TEXT DEFAULT ''," +
				"FOREIGN KEY (" + ACCOUNT_KEY + ") REFERENCES " + AccountDB.TABLE_NAME + "(" + AccountDB.API + ") ON DELETE CASCADE ON UPDATE CASCADE," +
				"FOREIGN KEY (" + ITEM_ID + ") REFERENCES " + ItemDB.TABLE_NAME + "(" + ItemDB.ID + ") ON DELETE CASCADE ON UPDATE CASCADE," +
				"FOREIGN KEY (" + CHARACTER_NAME + ") REFERENCES " + CharacterDB.TABLE_NAME + "(" + CharacterDB.NAME + ") ON DELETE CASCADE ON UPDATE CASCADE);";
	}

	public static String createStorageTable() {
		return "CREATE TABLE IF NOT EXISTS " + BANK_TABLE_NAME + " (" +
				ID + " INTEGER PRIMARY KEY," +
				ITEM_ID + " INTEGER NOT NULL," +
				ACCOUNT_KEY + " TEXT NOT NULL," +
				CATEGORY_NAME + " TEXT DEFAULT ''," +
				COUNT + " INTEGER NOT NULL CHECK(" + COUNT + " >= 0)," +
				BINDING + " TEXT DEFAULT ''," +
				BOUND_TO + " TEXT DEFAULT ''," +
				"FOREIGN KEY (" + ITEM_ID + ") REFERENCES " + ItemDB.TABLE_NAME + "(" + ItemDB.ID + ") ON DELETE CASCADE ON UPDATE CASCADE," +
				"FOREIGN KEY (" + ACCOUNT_KEY + ") REFERENCES " + AccountDB.TABLE_NAME + "(" + AccountDB.API + ") ON DELETE CASCADE ON UPDATE CASCADE);";
	}

	/**
	 * replace or insert the entry to database
	 * @param itemID  item id
	 * @param name    character name | empty if not apply
	 * @param api     API key
	 * @param count   number of items
	 * @param category  category name | empty if not apply
	 * @param binding binding | null if no binding
	 * @param boundTo character name | empty if not apply
	 * @param isBank  true if item is in the bank | false if item is in character inventory
	 * @return id on success, -1 on error
	 */
	long replace(long itemID, String name, String api, long count, String category,
	             Storage.Binding binding, String boundTo, boolean isBank) {
		Timber.i("Start insert or update storage entry for (%d, %s, %s)", itemID, name, api);
		if (isBank)
			return replaceAndReturn(BANK_TABLE_NAME, populateContent(itemID, name, api, count, category, binding, boundTo));
		return replaceAndReturn(INVENTORY_TABLE_NAME, populateContent(itemID, name, api, count, category, binding, boundTo));
	}

	/**
	 * delete given item from database
	 *
	 * @param id storage id
	 * @return true on success, false otherwise
	 */
	boolean delete(long id, boolean isBank) {
		Timber.i("Start deleting storage (%d)", id);
		String selection = ID + " = ?";
		String[] selectionArgs = {Long.toString(id)};
		if (isBank) return delete(BANK_TABLE_NAME, selection, selectionArgs);
		else return delete(INVENTORY_TABLE_NAME, selection, selectionArgs);
	}

//	List<StorageInfo> getAll(boolean isBank) {
//		if (isBank) return __get(BANK_TABLE_NAME, "");
//		else return __get(INVENTORY_TABLE_NAME, "");
//	}

	/**
	 * get all bank item that is in the given account
	 *
	 * @param api    API key
	 * @return list of storage info
	 */
	List<StorageInfo> getAllByAPI(String api) {
		return __get(BANK_TABLE_NAME, " WHERE " + ACCOUNT_KEY + " = '" + api + "'");
	}

	/**
	 * get all storage item stored by the given character
	 *
	 * @param name   character name
	 * @return list of storage info
	 */
	List<StorageInfo> getAllByHolder(String name) {
		return __get(INVENTORY_TABLE_NAME, " WHERE " + CHARACTER_NAME + " = '" + name + "'");
	}

//	/**
//	 * get all item by item id
//	 *
//	 * @param id     item id
//	 * @param isBank true if item is in the bank | false if item is in character inventory
//	 * @return list of storage info
//	 */
//	List<StorageInfo> getAllByItemID(long id, boolean isBank) {
//		if (isBank) return __get(BANK_TABLE_NAME, " WHERE " + ITEM_ID + " = " + id);
//		else return __get(INVENTORY_TABLE_NAME, " WHERE " + ITEM_ID + " = " + id);
//	}

	@Override
	protected List<StorageInfo> __parseGet(Cursor cursor) {
		List<StorageInfo> storage = new ArrayList<>();
		if (cursor.moveToFirst())
			while (!cursor.isAfterLast()) {
				int index;
				StorageInfo item = new StorageInfo();
				item.setId(cursor.getLong(cursor.getColumnIndex(ID)));
				item.setItemInfo(new ItemInfo(cursor.getLong(cursor.getColumnIndex(ITEM_ID))));
				item.setApi(cursor.getString(cursor.getColumnIndex(ACCOUNT_KEY)));
				item.setCount(cursor.getInt(cursor.getColumnIndex(COUNT)));
				//only for inventory
				if ((index = cursor.getColumnIndex(CHARACTER_NAME)) != -1)
					item.setCharacterName(cursor.getString(index));
				//only for storage
				if ((index = cursor.getColumnIndex(CATEGORY_NAME)) != -1)
					item.setCategoryName(cursor.getString(index));
				//check if there is a bind for this item
				String binding = cursor.getString(cursor.getColumnIndex(BINDING));
				if (binding.equals(""))
					item.setBinding(null);
				else item.setBinding(Storage.Binding.valueOf(binding));

				item.setBoundTo(cursor.getString(cursor.getColumnIndex(BOUND_TO)));
				storage.add(item);
				cursor.moveToNext();
			}
		return storage;
	}

	private ContentValues populateContent(long itemID, String name, String api,
	                                      long count, String category, Storage.Binding binding,
	                                      String boundTo) {
		ContentValues values = new ContentValues();
		values.put(ITEM_ID, itemID);
		if (!name.equals("")) values.put(CHARACTER_NAME, name);
		values.put(ACCOUNT_KEY, api);
		values.put(COUNT, count);
		if (!category.equals("")) values.put(CATEGORY_NAME, category);
		if (binding != null) {
			values.put(BINDING, binding.name());
			values.put(BOUND_TO, boundTo);
		}
		return values;
	}
}
