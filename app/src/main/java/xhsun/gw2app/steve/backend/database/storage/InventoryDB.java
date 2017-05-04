package xhsun.gw2app.steve.backend.database.storage;

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
import xhsun.gw2app.steve.backend.database.character.CharacterDB;
import xhsun.gw2app.steve.backend.database.character.CharacterInfo;
import xhsun.gw2app.steve.backend.database.common.ItemDB;
import xhsun.gw2app.steve.backend.database.common.ItemInfo;
import xhsun.gw2app.steve.backend.database.common.SkinDB;
import xhsun.gw2app.steve.backend.database.common.SkinInfo;

/**
 * This class handles all transaction for inventory table
 *
 * @author xhsun
 * @since 2017-03-29
 */

public class InventoryDB extends Database<AccountInfo> {
	public static final String TABLE_NAME = "inventory";
	private static final String ID = "id";
	private static final String ITEM_ID = "item_id";
	private static final String SKIN_ID = "skin_id";
	private static final String CHARACTER_NAME = "name";
	private static final String ACCOUNT_KEY = "api";
	private static final String COUNT = "count";
	private static final String BINDING = "binding";
	private static final String BOUND_TO = "bound";

	@Inject
	public InventoryDB(Context context) {
		super(context);
	}

	public static String createTable() {
		return "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
				ID + " INTEGER PRIMARY KEY," +
				ITEM_ID + " INTEGER NOT NULL," +
				CHARACTER_NAME + " TEXT NOT NULL," +
				ACCOUNT_KEY + " TEXT NOT NULL," +
				COUNT + " INTEGER NOT NULL CHECK(" + COUNT + " >= 0)," +
				SKIN_ID + " INTEGER DEFAULT NULL," +
				BINDING + " TEXT DEFAULT ''," +
				BOUND_TO + " TEXT DEFAULT ''," +
				"FOREIGN KEY (" + ACCOUNT_KEY + ") REFERENCES " + AccountDB.TABLE_NAME + "(" + AccountDB.API + ") ON DELETE CASCADE ON UPDATE CASCADE," +
				"FOREIGN KEY (" + ITEM_ID + ") REFERENCES " + ItemDB.TABLE_NAME + "(" + ItemDB.ID + ") ON DELETE CASCADE ON UPDATE CASCADE," +
				"FOREIGN KEY (" + SKIN_ID + ") REFERENCES " + SkinDB.TABLE_NAME + "(" + SkinDB.ID + ") ON DELETE CASCADE ON UPDATE CASCADE," +
				"FOREIGN KEY (" + CHARACTER_NAME + ") REFERENCES " + CharacterDB.TABLE_NAME + "(" + CharacterDB.NAME + ") ON DELETE CASCADE ON UPDATE CASCADE);";
	}

