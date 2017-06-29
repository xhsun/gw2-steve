package xhsun.gw2app.steve.backend.data.wrapper.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.annimon.stream.Stream;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;
import xhsun.gw2app.steve.backend.data.model.AccountModel;
import xhsun.gw2app.steve.backend.data.model.vault.WardrobeModel;
import xhsun.gw2app.steve.backend.data.model.vault.WardrobeSubModel;
import xhsun.gw2app.steve.backend.data.model.vault.item.WardrobeItemModel;
import xhsun.gw2app.steve.backend.data.wrapper.account.AccountDB;
import xhsun.gw2app.steve.backend.data.wrapper.common.MiscDB;
import xhsun.gw2app.steve.backend.data.wrapper.common.SkinDB;
import xhsun.gw2app.steve.backend.util.support.vault.VaultType;

/**
 * Handle all transaction for wardrobe table
 *
 * @author xhsun
 * @since 2017-05-04
 */

public class WardrobeDB extends StorageDB<WardrobeModel, WardrobeItemModel> {
	public static final String TABLE_NAME = "wardrobe";

	public WardrobeDB(Context context) {
		super(context, VaultType.WARDROBE);
	}

	public static String createTable() {
		return "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
				ID + " INTEGER PRIMARY KEY," +
				ACCOUNT_KEY + " TEXT NOT NULL," +
				SKIN_ID + " INTEGER DEFAULT NULL," +
				MISC_ID + " TEXT DEFAULT NULL," +
				COUNT + " INTEGER DEFAULT -1," +
				"FOREIGN KEY (" + SKIN_ID + ") REFERENCES " + SkinDB.TABLE_NAME + "(" + SkinDB.ID + ") ON DELETE CASCADE ON UPDATE CASCADE," +
				"FOREIGN KEY (" + MISC_ID + ") REFERENCES " + MiscDB.TABLE_NAME + "(" + MiscDB.ID + ") ON DELETE CASCADE ON UPDATE CASCADE," +
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
	long replace(WardrobeItemModel info) {
		Timber.d("Start insert or update wardrobe entry for (%s, %d)", info.getApi(), info.getSkinModel().getId());
		return insert(TABLE_NAME, populateContent(info.getId(), info.getApi(), info.getSkinModel().getId(),
				info.getMiscItem().getCombinedID(), info.getCount()));
	}

	@Override
	void bulkInsert(List<WardrobeItemModel> data) {
		Timber.d("Start bulk insert material entry");
		List<ContentValues> values = new ArrayList<>();
		Stream.of(data).forEach(w -> values.add(populateContent(w.getId(), w.getApi(),
				(w.getSkinModel() == null) ? -1 : w.getSkinModel().getId(),
				(w.getMiscItem() == null) ? "" : w.getMiscItem().getCombinedID(), w.getCount())));
		bulkInsert(TABLE_NAME, values);
	}

	/**
	 * delete given item from database
	 *
	 * @param data contain skin id and API key
	 * @return true on success, false otherwise
	 */
	@Override
	boolean delete(WardrobeItemModel data) {
		Timber.d("Start deleting item (%d) from wardrobe for %s", data.getId());
		String selection = ID + " = ?";
		String[] selectionArgs = {Integer.toString(data.getId())};
		return delete(TABLE_NAME, selection, selectionArgs);
	}

	/**
	 *
	 * @param data wardrobe item to delete
	 */
	@Override
	void bulkDelete(List<WardrobeItemModel> data) {
		if (data.size() < 1) return;
		Timber.d("Start bulk delete wardrobe entry");
		bulkDelete(Stream.of(data).map(WardrobeItemModel::getId).toList(), TABLE_NAME);
	}

	@Override
	List<WardrobeModel> get(String api) {
		List<AccountModel> list;
		if ((list = _get(TABLE_NAME, " WHERE " + ACCOUNT_KEY + " = '" + api + "'")).isEmpty())
			return new ArrayList<>();
		return list.get(0).getWardrobe();
	}

	@Override
	List<AccountModel> getAll() {
		return _get(TABLE_NAME, "");
	}

	@Override
	protected List<AccountModel> __parseGet(Cursor cursor) {
		List<AccountModel> storage = new ArrayList<>();
		if (cursor.moveToFirst())
			while (!cursor.isAfterLast()) {
				int index;
				AccountModel current = new AccountModel(cursor.getString(cursor.getColumnIndex(ACCOUNT_KEY)));
				if ((index = storage.indexOf(current)) >= 0) current = storage.get(index);
				else storage.add(current);

				WardrobeItemModel temp = new WardrobeItemModel();
				temp.setId(cursor.getInt(cursor.getColumnIndex(ID)));
				temp.setApi(current.getAPI());
				temp.setSkin(getSkin(cursor));
				temp.setMiscItem(getMiscItem(cursor));
				temp.setCount(cursor.getLong(cursor.getColumnIndex(COUNT)));

				//trying to add item to appropriate category
				WardrobeModel wardrobe = new WardrobeModel(temp.getCategoryType());
				wardrobe.setApi(current.getAPI());
				if ((index = current.getWardrobe().indexOf(wardrobe)) >= 0)
					wardrobe = current.getWardrobe().get(index);
				else current.getWardrobe().add(wardrobe);

				WardrobeSubModel subType;
				if (temp.getOrder() > 0)
					subType = new WardrobeSubModel(temp.getSubCategoryName(), temp.getOrder());
				else subType = new WardrobeSubModel(temp.getSubCategoryName());
				subType.setApi(current.getAPI());

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
