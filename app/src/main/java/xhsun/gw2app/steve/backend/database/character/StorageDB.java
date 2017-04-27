package xhsun.gw2app.steve.backend.database.character;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;
import xhsun.gw2api.guildwars2.model.Item;
import xhsun.gw2api.guildwars2.model.util.Storage;
import xhsun.gw2app.steve.backend.database.Database;
import xhsun.gw2app.steve.backend.database.account.AccountDB;
import xhsun.gw2app.steve.backend.database.account.AccountInfo;
import xhsun.gw2app.steve.backend.database.common.ItemDB;
import xhsun.gw2app.steve.backend.database.common.ItemInfo;

/**
 * This class handles all transaction for storage table
 *
 * @author xhsun
 * @since 2017-03-29
 */

public class StorageDB extends Database<AccountInfo> {
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

	private String itemID = "item_id";
	private String itemName = "item_name";
	private String storageID = "storage_id";
	private String charName = "character_name";

	@Inject
	public StorageDB(Context context) {
		super(context);
	}

	public static String createInventoryTable() {
		return "CREATE TABLE IF NOT EXISTS " + INVENTORY_TABLE_NAME + " (" +
				ID + " INTEGER PRIMARY KEY," +
				ITEM_ID + " INTEGER NOT NULL," +
				CHARACTER_NAME + " TEXT NOT NULL," +
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
	 * @param id database id | -1 if don't know
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
	long replace(long id, long itemID, String name, String api, long count, String category,
	             Storage.Binding binding, String boundTo, boolean isBank) {
		Timber.i("Start insert or update storage entry for (%d, %s, %s)", itemID, name, api);
		if (isBank)
			return replaceAndReturn(BANK_TABLE_NAME, populateContent(id, itemID, name, api, count, category, binding, boundTo));
		return replaceAndReturn(INVENTORY_TABLE_NAME, populateContent(id, itemID, name, api, count, category, binding, boundTo));
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
	 * @return account info that contains bank info
	 */
	AccountInfo getBank(String api) {
		List<AccountInfo> list;
		if ((list = _get(BANK_TABLE_NAME, " WHERE s." + ACCOUNT_KEY + " = '" + api + "'")).isEmpty())
			return null;
		return list.get(0);
	}

	/**
	 * get all bank info for all known accounts
	 * @return list of accounts
	 */
	List<AccountInfo> getAllBank() {
		return _get(BANK_TABLE_NAME, "");
	}

	/**
	 * get all inventory info for given character
	 *
	 * @param name character name
	 * @return account info that contains all character inventory info
	 */
	AccountInfo getInventory(String name) {
		List<AccountInfo> list;
		if ((list = _get(INVENTORY_TABLE_NAME, " WHERE " + charName + " = '" + name + "'")).isEmpty())
			return null;
		return list.get(0);
	}

	/**
	 * get all inventory info for all known accounts
	 *
	 * @return list of accounts
	 */
	List<AccountInfo> getAllInventory() {
		return _get(INVENTORY_TABLE_NAME, "");
	}

	//custom get that get both item info and storage info
	private List<AccountInfo> _get(String table, String flags) {
		String query = "SELECT " +
				"s." + ID + " AS " + storageID + ", " +
				"s." + ((table.equals(INVENTORY_TABLE_NAME))
				? (CHARACTER_NAME + " AS " + charName + ", ") : (CATEGORY_NAME + ", ")) +
				"s." + ACCOUNT_KEY + ", " +
				"s." + COUNT + ", " +
				"s." + BINDING + ", " +
				"s." + BOUND_TO + ", " +
				"i." + ItemDB.ID + " AS " + itemID + ", " +
				"i." + ItemDB.NAME + " AS " + itemName + ", " +
				"i." + ItemDB.CHAT_LINK + ", " +
				"i." + ItemDB.ICON + ", " +
				"i." + ItemDB.RARITY + ", " +
				"i." + ItemDB.LEVEL + ", " +
				"i." + ItemDB.DESCRIPTION + " " +
				"FROM " +
				table + " s " +
				"INNER JOIN " + ItemDB.TABLE_NAME + " i ON s." + ITEM_ID + " = i." + ItemDB.ID +
				flags;
		return customGet(query);
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
	protected List<AccountInfo> __parseGet(Cursor cursor) {
		List<AccountInfo> storage = new ArrayList<>();
		if (cursor.moveToFirst())
			while (!cursor.isAfterLast()) {
				AccountInfo current = new AccountInfo(cursor.getString(cursor.getColumnIndex(ACCOUNT_KEY)));
				if (storage.contains(current)) current = storage.get(storage.indexOf(current));
				else storage.add(current);

				StorageInfo temp = new StorageInfo();
				//check where this storage info belongs to
				if (cursor.getColumnIndex(charName) != -1) {//character inventory
					List<CharacterInfo> characters = current.getAllCharacters();
					CharacterInfo character = new CharacterInfo(cursor.getString(cursor.getColumnIndex(charName)));
					if (characters.contains(character))
						character = characters.get(characters.indexOf(character));
					else characters.add(character);
					//add this item to character inventory
					character.getInventory().add(temp);
					temp.setCharacterName(character.getName());
				} else {//bank info
					//add this item to bank
					current.getBank().add(temp);
					temp.setCategoryName(cursor.getString(cursor.getColumnIndex(CATEGORY_NAME)));
				}

				ItemInfo item = new ItemInfo(cursor.getLong(cursor.getColumnIndex(itemID)));
				//fill item info
				item.setName(cursor.getString(cursor.getColumnIndex(itemName)));
				item.setChatLink(cursor.getString(cursor.getColumnIndex(ItemDB.CHAT_LINK)));
				item.setIcon(cursor.getString(cursor.getColumnIndex(ItemDB.ICON)));
				item.setRarity(Item.Rarity.valueOf(cursor.getString(cursor.getColumnIndex(ItemDB.RARITY))));
				item.setLevel(cursor.getInt(cursor.getColumnIndex(ItemDB.LEVEL)));
				item.setDescription(cursor.getString(cursor.getColumnIndex(ItemDB.DESCRIPTION)));
				temp.setItemInfo(item);//add item info to storage
				//fill rest of the storage info
				temp.setId(cursor.getLong(cursor.getColumnIndex(storageID)));
				temp.setApi(current.getAPI());
				temp.setCount(cursor.getInt(cursor.getColumnIndex(COUNT)));
				//check if there is a bind for this item
				String binding = cursor.getString(cursor.getColumnIndex(BINDING));
				if (binding.equals(""))
					temp.setBinding(null);
				else temp.setBinding(Storage.Binding.valueOf(binding));

				temp.setBoundTo(cursor.getString(cursor.getColumnIndex(BOUND_TO)));

				cursor.moveToNext();
			}
		return storage;
	}

	private ContentValues populateContent(long id, long itemID, String name, String api,
	                                      long count, String category, Storage.Binding binding,
	                                      String boundTo) {
		ContentValues values = new ContentValues();
		if (id >= 0) values.put(ID, id);
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
