package xhsun.gw2app.steve.backend.database.wallet;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;
import xhsun.gw2app.steve.backend.database.Database;
import xhsun.gw2app.steve.backend.database.account.AccountDB;

/**
 * This handle all the database transactions for wallets
 *
 * @author xhsun
 * @since 2017-03-27
 */

public class WalletDB extends Database<WalletInfo> {
	public static final String TABLE_NAME = "wallets";
	private static final String CURRENCY_ID = "currency_id";
	private static final String ACCOUNT_KEY = "account_id";
	private static final String ACCOUNT_NAME = "name";
	private static final String VALUE = "value";

	@Inject
	public WalletDB(Context context) {
		super(context);
	}

	public static String createTable() {
		return "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
				CURRENCY_ID + " INTEGER NOT NULL," +
				ACCOUNT_KEY + " TEXT NOT NULL," +
				ACCOUNT_NAME + " TEXT NOT NULL," +
				VALUE + " INTEGER NOT NULL CHECK(" + VALUE + " > 0)," +
				"FOREIGN KEY (" + CURRENCY_ID + ") REFERENCES " + CurrencyDB.TABLE_NAME + "(" + CurrencyDB.ID + ") ON DELETE CASCADE ON UPDATE CASCADE," +
				"FOREIGN KEY (" + ACCOUNT_KEY + ") REFERENCES " + AccountDB.TABLE_NAME + "(" + AccountDB.API + ") ON DELETE CASCADE ON UPDATE CASCADE," +
				"PRIMARY KEY (" + CURRENCY_ID + ", " + ACCOUNT_KEY + "));";
	}

	/**
	 * Insert if this wallet entry does not exist<br/>
	 * Else, try to update it
	 *
	 * @param id    currency id
	 * @param api   API key
	 * @param name  account name
	 * @param value value
	 * @return true on success, false otherwise
	 */
	int replace(long id, String api, String name, long value) {
		Timber.i("Start insert or replace wallet entry for (%d, %s)", id, api);
		return replace(TABLE_NAME, populateContent(id, api, name, value));
	}

	/**
	 * remove outstanding wallet entry
	 * @param id currency id
	 * @param api API key
	 * @return true on success, false otherwise
	 */
	boolean delete(long id, String api) {
		Timber.i("Start deleting wallet (%d, %s)", id, api);
		String selection = CURRENCY_ID + " = ? AND " + ACCOUNT_KEY + " = ?";
		String[] selectionArgs = {Long.toString(id), api};
		return delete(TABLE_NAME, selection, selectionArgs);
	}

	/**
	 * get all wallet entries using currency id
	 * @param id currency id
	 * @return list of wallets | empty on not find
	 */
	List<WalletInfo> getAllByCurrency(long id) {
		return __get(TABLE_NAME, " WHERE " + CURRENCY_ID + " = " + id);
	}

	/**
	 * get all wallet entries using API key
	 * @param key API key
	 * @return list of wallets | empty on not find
	 */
	List<WalletInfo> getAllByAPI(String key) {
		return __get(TABLE_NAME, " WHERE " + ACCOUNT_KEY + " = '" + key + "'");
	}

	//parse get result
	@Override
	protected List<WalletInfo> __parseGet(Cursor cursor) {
		List<WalletInfo> currencies = new ArrayList<>();
		if (cursor.moveToFirst())
			while (!cursor.isAfterLast()) {
				WalletInfo currency = new WalletInfo();
				currency.setCurrencyID(cursor.getLong(cursor.getColumnIndex(CURRENCY_ID)));
				currency.setApi(cursor.getString(cursor.getColumnIndex(ACCOUNT_KEY)));
				currency.setAccount(cursor.getString(cursor.getColumnIndex(ACCOUNT_NAME)));
				currency.setValue(cursor.getLong(cursor.getColumnIndex(VALUE)));
				currencies.add(currency);
				cursor.moveToNext();
			}
		return currencies;
	}

	private ContentValues populateContent(long id, String api, String name, long value) {
		ContentValues values = new ContentValues();
		values.put(CURRENCY_ID, id);
		values.put(ACCOUNT_KEY, api);
		values.put(ACCOUNT_NAME, name);
		values.put(VALUE, value);
		return values;
	}
}
