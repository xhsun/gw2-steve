package xhsun.gw2app.steve.backend.database.wallet;

import android.content.Context;

import timber.log.Timber;
import xhsun.gw2app.steve.backend.database.Manager;

/**
 * Created by hannah on 27/03/17.
 */

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
}
