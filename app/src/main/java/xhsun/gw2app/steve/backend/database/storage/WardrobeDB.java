package xhsun.gw2app.steve.backend.database.storage;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;
import xhsun.gw2app.steve.backend.database.account.AccountDB;
import xhsun.gw2app.steve.backend.database.account.AccountInfo;
import xhsun.gw2app.steve.backend.database.common.SkinDB;
import xhsun.gw2app.steve.backend.util.items.StorageType;

/**
 * Handle all transaction for wardrobe table
 *
 * @author xhsun
 * @since 2017-05-04
 */

public class WardrobeDB extends StorageDB {
	public static final String TABLE_NAME = "wardrobe";

	WardrobeDB(Context context) {
		super(context, StorageType.WARDROBE);
	}

	public static String createTable() {
		return "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
				SKIN_ID + " INTEGER NOT NULL," +
				ACCOUNT_KEY + " TEXT NOT NULL," +
				"PRIMARY KEY (" + SKIN_ID + "," + ACCOUNT_KEY + ")," +
				"FOREIGN KEY (" + SKIN_ID + ") REFERENCES " + SkinDB.TABLE_NAME + "(" + SkinDB.ID + ") ON DELETE CASCADE ON UPDATE CASCADE," +
				"FOREIGN KEY (" + ACCOUNT_KEY + ") REFERENCES " + AccountDB.TABLE_NAME + "(" + AccountDB.API + ") ON DELETE CASCADE ON UPDATE CASCADE);";
	}

	/**
	 * add skin to wardrobe
	 * Note: even know this method is called replace,
	 * this method don't actually replace anything, this method only add
	 *
	 * @param info storage info that contains necessary info
	 * @return row id | -1 if failed
	 */
	@Override
	long replace(StorageInfo info) {
		Timber.i("Start insert or update wardrobe entry for (%s, %d)", info.getApi(), info.getSkinInfo().getId());
		return insert(TABLE_NAME, populateContent(info.getApi(), info.getSkinInfo().getId()));
	}

	/**
	 * delete given item from database
	 *
	 * @param api    API key
	 * @param skinID skin id
	 * @return true on success, false otherwise
	 */
	boolean delete(String api, long skinID) {
		Timber.i("Start deleting skin (%d) from wardrobe for %s", skinID, api);
		String selection = SKIN_ID + " = ? AND " + ACCOUNT_KEY + " = ?";
		String[] selectionArgs = {Long.toString(skinID), api};
		return delete(TABLE_NAME, selection, selectionArgs);
	}

	@Override
	List<StorageInfo> get(String api) {
		List<AccountInfo> list;
		if ((list = _get(TABLE_NAME, " WHERE " + ACCOUNT_KEY + " = '" + api + "'")).isEmpty())
			return new ArrayList<>();
		return list.get(0).getWardrobe();
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
				temp.setSkinInfo(getSkin(cursor));
				temp.setApi(current.getAPI());

				//add storage info to account
				current.getWardrobe().add(temp);

				cursor.moveToNext();
			}
		return storage;
	}
}
