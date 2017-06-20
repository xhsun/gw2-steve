package xhsun.gw2app.steve.backend.database.storage;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import me.xhsun.guildwars2wrapper.model.v2.util.Storage;
import timber.log.Timber;
import xhsun.gw2app.steve.backend.data.AccountData;
import xhsun.gw2app.steve.backend.data.vault.item.BankItemData;
import xhsun.gw2app.steve.backend.database.account.AccountDB;
import xhsun.gw2app.steve.backend.database.common.ItemDB;
import xhsun.gw2app.steve.backend.database.common.SkinDB;
import xhsun.gw2app.steve.backend.util.vault.VaultType;

/**
 * Handle all transaction for bank table
 *
 * @author xhsun
 * @since 2017-05-04
 */

public class BankDB extends StorageDB<BankItemData, BankItemData> {
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
	long replace(BankItemData info) {
		Timber.d("Start insert or update bank entry for (%s, %d)", info.getApi(), info.getItemData().getId());
		return replaceAndReturn(TABLE_NAME, populateContent(info.getId(), info.getItemData().getId(),
				info.getApi(), info.getCount(), (info.getSkinData() == null) ? -1 : info.getSkinData().getId(),
				info.getBinding(), info.getBoundTo()));
	}

	/**
	 * delete given item from database
	 *
	 * @param data contains bank id
	 * @return true on success, false otherwise
	 */
	@Override
	boolean delete(BankItemData data) {
		return delete(data.getId(), TABLE_NAME);
	}

	@Override
	List<BankItemData> get(String api) {
		List<AccountData> list;
		if ((list = _get(TABLE_NAME, " WHERE " + ACCOUNT_KEY + " = '" + api + "'")).isEmpty())
			return new ArrayList<>();
		return list.get(0).getBank();
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
				AccountData current = new AccountData(cursor.getString(cursor.getColumnIndex(ACCOUNT_KEY)));
				if (storage.contains(current)) current = storage.get(storage.indexOf(current));
				else storage.add(current);

				BankItemData temp = new BankItemData();
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
