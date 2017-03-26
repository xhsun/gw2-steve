package xhsun.gw2app.steve.backend.database.account;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import timber.log.Timber;
import xhsun.gw2app.steve.backend.util.Utility;

/**
 * database helper for account table
 *
 * @author xhsun
 * @since 2017-02-04
 */

class Helper extends SQLiteOpenHelper {
	private static Helper instance = null;
	public static final String ACCOUNT_TABLE_NAME = "accounts";
	private static final String ACCOUNT_ID = "id";
	public static final String ACCOUNT_API = "api_key";
	public static final String ACCOUNT_ACC_ID = "acc_id";
	public static final String ACCOUNT_NAME = "name";
	public static final String ACCOUNT_WORLD = "world";
	public static final String ACCOUNT_WORLD_ID = "world_id";
	public static final String ACCOUNT_ACCESS = "access";
	public static final String ACCOUNT_STATE = "state";
	public static final int VALID = 1;
	public static final int INVALID = 0;

	private static final String TABLE_CREATE = "CREATE TABLE IF NOT EXISTS " + ACCOUNT_TABLE_NAME + " (" +
			ACCOUNT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
			ACCOUNT_API + " TEXT UNIQUE NOT NULL," +
			ACCOUNT_ACC_ID + " TEXT UNIQUE NOT NULL," +
			ACCOUNT_NAME + " TEXT NOT NULL," +
			ACCOUNT_WORLD + " TEXT NOT NULL DEFAULT 'No World'," +
			ACCOUNT_WORLD_ID + " INT NOT NULL DEFAULT 0," +
			ACCOUNT_ACCESS + " TEST NOT NULL," +
			ACCOUNT_STATE + " INT NOT NULL DEFAULT " + VALID + ");";//0 - not accessible, 1 - accessible

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
		Timber.i("Creating Accounts table if it does not exist");
		db.execSQL(TABLE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Timber.i("Dropping Accounts table if it does not exist");
		String query = "DROP TABLE IF EXISTS ";
		db.execSQL(query + ACCOUNT_TABLE_NAME);
		onCreate(db);
	}
}