	/**
	 * replace or insert the entry to database
	 * @param id database id | -1 if don't know
	 * @param itemID  item id
	 * @param name    character name
	 * @param api     API key
	 * @param count   number of items
	 * @param skinID skin id
	 * @param binding binding | null if no binding
	 * @param boundTo character name | empty if not apply
	 * @return id on success, -1 on error
	 */
	long replace(long id, long itemID, String name, String api, long count, long skinID,
	             Storage.Binding binding, String boundTo) {
		Timber.i("Start insert or update storage entry for (%d, %s, %s)", itemID, name, api);
		return replaceAndReturn(TABLE_NAME, populateContent(id, itemID, name, api, count,
				skinID, binding, boundTo));
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

//	List<StorageInfo> getAll(boolean isBank) {
//		if (isBank) return __get(BANK_TABLE_NAME, "");
//		else return __get(INVENTORY_TABLE_NAME, "");
//	}

	/**
	 * get all inventory info for given character
	 *
	 * @param name character name
	 * @return list of items in given char's inventory
	 */
	List<StorageInfo> get(String name) {
		List<AccountInfo> list;
		if ((list = _get(TABLE_NAME, " WHERE " + CHARACTER_NAME + " = '" + name + "'")).isEmpty())
			return null;
		return list.get(0).getAllCharacters().get(0).getInventory();
	}

	/**
	 * get all inventory info for all known accounts
	 *
	 * @return list of accounts
	 */
	List<AccountInfo> getAll() {
		return _get(TABLE_NAME, "");
	}

	//TODO update join to incorporate skin
	//custom get that get both item info and storage info
	private List<AccountInfo> _get(String table, String flags) {
		String query = "SELECT " +
				"c." + ID + ", " +
				"c." + CHARACTER_NAME + ", " +
				"c." + ACCOUNT_KEY + ", " +
				"c." + COUNT + ", " +
				"c." + BINDING + ", " +
				"c." + BOUND_TO + ", " +
				"i." + ItemDB.ID + ", " +
				"i." + ItemDB.NAME + ", " +
				"i." + ItemDB.CHAT_LINK + ", " +
				"i." + ItemDB.ICON + ", " +
				"i." + ItemDB.RARITY + ", " +
				"i." + ItemDB.LEVEL + ", " +
				"i." + ItemDB.DESCRIPTION + ", " +
				"s." + SkinDB.ID + ", " +
				"s." + SkinDB.NAME + ", " +
				"s." + SkinDB.TYPE + ", " +
				"s." + SkinDB.RESTRICTION + ", " +
				"s." + SkinDB.ICON + ", " +
				"s." + SkinDB.RARITY + ", " +
				"s." + SkinDB.OVERRIDE + ", " +
				"s." + SkinDB.DESCRIPTION + " " +
				"FROM " +
				table + " c " +
				"INNER JOIN " + ItemDB.TABLE_NAME + " i ON c." + ITEM_ID + " = i." + ItemDB.ID + "\n" +
				"LEFT JOIN " + SkinDB.TABLE_NAME + " s ON c." + SKIN_ID + " = s." + SkinDB.ID + "\n" +
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
				List<CharacterInfo> characters = current.getAllCharacters();
				CharacterInfo character = new CharacterInfo(cursor.getString(cursor.getColumnIndex(CHARACTER_NAME)));
				if (characters.contains(character))
					character = characters.get(characters.indexOf(character));
				else characters.add(character);
				//add this item to character inventory
				character.getInventory().add(temp);
				temp.setCharacterName(character.getName());

				ItemInfo item = new ItemInfo(cursor.getLong(cursor.getColumnIndex(ItemDB.ID)));
				//fill item info
				item.setName(cursor.getString(cursor.getColumnIndex(ItemDB.NAME)));
				item.setChatLink(cursor.getString(cursor.getColumnIndex(ItemDB.CHAT_LINK)));
				item.setIcon(cursor.getString(cursor.getColumnIndex(ItemDB.ICON)));
				item.setRarity(Item.Rarity.valueOf(cursor.getString(cursor.getColumnIndex(ItemDB.RARITY))));
				item.setLevel(cursor.getInt(cursor.getColumnIndex(ItemDB.LEVEL)));
				item.setDescription(cursor.getString(cursor.getColumnIndex(ItemDB.DESCRIPTION)));
				temp.setItemInfo(item);//add item info to storage

				//fill skin info, only if it exist
				if (!cursor.isNull(cursor.getColumnIndex(SkinDB.ID))) {
					SkinInfo skin = new SkinInfo(cursor.getLong(cursor.getColumnIndex(SkinDB.ID)));
					skin.setName(cursor.getString(cursor.getColumnIndex(SkinDB.NAME)));
					skin.setType(Item.Type.valueOf(cursor.getString(cursor.getColumnIndex(SkinDB.TYPE))));
					skin.setIcon(cursor.getString(cursor.getColumnIndex(SkinDB.ICON)));
					skin.setRarity(Item.Rarity.valueOf(cursor.getString(cursor.getColumnIndex(SkinDB.RARITY))));
					skin.setOverride((cursor.getInt(cursor.getColumnIndex(SkinDB.OVERRIDE)) == SkinDB.OVERRIDING));
					skin.setRestriction(SkinDB.toRestrictionArray(cursor.getString(cursor.getColumnIndex(SkinDB.RESTRICTION))));
					skin.setDescription(cursor.getString(cursor.getColumnIndex(SkinDB.DESCRIPTION)));
					temp.setSkinInfo(skin);
				}
				//fill rest of the storage info
				temp.setId(cursor.getLong(cursor.getColumnIndex(ID)));
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
	                                      long count, long skinID, Storage.Binding binding,
	                                      String boundTo) {
		ContentValues values = new ContentValues();
		if (id >= 0) values.put(ID, id);
		values.put(ITEM_ID, itemID);
		if (!name.equals("")) values.put(CHARACTER_NAME, name);
		values.put(ACCOUNT_KEY, api);
		values.put(COUNT, count);
		if (skinID > 0) values.put(SKIN_ID, skinID);
		if (binding != null) {
			values.put(BINDING, binding.name());
			values.put(BOUND_TO, boundTo);
		}
		return values;
	}
}
