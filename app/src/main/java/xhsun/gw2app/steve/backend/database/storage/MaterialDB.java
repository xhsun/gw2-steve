package xhsun.gw2app.steve.backend.database.storage;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;
import xhsun.gw2api.guildwars2.model.util.Storage;
import xhsun.gw2app.steve.backend.database.account.AccountDB;
import xhsun.gw2app.steve.backend.database.account.AccountInfo;
import xhsun.gw2app.steve.backend.database.common.ItemDB;
import xhsun.gw2app.steve.backend.util.items.StorageType;

/**
 * Handle all transaction for material storage table
 *
 * @author xhsun
 * @since 2017-05-04
 */

public class MaterialDB extends StorageDB {
	public static final String TABLE_NAME = "materialStorage";

	MaterialDB(Context context) {
		super(context, StorageType.MATERIAL);
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
	long replace(StorageInfo info) {
		Timber.i("Start insert or update material entry for (%s, %d)", info.getApi(), info.getItemInfo().getId());
		return replaceAndReturn(TABLE_NAME, populateContent(info.getId(), info.getItemInfo().getId(),
				info.getApi(), info.getCount(), info.getBinding(), info.getCategoryID(), info.getCategoryName()));
	}

	/**
	 * delete given item from database
	 *
	 * @param id material storage id
	 * @return true on success, false otherwise
	 */
	boolean delete(long id) {
		return delete(id, TABLE_NAME);
	}

	@Override
	List<StorageInfo> get(String api) {
		List<AccountInfo> list;
		if ((list = _get(TABLE_NAME, " WHERE " + ACCOUNT_KEY + " = '" + api + "'")).isEmpty())
			return new ArrayList<>();
		return list.get(0).getMaterial();
	}

	@Override
	List<AccountInfo> getAll() {
		return _get(TABLE_NAME, "");
	}

	@Override
	protected List<AccountInfo> __parseGet(Cursor cursor) {
		List<AccountInfo> storage = new ArrayList<>();
		if (cursor.moveToFirst())
			while (!cursor.isAfterLast()) {
				AccountInfo current = new AccountInfo(cursor.getString(cursor.getColumnIndex(ACCOUNT_KEY)));
				if (storage.contains(current)) current = storage.get(storage.indexOf(current));
				else storage.add(current);

				StorageInfo temp = new StorageInfo();
				//fill item info
				temp.setItemInfo(getItem(cursor));
				//fill rest of the storage info
				temp.setId(cursor.getLong(cursor.getColumnIndex(ID)));
				temp.setApi(current.getAPI());
				temp.setCount(cursor.getInt(cursor.getColumnIndex(COUNT)));
				temp.setCategoryID(cursor.getLong(cursor.getColumnIndex(MATERIAL_ID)));
				temp.setCategoryName(cursor.getString(cursor.getColumnIndex(MATERIAL_NAME)));
				//check if there is a bind for this item
				String binding = cursor.getString(cursor.getColumnIndex(BINDING));
				if (binding.equals(""))
					temp.setBinding(null);
				else temp.setBinding(Storage.Binding.valueOf(binding));
				temp.setBoundTo(cursor.getString(cursor.getColumnIndex(BOUND_TO)));

				//add storage info to account
				current.getMaterial().add(temp);

				cursor.moveToNext();
			}
		return storage;
	}
}
