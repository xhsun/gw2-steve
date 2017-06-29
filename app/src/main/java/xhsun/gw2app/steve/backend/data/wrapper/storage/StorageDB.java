package xhsun.gw2app.steve.backend.data.wrapper.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

import java.util.List;

import me.xhsun.guildwars2wrapper.model.v2.Item;
import me.xhsun.guildwars2wrapper.model.v2.util.Storage;
import me.xhsun.guildwars2wrapper.model.v2.util.comm.Type;
import me.xhsun.guildwars2wrapper.model.v2.util.itemDetail.ItemDetail;
import xhsun.gw2app.steve.backend.data.model.AbstractModel;
import xhsun.gw2app.steve.backend.data.model.AccountModel;
import xhsun.gw2app.steve.backend.data.model.ItemModel;
import xhsun.gw2app.steve.backend.data.model.MiscItemModel;
import xhsun.gw2app.steve.backend.data.model.SkinModel;
import xhsun.gw2app.steve.backend.data.model.vault.item.VaultItemModel;
import xhsun.gw2app.steve.backend.data.wrapper.Database;
import xhsun.gw2app.steve.backend.data.wrapper.common.ItemDB;
import xhsun.gw2app.steve.backend.data.wrapper.common.MiscDB;
import xhsun.gw2app.steve.backend.data.wrapper.common.SkinDB;
import xhsun.gw2app.steve.backend.util.support.vault.VaultType;

import static xhsun.gw2app.steve.backend.data.model.MiscItemModel.SPLIT;

/**
 * template class for database access related to storage
 *
 * @author xhsun
 * @since 2017-05-04
 */

abstract class StorageDB<I extends AbstractModel, S extends VaultItemModel> extends Database<AccountModel> {
	static final String ID = "id";
	static final String ACCOUNT_KEY = "api";
	static final String COUNT = "count";
	static final String BINDING = "binding";
	static final String BOUND_TO = "bound";
	static final String ITEM_ID = "item_id";
	static final String SKIN_ID = "skin_id";
	static final String MISC_ID = "misc_id";
	static final String MATERIAL_ID = "material_id";
	static final String MATERIAL_NAME = "material_name";
	static final String CHARACTER_NAME = "name";
	private VaultType type;


