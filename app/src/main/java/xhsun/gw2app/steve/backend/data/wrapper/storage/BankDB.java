package xhsun.gw2app.steve.backend.data.wrapper.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.annimon.stream.Stream;

import java.util.ArrayList;
import java.util.List;

import me.xhsun.guildwars2wrapper.model.v2.util.Storage;
import timber.log.Timber;
import xhsun.gw2app.steve.backend.data.model.AccountModel;
import xhsun.gw2app.steve.backend.data.model.vault.item.BankItemModel;
import xhsun.gw2app.steve.backend.data.wrapper.account.AccountDB;
import xhsun.gw2app.steve.backend.data.wrapper.common.ItemDB;
import xhsun.gw2app.steve.backend.data.wrapper.common.SkinDB;
import xhsun.gw2app.steve.backend.util.support.vault.VaultType;

/**
 * Handle all transaction for bank table
 *
 * @author xhsun
 * @since 2017-05-04
 */

public class BankDB extends StorageDB<BankItemModel, BankItemModel> {
	public static final String TABLE_NAME = "bankStorage";

	public BankDB(Context context) {
		super(context, VaultType.BANK);
	}

	public static String createTable() {
		return "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
				ID + " INTEGER PRIMARY KEY," +
				ITEM_ID + " INTEGER NOT NULL," +
				ACCOUNT_KEY + " TEXT NOT NULL," +
				COUNT + " INTEGER NOT NULL CHECK(" + COUNT + " >= 0)," +
				SKIN_ID + " INTEGER DEFAULT NULL," +
				BINDING + " TEXT DEFAULT ''," +
				BOUND_TO + " TEXT DEFAULT ''," +
				"FOREIGN KEY (" + ITEM_ID + ") REFERENCES " + ItemDB.TABLE_NAME + "(" + ItemDB.ID + ") ON DELETE CASCADE ON UPDATE CASCADE," +
				"FOREIGN KEY (" + SKIN_ID + ") REFERENCES " + SkinDB.TABLE_NAME + "(" + SkinDB.ID + ") ON DELETE CASCADE ON UPDATE CASCADE," +
				"FOREIGN KEY (" + ACCOUNT_KEY + ") REFERENCES " + AccountDB.TABLE_NAME + "(" + AccountDB.API + ") ON DELETE CASCADE ON UPDATE CASCADE);";
	}

	@Override
	long replace(BankItemModel info) {
		Timber.d("Start insert or update bank entry for (%s, %d)", info.getApi(), info.getItemModel().getId());
		return replaceAndReturn(TABLE_NAME, populateContent(info.getId(), info.getItemModel().getId(),
				info.getApi(), info.getCount(), (info.getSkinModel() == null) ? -1 : info.getSkinModel().getId(),
				info.getBinding(), info.getBoundTo()));
	}

	@Override
	void bulkInsert(List<BankItemModel> data) {
		Timber.d("Start bulk insert bank entry");
		List<ContentValues> values = new ArrayList<>();
		Stream.of(data).forEach(b -> values.add(populateContent(b.getId(), b.getItemModel().getId(),
				b.getApi(), b.getCount(), (b.getSkinModel() == null) ? -1 : b.getSkinModel().getId(),
				b.getBinding(), b.getBoundTo())));
		bulkInsert(TABLE_NAME, values);
	}

	/**
	 * delete given item from database
	 *
	 * @param data contains bank id
	 * @return true on success, false otherwise
	 */
	@Override
	boolean delete(BankItemModel data) {
		return delete(data.getId(), TABLE_NAME);
	}

	@Override
	void bulkDelete(List<BankItemModel> data) {
		if (data.size() < 1) return;
		Timber.d("Start bulk delete inventory entry");
		bulkDelete(Stream.of(data).map(BankItemModel::getId).toList(), TABLE_NAME);
	}

	@Override
	List<BankItemModel> get(String api) {
		List<AccountModel> list;
		if ((list = _get(TABLE_NAME, " WHERE " + ACCOUNT_KEY + " = '" + api + "'")).isEmpty())
			return new ArrayList<>();
		return list.get(0).getBank();
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
				AccountModel current = new AccountModel(cursor.getString(cursor.getColumnIndex(ACCOUNT_KEY)));
				if (storage.contains(current)) current = storage.get(storage.indexOf(current));
				else storage.add(current);

				BankItemModel temp = new BankItemModel();
				//fill item info
				temp.setItemData(getItem(cursor));
				//fill skin info, only if it exist
				temp.setSkinData(getSkin(cursor));
				//fill rest of the storage info
				temp.setId(cursor.getInt(cursor.getColumnIndex(ID)));
				temp.setApi(current.getAPI());
				temp.setCount(cursor.getInt(cursor.getColumnIndex(COUNT)));
				//check if there is a bind for this item
				String binding = cursor.getString(cursor.getColumnIndex(BINDING));
				if (binding.equals(""))
					temp.setBinding(null);
				else temp.setBinding(Storage.Binding.valueOf(binding));
				temp.setBoundTo(cursor.getString(cursor.getColumnIndex(BOUND_TO)));
				//add storage info to account
				current.getBank().add(temp);

				cursor.moveToNext();
			}
		return storage;
	}
}
