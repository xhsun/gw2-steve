package xhsun.gw2app.steve.backend.database.wallet;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;
import xhsun.gw2app.steve.backend.database.Manager;
import xhsun.gw2app.steve.backend.database.account.AccountDB;

/**
 * This handle all the database transactions for wallets
 *
 * @author xhsun
 * @since 2017-03-27
 */

public class WalletDB {
	public static final String TABLE_NAME = "wallets";
	private static final String CURRENCY_ID = "currency_id";
	private static final String ACCOUNT_KEY = "account_id";
	private static final String VALUE = "value";

	private Manager manager;

	WalletDB(Context context) {
		Timber.i("Open connection to database");
		manager = Manager.getInstance(context);
	}

	public static String createTable() {
		return "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
				CURRENCY_ID + " INTEGER NOT NULL," +
				ACCOUNT_KEY + " TEXT NOT NULL," +
				VALUE + " INTEGER NOT NULL CHECK(" + VALUE + " > 0)," +
				"FOREIGN KEY (" + CURRENCY_ID + ") REFERENCES " + CurrencyDB.TABLE_NAME + "(" + CurrencyDB.ID + ") ON DELETE CASCADE ON UPDATE CASCADE," +
				"FOREIGN KEY (" + ACCOUNT_KEY + ") REFERENCES " + AccountDB.TABLE_NAME + "(" + AccountDB.API + ") ON DELETE CASCADE ON UPDATE CASCADE," +
				"PRIMARY KEY (" + CURRENCY_ID + ", " + ACCOUNT_KEY + "));";
	}

	boolean add(long id, String api, long value) {
		Timber.i("Start creating new wallet entry for (%d, %s)", id, api);
		try {
			return manager.open().insertOrThrow(TABLE_NAME, null, populateCreate(id, api, value)) > 0;
		} catch (SQLException ex) {
			Timber.e(ex, "Unable to insert wallet (%d, %s) into database", id, api);
			return false;
		} finally {
			manager.close();
		}
	}

	boolean update(long id, String api, long value) {
		ContentValues values = new ContentValues();
		values.put(VALUE, value);
		Timber.i("Start updating wallet (%d, %s)", id, api);
		String selection = CURRENCY_ID + " = ? AND " + ACCOUNT_KEY + " = ?";
		String[] selectionArgs = {Long.toString(id), api};

		try {
			return manager.open().update(TABLE_NAME, values, selection, selectionArgs) > 0;
		} catch (SQLException ex) {
			Timber.e(ex, "Unable to update wallet (%d, %s)", id, api);
			return false;
		} finally {
			manager.close();
		}
	}

	boolean delete(long id, String api) {
		Timber.i("Start deleting wallet (%d, %s)", id, api);
		String selection = CURRENCY_ID + " = ? AND " + ACCOUNT_KEY + " = ?";
		String[] selectionArgs = {Long.toString(id), api};
		try {
			return manager.open().delete(TABLE_NAME, selection, selectionArgs) > 0;
		} catch (SQLException ex) {
			Timber.e(ex, "Unable to delete wallet (%d, %s) from database", id, api);
			return false;
		} finally {
			manager.close();
		}
	}

	WalletInfo get(long id, String api) {
		if ("".equals(api)) return null;
		List<WalletInfo> list;
		if ((list = __get(" WHERE " + CURRENCY_ID + " = " + id + " AND " + ACCOUNT_KEY + " = '" + api + "'")).isEmpty())
			return null;
		return list.get(0);
	}

	List<WalletInfo> getAllByCurrency(long id) {
		return __get("WHERE " + CURRENCY_ID + " = " + id);
	}

	List<WalletInfo> getAllByAPI(String key) {
		return __get("WHERE " + ACCOUNT_KEY + " = '" + key + "'");
	}

	//execute get API with flags
	private List<WalletInfo> __get(String flags) {
		String query = "SELECT * FROM " + TABLE_NAME + flags;
		try {
			Cursor cursor = manager.open().rawQuery(query, null);
			try {
				return __parseGet(cursor);
			} finally {
				cursor.close();
			}
		} catch (SQLException e) {
			Timber.e(e, "Unable to find any that match the flags (%s)", flags);
			return new ArrayList<>();
		} finally {
			manager.close();
		}
	}

	//parse get result
	private List<WalletInfo> __parseGet(Cursor cursor) {
		List<WalletInfo> currencies = new ArrayList<>();
		if (cursor.moveToFirst())
			while (!cursor.isAfterLast()) {
				WalletInfo currency = new WalletInfo();
				currency.setCurrencyID(cursor.getLong(cursor.getColumnIndex(CURRENCY_ID)));
				currency.setApi(cursor.getString(cursor.getColumnIndex(ACCOUNT_KEY)));
				currency.setValue(cursor.getLong(cursor.getColumnIndex(VALUE)));
				currencies.add(currency);
				cursor.moveToNext();
			}
		return currencies;
	}

	private ContentValues populateCreate(long id, String api, long value) {
		ContentValues values = new ContentValues();
		values.put(CURRENCY_ID, id);
		values.put(ACCOUNT_KEY, api);
		values.put(VALUE, value);
		return values;
	}
}
