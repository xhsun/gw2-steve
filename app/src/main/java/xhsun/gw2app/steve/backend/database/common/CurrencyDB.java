package xhsun.gw2app.steve.backend.database.common;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;
import xhsun.gw2app.steve.backend.data.CurrencyData;
import xhsun.gw2app.steve.backend.database.Database;

/**
 * This handle all the database transactions for currencies
 *
 * @author xhsun
 * @since 2017-03-27
 */
@SuppressWarnings("TryFinallyCanBeTryWithResources")
public class CurrencyDB extends Database<CurrencyData> {
	public static final String TABLE_NAME = "currencies";
	public static final String ID = "currency_id";
	private static final String NAME = "currency_name";
	private static final String ICON = "currency_icon";

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
	boolean replace(int id, String name, String icon) {
		Timber.d("Start insert or replace currency entry for %s", name);
		return replace(TABLE_NAME, populateValue(id, name, icon)) == 0;
	}

	/**
	 * remove currency from database
	 *
	 * @param id currency id
	 * @return true on success, false otherwise
	 */
	boolean delete(int id) {
		Timber.d("Start deleting currency (%d)", id);
		String selection = ID + " = ?";
		String[] selectionArgs = {Integer.toString(id)};
		return delete(TABLE_NAME, selection, selectionArgs);
	}

	/**
	 * get all currencies currently in the database
	 *
	 * @return list of all currency
	 */
	List<CurrencyData> getAll() {
		return __get(TABLE_NAME, "");
	}

	/**
	 * get currency info
	 *
	 * @param id currency id
	 * @return currency info
	 */
	CurrencyData get(int id) {
		List<CurrencyData> list;
		if ((list = super.__get(TABLE_NAME, " WHERE " + ID + " = " + id)).isEmpty())
			return null;
		return list.get(0);
	}

	//parse get result
	@Override
	protected List<CurrencyData> __parseGet(Cursor cursor) {
		List<CurrencyData> currencies = new ArrayList<>();
		if (cursor.moveToFirst())
			while (!cursor.isAfterLast()) {
				CurrencyData currency = new CurrencyData();
				currency.setId(cursor.getInt(cursor.getColumnIndex(ID)));
				currency.setName(cursor.getString(cursor.getColumnIndex(NAME)));
				currency.setIcon(cursor.getString(cursor.getColumnIndex(ICON)));
				currencies.add(currency);
				cursor.moveToNext();
			}
		return currencies;
	}

	private ContentValues populateValue(int id, String name, String icon) {
		ContentValues values = new ContentValues();
		values.put(ID, id);
		values.put(NAME, name);
		values.put(ICON, icon);
		return values;
	}
}
