package xhsun.gw2app.steve.backend.database.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.List;

import timber.log.Timber;
import xhsun.gw2api.guildwars2.model.Item;
import xhsun.gw2api.guildwars2.model.util.Storage;
import xhsun.gw2app.steve.backend.database.Database;
import xhsun.gw2app.steve.backend.database.account.AccountInfo;
import xhsun.gw2app.steve.backend.database.common.ItemDB;
import xhsun.gw2app.steve.backend.database.common.ItemInfo;
import xhsun.gw2app.steve.backend.database.common.SkinDB;
import xhsun.gw2app.steve.backend.database.common.SkinInfo;
import xhsun.gw2app.steve.backend.util.items.StorageType;

/**
 * template class for database access related to storage
 *
 * @author xhsun
 * @since 2017-05-04
 */

abstract class StorageDB extends Database<AccountInfo> {
	static final String ID = "id";
	static final String ACCOUNT_KEY = "api";
	static final String COUNT = "count";
	static final String BINDING = "binding";
	static final String BOUND_TO = "bound";
	static final String ITEM_ID = "item_id";
	static final String SKIN_ID = "skin_id";
	static final String MATERIAL_ID = "material_id";
	static final String MATERIAL_NAME = "material_name";
	static final String CHARACTER_NAME = "name";
	private StorageType type;


	StorageDB(Context context, StorageType type) {
		super(context);
		this.type = type;
	}

	/**
	 * replace or insert the entry to database
	 * Note: leave id as -1 if you don't know the id
	 *
	 * @param info storage info that contains necessary info
	 * @return id on success, -1 on error
	 */
	abstract long replace(StorageInfo info);

	/**
	 * get all storage item for the given value
	 *
	 * @param value API key | character name
	 * @return list of storage info
	 */
	abstract List<StorageInfo> get(String value);

	/**
	 * get all storage info
	 *
	 * @return list of accounts
	 */
	abstract List<AccountInfo> getAll();

	//delete given entry from given table
	boolean delete(long id, String table) {
		Timber.i("Start deleting storage (%d) for type %s", id, type);
		String selection = ID + " = ?";
		String[] selectionArgs = {Long.toString(id)};
		return delete(table, selection, selectionArgs);
	}

	//TODO change this as more table got added
	List<AccountInfo> _get(String table, String flags) {
		String query = "SELECT " +
				"c." + ID + ", " +
				((type == StorageType.INVENTORY) ? ("c." + CHARACTER_NAME + ", ") : "") +
				"c." + ACCOUNT_KEY + ", " +
				"c." + COUNT + ", " +
				"c." + BINDING + ", " +
				"c." + BOUND_TO;
		query += (type == StorageType.MATERIAL) ? ", c." + MATERIAL_ID + ", c." + CHARACTER_NAME : "";
		query += (type == StorageType.WARDROBE) ? " " : ", i." + ItemDB.ID + ", " +
				"i." + ItemDB.NAME + ", " +
				"i." + ItemDB.CHAT_LINK + ", " +
				"i." + ItemDB.ICON + ", " +
				"i." + ItemDB.RARITY + ", " +
				"i." + ItemDB.LEVEL + ", " +
				"i." + ItemDB.DESCRIPTION;
		query += (type == StorageType.MATERIAL) ? " " : ", s." + SkinDB.ID + ", " +
				"s." + SkinDB.NAME + ", " +
				"s." + SkinDB.TYPE + ", " +
				"s." + SkinDB.RESTRICTION + ", " +
				"s." + SkinDB.ICON + ", " +
				"s." + SkinDB.RARITY + ", " +
				"s." + SkinDB.OVERRIDE + ", " +
				"s." + SkinDB.DESCRIPTION + " ";
		query += "FROM " +
				table + " c ";
		query += (type == StorageType.WARDROBE) ? "" :
				"INNER JOIN " + ItemDB.TABLE_NAME + " i ON c." + ITEM_ID + " = i." + ItemDB.ID + "\n";
		query += (type == StorageType.MATERIAL) ? "" :
				"LEFT JOIN " + SkinDB.TABLE_NAME + " s ON c." + SKIN_ID + " = s." + SkinDB.ID + "\n";
		query += flags;
		return customGet(query);
	}

