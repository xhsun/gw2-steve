package xhsun.gw2app.steve.backend.database.character;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

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
	public static final String TABLE_NAME = "storage";
	private static final String ID = "id";
	private static final String ITEM_ID = "item_id";
	private static final String CHARACTER_NAME = "name";
	private static final String ACCOUNT_KEY = "api";
	private static final String COUNT = "count";
	private static final String SKIN_ID = "skin_id";
	private static final String STATS_ID = "stats_id";
	private static final String BINDING = "binding";
	private static final String BOUND_TO = "bound";
	private static final String TYPE = "type";//0 - inventory, 1 - bank

	protected StorageDB(Context context) {
		super(context);
	}

	public static String createTable() {
		return "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
				ID + " INTEGER PRIMARY KEY," +
				ITEM_ID + " INTEGER NOT NULL," +
				CHARACTER_NAME + " TEXT NOT NULL," +
				ACCOUNT_KEY + " TEXT NOT NULL," +
				COUNT + " INTEGER NOT NULL CHECK(" + COUNT + " >= 0)," +
				SKIN_ID + " INTEGER DEFAULT -1," +
				STATS_ID + " INTEGER DEFAULT -1," +
				BINDING + " TEXT DEFAULT ''," +
				BOUND_TO + " TEXT DEFAULT ''," +
				TYPE + " INTEGER NOT NULL DEFAULT 1," +//default bank item
				"FOREIGN KEY (" + ACCOUNT_KEY + ") REFERENCES " + AccountDB.TABLE_NAME + "(" + AccountDB.API + ") ON DELETE CASCADE ON UPDATE CASCADE," +
				"FOREIGN KEY (" + CHARACTER_NAME + ") REFERENCES " + CharacterDB.TABLE_NAME + "(" + CharacterDB.NAME + ") ON DELETE CASCADE ON UPDATE CASCADE);";
	}

	/**
	 * create new storage in the database
	 *
	 * @param itemID  item id
	 * @param name    character name
	 * @param api     API key
	 * @param count   number of items
	 * @param skinID  skin id | -1 if not apply
	 * @param statsID stats id | -1 if not apply
	 * @param binding binding | null if no binding
	 * @param boundTo character name | empty if not apply
	 * @param isBank  true if item is in the bank | false if item is in character inventory
	 * @return true on success, false otherwise
	 */
	boolean create(long itemID, String name, String api, long count, long skinID, long statsID, Storage.Binding binding, String boundTo, boolean isBank) {
		Timber.i("Start insert storage entry for (%d, %s, %s)", itemID, name, api);
		return insert(TABLE_NAME, populateContent(itemID, name, api, count, skinID, statsID, binding, boundTo, isBank));
	}

	/**
	 * update storage info
	 *
	 * @param id      storage id
	 * @param count   count | -1 if no change
	 * @param skinID  skin id | -1 if no change
	 * @param statsID stats id | -1 if no change
	 * @param boundTo character name | empty if no change
	 * @param isBank  true if item is in the bank | false if item is in character inventory
	 * @return true on success, false otherwise
	 */
	boolean update(long id, long count, long skinID, long statsID, String boundTo, boolean isBank) {
		Timber.i("Start updating storage (%d)", id);
		String selection = ID + " = ?";
		String[] selectionArgs = {Long.toString(id)};
		ContentValues values = populateUpdate(count, skinID, statsID, boundTo, isBank);
		if (values == null) {
			Timber.i("Storage (%d) is already up to date", id);
			return false;
		}
		return update(TABLE_NAME, values, selection, selectionArgs);
	}

	/**
	 * delete given item from database
	 *
	 * @param id storage id
	 * @return true on success, false otherwise
	 */
	boolean delete(long id) {
		Timber.i("Start deleting storage (%d)", id);
		String selection = ID + " = ?";
		String[] selectionArgs = {Long.toString(id)};
		return delete(TABLE_NAME, selection, selectionArgs);
	}

	List<StorageInfo> getAll() {
		return __get(TABLE_NAME, "");
	}

	/**
	 * get all storage item bound to or stored by the given character
	 *
	 * @param name   character name
	 * @param isBank true if item is in the bank | false if item is in character inventory
	 * @return list of storage info
	 */
	List<StorageInfo> getAllByCharacter(String name, boolean isBank) {
		return __get(TABLE_NAME, " WHERE " + TYPE + " = " + ((isBank) ? 1 : 0) + " AND " + CHARACTER_NAME
				+ " = '" + name + "' OR " + BOUND_TO + " = '" + name + "'");
	}

	/**
	 * get all item that is in the given account
	 *
	 * @param api    API key
	 * @param isBank true if item is in the bank | false if item is in character inventory
	 * @return list of storage info
	 */
	List<StorageInfo> getAllByAPI(String api, boolean isBank) {
		return __get(TABLE_NAME, " WHERE " + TYPE + " = " + ((isBank) ? 1 : 0) + " AND " + ACCOUNT_KEY +
				" = '" + api + "'");
	}

	/**
	 * get all item by item id
	 *
	 * @param id     item id
	 * @param isBank true if item is in the bank | false if item is in character inventory
	 * @return list of storage info
	 */
	List<StorageInfo> getAllByItemID(long id, boolean isBank) {
		return __get(TABLE_NAME, " WHERE " + TYPE + " = " + ((isBank) ? 1 : 0) + " AND " + ITEM_ID + " = " + id);
	}

	/**
	 * get all by skin id
	 *
	 * @param id     skin id
	 * @param isBank true if item is in the bank | false if item is in character inventory
	 * @return list of storage info
	 */
	List<StorageInfo> getAllBySkinID(long id, boolean isBank) {
		return __get(TABLE_NAME, " WHERE " + TYPE + " = " + ((isBank) ? 1 : 0) + " AND " + SKIN_ID + " = " + id);
	}

	/**
	 * get all by status id
	 *
	 * @param id     item status id
	 * @param isBank true if item is in the bank | false if item is in character inventory
	 * @return list of storage info
	 */
	List<StorageInfo> getAllByStatsID(long id, boolean isBank) {
		return __get(TABLE_NAME, " WHERE " + TYPE + " = " + ((isBank) ? 1 : 0) + " AND " + STATS_ID + " = " + id);
	}

	@Override
	protected List<StorageInfo> __parseGet(Cursor cursor) {
		List<StorageInfo> storage = new ArrayList<>();
		if (cursor.moveToFirst())
			while (!cursor.isAfterLast()) {
				StorageInfo item = new StorageInfo();
				item.setId(cursor.getLong(cursor.getColumnIndex(ID)));
				item.setItemID(cursor.getLong(cursor.getColumnIndex(ITEM_ID)));
				item.setCharacterName(cursor.getString(cursor.getColumnIndex(CHARACTER_NAME)));
				item.setApi(cursor.getString(cursor.getColumnIndex(ACCOUNT_KEY)));
				item.setCount(cursor.getInt(cursor.getColumnIndex(COUNT)));
				item.setSkinID(cursor.getLong(cursor.getColumnIndex(SKIN_ID)));
				item.setStatsID(cursor.getLong(cursor.getColumnIndex(STATS_ID)));
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

	private ContentValues populateContent(long itemID, String name, String api, long count, long skinID, long statsID, Storage.Binding binding, String boundTo, boolean isBank) {
		ContentValues values = new ContentValues();
		values.put(ITEM_ID, itemID);
		values.put(CHARACTER_NAME, name);
		values.put(ACCOUNT_KEY, api);
		values.put(COUNT, count);
		values.put(SKIN_ID, skinID);
		values.put(STATS_ID, statsID);
		if (binding != null) {
			values.put(BINDING, binding.name());
			values.put(BOUND_TO, boundTo);
		}
		if (isBank) values.put(TYPE, 1);
		else values.put(TYPE, 0);
		return values;
	}

	private ContentValues populateUpdate(long count, long skinID, long statsID, String boundTo, Boolean isBank) {
		if (count < 0 && skinID < 0 && statsID < 0 && boundTo.equals("") && isBank == null) return null;
		ContentValues values = new ContentValues();
		if (count > 0) values.put(COUNT, count);
		if (skinID > 0) values.put(SKIN_ID, skinID);
		if (statsID > 0) values.put(STATS_ID, statsID);
		if (!boundTo.equals("")) values.put(BOUND_TO, boundTo);
		if (isBank != null) {
			if (isBank) values.put(TYPE, 1);
			else values.put(TYPE, 0);
		}
		return values;
	}
}
