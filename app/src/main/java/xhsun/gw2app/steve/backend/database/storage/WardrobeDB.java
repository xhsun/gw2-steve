package xhsun.gw2app.steve.backend.database.storage;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;
import xhsun.gw2app.steve.backend.data.AccountData;
import xhsun.gw2app.steve.backend.data.vault.WardrobeData;
import xhsun.gw2app.steve.backend.data.vault.WardrobeSubData;
import xhsun.gw2app.steve.backend.data.vault.item.WardrobeItemData;
import xhsun.gw2app.steve.backend.database.account.AccountDB;
import xhsun.gw2app.steve.backend.database.common.SkinDB;
import xhsun.gw2app.steve.backend.util.vault.VaultType;

/**
 * Handle all transaction for wardrobe table
 *
 * @author xhsun
 * @since 2017-05-04
 */

public class WardrobeDB extends StorageDB<WardrobeData, WardrobeItemData> {
	public static final String TABLE_NAME = "wardrobe";

	public WardrobeDB(Context context) {
		super(context, VaultType.WARDROBE);
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
	long replace(WardrobeItemData info) {
		Timber.d("Start insert or update wardrobe entry for (%s, %d)", info.getApi(), info.getSkinData().getId());
		return insert(TABLE_NAME, populateContent(info.getApi(), info.getSkinData().getId()));
	}

	/**
	 * delete given item from database
	 *
	 * @param data contain skin id and API key
	 * @return true on success, false otherwise
	 */
	@Override
	boolean delete(WardrobeItemData data) {
		Timber.d("Start deleting skin (%d) from wardrobe for %s", data.getId(), data.getApi());
		String selection = SKIN_ID + " = ? AND " + ACCOUNT_KEY + " = ?";
		String[] selectionArgs = {Integer.toString(data.getId()), data.getApi()};
		return delete(TABLE_NAME, selection, selectionArgs);
	}

	@Override
	List<WardrobeData> get(String api) {
		List<AccountData> list;
		if ((list = _get(TABLE_NAME, " WHERE " + ACCOUNT_KEY + " = '" + api + "'")).isEmpty())
			return new ArrayList<>();
		return list.get(0).getWardrobe();
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
				if ((index = storage.indexOf(current)) > 0) current = storage.get(index);
				else storage.add(current);

				WardrobeItemData temp = new WardrobeItemData();
				temp.setApi(current.getAPI());
				temp.setSkinData(getSkin(cursor));

				//trying to add item to appropriate category
				WardrobeData wardrobe = new WardrobeData(temp.getCategoryType());
				if ((index = current.getWardrobe().indexOf(wardrobe)) >= 0)
					wardrobe = current.getWardrobe().get(index);
				else current.getWardrobe().add(wardrobe);

				WardrobeSubData subType = new WardrobeSubData(temp.getSubCategoryName());
				if ((index = wardrobe.getData().indexOf(subType)) >= 0)
					subType = wardrobe.getData().get(index);
				else wardrobe.getData().add(subType);

				//add wardrobe info to account
				subType.getItems().add(temp);

				cursor.moveToNext();
			}
		return storage;
	}
}