	ContentValues populateContent(long id, long itemID, String api, long count, long skinID,
	                              Storage.Binding binding, String boundTo) {
		return populateContent(id, itemID, "", api, count, skinID, -1, "", binding, boundTo);
	}

	ContentValues populateContent(long id, long itemID, String name, String api,
	                              long count, long skinID, Storage.Binding binding,
	                              String boundTo) {
		return populateContent(id, itemID, name, api, count, skinID, -1, "", binding, boundTo);
	}

	ContentValues populateContent(long id, long itemID, String api, long count,
	                              Storage.Binding binding, long categoryID, String categoryName) {
		return populateContent(id, itemID, "", api, count, -1, categoryID, categoryName, binding, "");
	}

	private ContentValues populateContent(long id, long itemID, String name, String api,
	                                      long count, long skinID, long categoryID, String categoryName,
	                                      Storage.Binding binding, String boundTo) {
		ContentValues values = new ContentValues();
		if (id >= 0) values.put(ID, id);
		if (itemID > 0) values.put(ITEM_ID, itemID);
		if (!name.equals("")) values.put(CHARACTER_NAME, name);
		values.put(ACCOUNT_KEY, api);
		values.put(COUNT, count);
		if (skinID > 0) values.put(SKIN_ID, skinID);
		if (binding != null) values.put(BINDING, binding.name());
		if (!boundTo.equals("")) values.put(BOUND_TO, boundTo);
		if (categoryID > 0) {
			values.put(MATERIAL_ID, categoryID);
			values.put(MATERIAL_NAME, categoryName);
		}
		return values;
	}

	ItemInfo getItem(Cursor cursor) {
		ItemInfo item = null;
		if (!cursor.isNull(cursor.getColumnIndex(ItemDB.ID))) {
			item = new ItemInfo(cursor.getLong(cursor.getColumnIndex(ItemDB.ID)));
			item.setName(cursor.getString(cursor.getColumnIndex(ItemDB.NAME)));
			item.setChatLink(cursor.getString(cursor.getColumnIndex(ItemDB.CHAT_LINK)));
			item.setIcon(cursor.getString(cursor.getColumnIndex(ItemDB.ICON)));
			item.setRarity(Item.Rarity.valueOf(cursor.getString(cursor.getColumnIndex(ItemDB.RARITY))));
			item.setLevel(cursor.getInt(cursor.getColumnIndex(ItemDB.LEVEL)));
			item.setDescription(cursor.getString(cursor.getColumnIndex(ItemDB.DESCRIPTION)));
		}
		return item;
	}

	SkinInfo getSkin(Cursor cursor) {
		SkinInfo skin = null;
		if (!cursor.isNull(cursor.getColumnIndex(SkinDB.ID))) {
			skin = new SkinInfo(cursor.getLong(cursor.getColumnIndex(SkinDB.ID)));
			skin.setName(cursor.getString(cursor.getColumnIndex(SkinDB.NAME)));
			skin.setType(Item.Type.valueOf(cursor.getString(cursor.getColumnIndex(SkinDB.TYPE))));
			skin.setIcon(cursor.getString(cursor.getColumnIndex(SkinDB.ICON)));
			skin.setRarity(Item.Rarity.valueOf(cursor.getString(cursor.getColumnIndex(SkinDB.RARITY))));
			skin.setOverride((cursor.getInt(cursor.getColumnIndex(SkinDB.OVERRIDE)) == SkinDB.OVERRIDING));
			skin.setRestriction(SkinDB.toRestrictionArray(cursor.getString(cursor.getColumnIndex(SkinDB.RESTRICTION))));
			skin.setDescription(cursor.getString(cursor.getColumnIndex(SkinDB.DESCRIPTION)));
		}
		return skin;
	}
}