	StorageDB(Context context, VaultType type) {
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
	abstract long replace(S info);

	/**
	 * replace or insert bunck data into database
	 *
	 * @param data data to insert
	 */
	abstract void bulkInsert(List<S> data);

	/**
	 * get all storage item for the given value
	 *
	 * @param value API key | character name
	 * @return list of storage info
	 */
	abstract List<I> get(String value);

	/**
	 * get all storage info
	 *
	 * @return list of accounts
	 */
	abstract List<AccountModel> getAll();

	abstract boolean delete(S data);

	abstract void bulkDelete(List<S> data);

	//delete given entry from given table
	boolean delete(int id, String table) {
		String selection = ID + " = ?";
		String[] selectionArgs = {Integer.toString(id)};
		return delete(table, selection, selectionArgs);
	}

	void bulkDelete(List<Integer> ids, String table) {
		bulkDelete(table, ID, TextUtils.join(", ", ids));
	}

	List<AccountModel> _get(String table, String flags) {
		String query = "SELECT ";
		query += "c." + ID + ", " +
				((type == VaultType.INVENTORY) ? ("c." + CHARACTER_NAME + ", ") : "") +
				"c." + ACCOUNT_KEY + ", " +
				"c." + COUNT +
				((type == VaultType.WARDROBE) ? "" : (", c." + BINDING +
						((type == VaultType.MATERIAL) ? "" : ", c." + BOUND_TO)));
		query += (type == VaultType.MATERIAL) ? ", c." + MATERIAL_ID + ", c." + MATERIAL_NAME : "";
		query += (type == VaultType.WARDROBE) ? " " : ", i." + ItemDB.ID + ", " +
				"i." + ItemDB.NAME + ", " +
				"i." + ItemDB.CHAT_LINK + ", " +
				"i." + ItemDB.ICON + ", " +
				"i." + ItemDB.RARITY + ", " +
				"i." + ItemDB.LEVEL + ", " +
				"i." + ItemDB.DESCRIPTION;
		query += (type == VaultType.MATERIAL) ? " " : ", s." + SkinDB.ID + ", " +
				"s." + SkinDB.NAME + ", " +
				"s." + SkinDB.TYPE + ", " +
				"s." + SkinDB.SUBTYPE + ", " +
				"s." + SkinDB.WEIGHT + ", " +
				"s." + SkinDB.RESTRICTION + ", " +
				"s." + SkinDB.ICON + ", " +
				"s." + SkinDB.RARITY + ", " +
				"s." + SkinDB.OVERRIDE + ", " +
				"s." + SkinDB.DESCRIPTION + " ";
		query += (type != VaultType.WARDROBE) ? " " : ", m." + MiscDB.ID + ", " +
				"m." + MiscDB.NAME + ", " +
				"m." + MiscDB.ICON + " ";
		query += "FROM " +
				table + " c ";
		query += (type == VaultType.WARDROBE) ? "" :
				"INNER JOIN " + ItemDB.TABLE_NAME + " i ON c." + ITEM_ID + " = i." + ItemDB.ID + "\n";
		query += (type == VaultType.MATERIAL) ? "" :
				"LEFT JOIN " + SkinDB.TABLE_NAME + " s ON c." + SKIN_ID + " = s." + SkinDB.ID + "\n";
		query += (type != VaultType.WARDROBE) ? "" :
				"LEFT JOIN " + MiscDB.TABLE_NAME + " m ON c." + MISC_ID + " = m." + MiscDB.ID + "\n";
		query += flags;
		return customGet(query);
	}

	ContentValues populateContent(int id, String api, int skinID, String miscID, long count) {
		return populateContent(id, -1, "", api, count, skinID, -1, "", null, "", miscID);
	}

	ContentValues populateContent(int id, int itemID, String api, long count, int skinID,
	                              Storage.Binding binding, String boundTo) {
		return populateContent(id, itemID, "", api, count, skinID, -1, "", binding, boundTo, "");
	}

	ContentValues populateContent(int id, int itemID, String name, String api,
	                              long count, int skinID, Storage.Binding binding,
	                              String boundTo) {
		return populateContent(id, itemID, name, api, count, skinID, -1, "", binding, boundTo, "");
	}

	ContentValues populateContent(int id, int itemID, String api, long count,
	                              Storage.Binding binding, int categoryID, String categoryName) {
		return populateContent(id, itemID, "", api, count, -1, categoryID, categoryName, binding, "", "");
	}

	private ContentValues populateContent(int id, int itemID, String name, String api,
	                                      long count, int skinID, int categoryID, String categoryName,
	                                      Storage.Binding binding, String boundTo, String miscID) {
		ContentValues values = new ContentValues();
		if (id >= 0) values.put(ID, id);
		if (itemID > 0) values.put(ITEM_ID, itemID);
		if (!name.equals("")) values.put(CHARACTER_NAME, name);
		values.put(ACCOUNT_KEY, api);
		if (count >= 0) values.put(COUNT, count);
		if (skinID > 0) values.put(SKIN_ID, skinID);
		if (binding != null) values.put(BINDING, binding.name());
		if (!boundTo.equals("")) values.put(BOUND_TO, boundTo);
		if (categoryID > 0) {
			values.put(MATERIAL_ID, categoryID);
			values.put(MATERIAL_NAME, categoryName);
		}
		if (!miscID.equals("")) values.put(MISC_ID, miscID);
		return values;
	}

	ItemModel getItem(Cursor cursor) {
		ItemModel item = null;
		if (!cursor.isNull(cursor.getColumnIndex(ItemDB.ID))) {
			item = new ItemModel(cursor.getInt(cursor.getColumnIndex(ItemDB.ID)));
			item.setName(cursor.getString(cursor.getColumnIndex(ItemDB.NAME)));
			item.setChatLink(cursor.getString(cursor.getColumnIndex(ItemDB.CHAT_LINK)));
			item.setIcon(cursor.getString(cursor.getColumnIndex(ItemDB.ICON)));
			item.setRarity(Item.Rarity.valueOf(cursor.getString(cursor.getColumnIndex(ItemDB.RARITY))));
			item.setLevel(cursor.getInt(cursor.getColumnIndex(ItemDB.LEVEL)));
			item.setDescription(cursor.getString(cursor.getColumnIndex(ItemDB.DESCRIPTION)));
		}
		return item;
	}

	SkinModel getSkin(Cursor cursor) {
		SkinModel skin = null;
		if (!cursor.isNull(cursor.getColumnIndex(SkinDB.ID))) {
			skin = new SkinModel(cursor.getInt(cursor.getColumnIndex(SkinDB.ID)));
			skin.setName(cursor.getString(cursor.getColumnIndex(SkinDB.NAME)));
			skin.setType(Item.Type.valueOf(cursor.getString(cursor.getColumnIndex(SkinDB.TYPE))));
			skin.setSubType(Type.valueOf(cursor.getString(cursor.getColumnIndex(SkinDB.SUBTYPE))));

			String weight = cursor.getString(cursor.getColumnIndex(SkinDB.WEIGHT));
			if (weight != null)
				skin.setWeightClass(ItemDetail.Weight.valueOf(weight));

			skin.setIcon(cursor.getString(cursor.getColumnIndex(SkinDB.ICON)));
			skin.setRarity(Item.Rarity.valueOf(cursor.getString(cursor.getColumnIndex(SkinDB.RARITY))));
			skin.setOverride((cursor.getInt(cursor.getColumnIndex(SkinDB.OVERRIDE)) == SkinDB.OVERRIDING));
			skin.setRestriction(SkinDB.toRestrictionArray(cursor.getString(cursor.getColumnIndex(SkinDB.RESTRICTION))));
			skin.setDescription(cursor.getString(cursor.getColumnIndex(SkinDB.DESCRIPTION)));
		}
		return skin;
	}

	MiscItemModel getMiscItem(Cursor cursor) {
		MiscItemModel misc = null;
		if (!cursor.isNull(cursor.getColumnIndex(MiscDB.ID))) {
			String[] idStr = cursor.getString(cursor.getColumnIndex(MiscDB.ID)).split(SPLIT);
			misc = new MiscItemModel(MiscItemModel.MiscItemType.valueOf(idStr[0]), Integer.valueOf(idStr[1]));
			misc.setName(cursor.getString(cursor.getColumnIndex(MiscDB.NAME)));
			misc.setIcon(cursor.getString(cursor.getColumnIndex(MiscDB.ICON)));
		}
		return misc;
	}
}
