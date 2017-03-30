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
	private static final String ITEM_NAME = "item_name";
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
				ITEM_NAME + " TEXT NOT NULL," +
				BINDING + " TEXT DEFAULT ''," +
				BOUND_TO + " TEXT DEFAULT ''," +
				"FOREIGN KEY (" + ACCOUNT_KEY + ") REFERENCES " + AccountDB.TABLE_NAME + "(" + AccountDB.API + ") ON DELETE CASCADE ON UPDATE CASCADE," +
				"FOREIGN KEY (" + CHARACTER_NAME + ") REFERENCES " + CharacterDB.TABLE_NAME + "(" + CharacterDB.NAME + ") ON DELETE CASCADE ON UPDATE CASCADE);";
	}

	public static String createStorageTable() {
		return "CREATE TABLE IF NOT EXISTS " + BANK_TABLE_NAME + " (" +
				ID + " INTEGER PRIMARY KEY," +
				ITEM_ID + " INTEGER NOT NULL," +
				ACCOUNT_KEY + " TEXT NOT NULL," +
				COUNT + " INTEGER NOT NULL CHECK(" + COUNT + " >= 0)," +
				ITEM_NAME + " TEXT NOT NULL," +
				CATEGORY_NAME + " TEXT DEFAULT ''," +
				BINDING + " TEXT DEFAULT ''," +
				BOUND_TO + " TEXT DEFAULT ''," +
				"FOREIGN KEY (" + ACCOUNT_KEY + ") REFERENCES " + AccountDB.TABLE_NAME + "(" + AccountDB.API + ") ON DELETE CASCADE ON UPDATE CASCADE);";
	}

	/**
	 * create new storage in the database
	 *
	 * @param itemID  item id
	 * @param name    character name | empty if not apply
	 * @param api     API key
	 * @param itemName item name
	 * @param count   number of items
	 * @param category  category name | empty if not apply
	 * @param binding binding | null if no binding
	 * @param boundTo character name | empty if not apply
	 * @param isBank  true if item is in the bank | false if item is in character inventory
	 * @return true on success, false otherwise
	 */
	long create(long itemID, String name, String api, String itemName, long count, String category,
	            Storage.Binding binding, String boundTo, boolean isBank) {
		Timber.i("Start insert storage entry for (%d, %s, %s)", itemID, name, api);
		if (isBank)
			return insert(BANK_TABLE_NAME, populateContent(itemID, name, api, itemName, count, category, binding, boundTo));
		else
			return insert(INVENTORY_TABLE_NAME, populateContent(itemID, name, api, itemName, count, category, binding, boundTo));
	}

	/**
	 * update storage info
	 *
	 * @param id storage id
	 * @param count count | -1 if no change
	 * @param boundTo character name | empty if no change
	 * @param isBank  true if item is in the bank | false if item is in character inventory
	 * @return true on success, false otherwise
	 */
	boolean update(long id, long count, String boundTo, boolean isBank) {
		Timber.i("Start updating storage (%d)", id);
		String selection = ID + " = ?";
		String[] selectionArgs = {Long.toString(id)};
		ContentValues values = populateUpdate(count, (boundTo == null) ? "" : boundTo);
		if (values == null) {
			Timber.i("Storage (%d) is already up to date", id);
			return false;
		}
		if (isBank) return update(BANK_TABLE_NAME, values, selection, selectionArgs);
		else return update(INVENTORY_TABLE_NAME, values, selection, selectionArgs);
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

	List<StorageInfo> getAll(boolean isBank) {
		if (isBank) return __get(BANK_TABLE_NAME, "");
		else return __get(INVENTORY_TABLE_NAME, "");
	}

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

	/**
	 * get all item by item id
	 *
	 * @param id     item id
	 * @param isBank true if item is in the bank | false if item is in character inventory
	 * @return list of storage info
	 */
	List<StorageInfo> getAllByItemID(long id, boolean isBank) {
		if (isBank) return __get(BANK_TABLE_NAME, " WHERE " + ITEM_ID + " = " + id);
		else return __get(INVENTORY_TABLE_NAME, " WHERE " + ITEM_ID + " = " + id);
	}

	/**
	 * search the database to find item that match the keyword
	 *
	 * @param keyword   keywork for search
	 * @param isBank true if item is in the bank | false if item is in character inventory
	 * @return list of storage info
	 */
	List<StorageInfo> search(String keyword, boolean isBank) {
		String flag = " WHERE " + BOUND_TO + " = '" + keyword + "'" + " OR " + ITEM_NAME + " = '" + keyword + "'";
		if (isBank) return __get(BANK_TABLE_NAME, flag);
		else return __get(INVENTORY_TABLE_NAME, flag);
	}

	@Override
	protected List<StorageInfo> __parseGet(Cursor cursor) {
		List<StorageInfo> storage = new ArrayList<>();
		if (cursor.moveToFirst())
			while (!cursor.isAfterLast()) {
				int index;
				StorageInfo item = new StorageInfo();
				item.setId(cursor.getLong(cursor.getColumnIndex(ID)));
				item.setItemID(cursor.getLong(cursor.getColumnIndex(ITEM_ID)));
				item.setItemName(cursor.getString(cursor.getColumnIndex(ITEM_NAME)));
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

	private ContentValues populateContent(long itemID, String name, String api, String itemName,
	                                      long count, String category, Storage.Binding binding,
	                                      String boundTo) {
		ContentValues values = new ContentValues();
		values.put(ITEM_ID, itemID);
		if (!name.equals("")) values.put(CHARACTER_NAME, name);
		values.put(ACCOUNT_KEY, api);
		values.put(ITEM_NAME, itemName);
		values.put(COUNT, count);
		if (!category.equals("")) values.put(CATEGORY_NAME, category);
		if (binding != null) {
			values.put(BINDING, binding.name());
			values.put(BOUND_TO, boundTo);
		}
		return values;
	}

	private ContentValues populateUpdate(long count, String boundTo) {
		if (count < 0 && boundTo.equals(""))
			return null;
		ContentValues values = new ContentValues();
		if (count > 0) values.put(COUNT, count);
		if (!boundTo.equals("")) values.put(BOUND_TO, boundTo);
		return values;
	}
}
