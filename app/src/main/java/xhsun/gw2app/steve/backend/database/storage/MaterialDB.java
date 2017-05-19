package xhsun.gw2app.steve.backend.database.storage;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;
import xhsun.gw2api.guildwars2.model.util.Storage;
import xhsun.gw2app.steve.backend.data.AccountData;
import xhsun.gw2app.steve.backend.data.vault.MaterialStorageData;
import xhsun.gw2app.steve.backend.data.vault.item.MaterialItemData;
import xhsun.gw2app.steve.backend.database.account.AccountDB;
import xhsun.gw2app.steve.backend.database.common.ItemDB;
import xhsun.gw2app.steve.backend.util.vault.VaultType;

/**
 * Handle all transaction for material storage table
 *
 * @author xhsun
 * @since 2017-05-04
 */

public class MaterialDB extends StorageDB<MaterialStorageData, MaterialItemData> {
	public static final String TABLE_NAME = "materialStorage";

	public MaterialDB(Context context) {
		super(context, VaultType.MATERIAL);
	}

	public static String createTable() {
		return "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
				ID + " INTEGER PRIMARY KEY," +
				ITEM_ID + " INTEGER NOT NULL," +
				ACCOUNT_KEY + " TEXT NOT NULL," +
				COUNT + " INTEGER NOT NULL CHECK(" + COUNT + " >= 0)," +
				BINDING + " TEXT DEFAULT ''," +
				MATERIAL_ID + " INTEGER NOT NULL," +
				MATERIAL_NAME + " INTEGER NOT NULL," +
				"FOREIGN KEY (" + ITEM_ID + ") REFERENCES " + ItemDB.TABLE_NAME + "(" + ItemDB.ID + ") ON DELETE CASCADE ON UPDATE CASCADE," +
				"FOREIGN KEY (" + ACCOUNT_KEY + ") REFERENCES " + AccountDB.TABLE_NAME + "(" + AccountDB.API + ") ON DELETE CASCADE ON UPDATE CASCADE);";
	}

	@Override
	long replace(MaterialItemData info) {
		Timber.d("Start insert or update material entry for (%s, %d)", info.getApi(), info.getItemData().getId());
		return replaceAndReturn(TABLE_NAME, populateContent(info.getId(), info.getItemData().getId(),
				info.getApi(), info.getCount(), info.getBinding(), info.getCategoryID(), info.getCategoryName()));
	}

	/**
	 * delete given item from database
	 *
	 * @param data contains material storage id
	 * @return true on success, false otherwise
	 */
	@Override
	boolean delete(MaterialItemData data) {
		return delete(data.getId(), TABLE_NAME);
	}

	@Override
	List<MaterialStorageData> get(String api) {
		List<AccountData> list;
		if ((list = _get(TABLE_NAME, " WHERE " + ACCOUNT_KEY + " = '" + api + "'")).isEmpty())
			return new ArrayList<>();
		return list.get(0).getMaterial();
	}

	@Override
	List<AccountData> getAll() {
		return _get(TABLE_NAME, "");
	}


	@Override
	protected List<AccountData> __parseGet(Cursor cursor) {
		List<AccountData> storage = new ArrayList<>();
		if (cursor.moveToFirst())
			while (!cursor.isAfterLast()) {
				int index;
				AccountData current = new AccountData(cursor.getString(cursor.getColumnIndex(ACCOUNT_KEY)));
				if ((index = storage.indexOf(current)) >= 0) current = storage.get(index);
				else storage.add(current);

				long id = cursor.getLong(cursor.getColumnIndex(MATERIAL_ID));
				String name = cursor.getString(cursor.getColumnIndex(MATERIAL_NAME));

				MaterialStorageData material = new MaterialStorageData(id, name);
				if ((index = current.getMaterial().indexOf(material)) >= 0)
					material = current.getMaterial().get(index);
				else current.getMaterial().add(material);

				MaterialItemData temp = new MaterialItemData();
				//fill item info
				temp.setItemData(getItem(cursor));

				//fill rest of the material item
				temp.setCategoryID(id);
				temp.setCategoryName(name);
				temp.setId(cursor.getLong(cursor.getColumnIndex(ID)));
				temp.setApi(current.getAPI());
				temp.setCount(cursor.getInt(cursor.getColumnIndex(COUNT)));

				//check if there is a bind for this item
				String binding = cursor.getString(cursor.getColumnIndex(BINDING));
				if (binding.equals("")) temp.setBinding(null);
				else temp.setBinding(Storage.Binding.valueOf(binding));

				//add storage info to account
				material.getItems().add(temp);

				cursor.moveToNext();
			}
		return storage;
	}
}
