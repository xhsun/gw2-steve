package xhsun.gw2app.steve.backend.database.common;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;
import xhsun.gw2app.steve.backend.database.Database;

/**
 * This handle all the database transactions for currencies
 *
 * @author xhsun
 * @since 2017-03-27
 */
@SuppressWarnings("TryFinallyCanBeTryWithResources")
public class CurrencyDB extends Database<CurrencyInfo> {
	public static final String TABLE_NAME = "currencies";
	public static final String ID = "id";
	private static final String NAME = "name";
	private static final String ICON = "icon";

	@Inject
	public CurrencyDB(Context context) {
		super(context);
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
		return replace(TABLE_NAME, populateValue(id, name, icon)) == 0;
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
		return delete(TABLE_NAME, selection, selectionArgs);
	}

	/**
	 * get all currencies currently in the database
	 *
	 * @return list of all currency
	 */
	List<CurrencyInfo> getAll() {
		return __get(TABLE_NAME, "");
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
//		if ((list = super.__get(TABLE_NAME, " WHERE " + ID + " = " + id)).isEmpty())
//			return null;
//		return list.get(0);
//	}

	//parse get result
	@Override
	protected List<CurrencyInfo> __parseGet(Cursor cursor) {
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
