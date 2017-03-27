package xhsun.gw2app.steve.backend.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import timber.log.Timber;
import xhsun.gw2app.steve.backend.database.account.AccountDB;
import xhsun.gw2app.steve.backend.util.Utility;

/**
 * database helper for account table
 *
 * @author xhsun
 * @since 2017-02-04
 */

class Helper extends SQLiteOpenHelper {
	private static Helper instance = null;

	//singleton to make sure there is only one helper
	static synchronized Helper getHelper(Context context) {
		if (instance == null) instance = new Helper(context);

		return instance;
	}

	private Helper(Context context) {
		super(context, Utility.DATABASE, null, Utility.DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Timber.i("Creating tables if it does not exist");
		db.execSQL(AccountDB.createTable());
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Timber.i("Dropping tables if it does not exist");
		String query = "DROP TABLE IF EXISTS ";
		db.execSQL(query + AccountDB.createTable());
		onCreate(db);
	}
}
