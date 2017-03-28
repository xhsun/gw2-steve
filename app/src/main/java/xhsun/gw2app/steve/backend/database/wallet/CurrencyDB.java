package xhsun.gw2app.steve.backend.database.wallet;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;
import xhsun.gw2app.steve.backend.database.Manager;

/**
 * This handle all the database transactions for currencies
 *
 * @author xhsun
 * @since 2017-03-27
 */
@SuppressWarnings("TryFinallyCanBeTryWithResources")
public class CurrencyDB {
	public static final String TABLE_NAME = "currencies";
	static final String ID = "id";
	private static final String NAME = "name";
	private static final String ICON = "icon";

	private Manager manager;

	CurrencyDB(Context context) {
		Timber.i("Open connection to database");
		manager = Manager.getInstance(context);
	}

	public static String createTable() {
		return "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
				ID + " INTEGER PRIMARY KEY NOT NULL," +
				NAME + " TEXT NOT NULL," +
				ICON + " TEXT NOT NULL );";
	}

	/**
	 * Insert if this currency entry does not exist<br/>
	 * Else, try to update it
	 *
	 * @param id   currency id
	 * @param name currency name
	 * @param icon currency icon
	 * @return true on success, false otherwise
	 */
	boolean replace(long id, String name, String icon) {
		Timber.i("Start insert or replace currency entry for %s", name);
		try {
			return manager.open().replaceOrThrow(TABLE_NAME, null, populateValue(id, name, icon)) > 0;
		} catch (SQLException ex) {
			Timber.e(ex, "Unable to insert or replace currency (%s)", name);
			return false;
		} finally {
			manager.close();
		}
	}

	/**
	 * remove currency from database
	 * @param id currency id
	 * @return true on success, false otherwise
	 */
	boolean delete(long id) {
		Timber.i("Start deleting currency (%d)", id);
		String selection = ID + " = ?";
		String[] selectionArgs = {Long.toString(id)};
		try {
			return manager.open().delete(TABLE_NAME, selection, selectionArgs) > 0;
		} catch (SQLException ex) {
			Timber.e(ex, "Unable to delete currency (%d) from database", id);
			return false;
		} finally {
			manager.close();
		}
	}

	/**
	 * get all currencies currently in the database
	 *
	 * @return list of all currency
	 */
	List<CurrencyInfo> getAll() {
		String query = "SELECT * FROM " + TABLE_NAME;
		try {
			Cursor cursor = manager.open().rawQuery(query, null);
			try {
				return __parseGet(cursor);
			} finally {
				cursor.close();
			}
		} catch (SQLException e) {
			Timber.e(e, "Unable to find any currency");
			return new ArrayList<>();
		} finally {
			manager.close();
		}
	}

//TODO might need this later for transactions
//	/**
//	 * get currency info
//	 *
//	 * @param id currency id
//	 * @return currency info
//	 */
//	CurrencyInfo get(long id) {
//		List<CurrencyInfo> list;
//		String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + ID + " = " + id;
//		try {
//			Cursor cursor = manager.open().rawQuery(query, null);
//			try {
//				if ((list = __parseGet(cursor)).isEmpty()) return null;
//				return list.get(0);
//			} finally {
//				cursor.close();
//			}
//		} catch (SQLException e) {
//			Timber.e(e, "Unable to find any that have the id (%d)", id);
//			return null;
//		} finally {
//			manager.close();
//		}
//	}

	//parse get result
	private List<CurrencyInfo> __parseGet(Cursor cursor) {
		List<CurrencyInfo> currencies = new ArrayList<>();
		if (cursor.moveToFirst())
			while (!cursor.isAfterLast()) {
				CurrencyInfo currency = new CurrencyInfo();
				currency.setId(cursor.getLong(cursor.getColumnIndex(ID)));
				currency.setName(cursor.getString(cursor.getColumnIndex(NAME)));
				currency.setIcon(cursor.getString(cursor.getColumnIndex(ICON)));
				currencies.add(currency);
				cursor.moveToNext();
			}
		return currencies;
	}

	private ContentValues populateValue(long id, String name, String icon) {
		ContentValues values = new ContentValues();
		values.put(ID, id);
		values.put(NAME, name);
		values.put(ICON, icon);
		return values;
	}
}
