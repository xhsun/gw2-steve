package xhsun.gw2app.steve.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * database helper
 *
 * @author xhsun
 * @since 2017-02-04
 */

public class DataBaseHelper extends SQLiteOpenHelper {
	public static final String ACCOUNT_TABLE_NAME = "accounts";
	public static final String ACCOUNT_API = "api_key";
	public static final String ACCOUNT_ACC_ID = "acc_id";
	public static final String ACCOUNT_NAME = "name";
	public static final String ACCOUNT_ACC_NAME = "acc_name";
	public static final String ACCOUNT_WORLD = "world";
	public static final String ACCOUNT_ACCESS = "access";
	public static final String ACCOUNT_STATE = "state";
	public static final int NAME_LIMIT = 25;
	public static final int VALID = 1;
	public static final int INVALID = 0;

	private static final String ACCOUNT_ID = "id";
	private static final int DATABASE_VERSION = 3;
	private static final String DATABASE = "gw2_steve";

	private static final String TABLE_CREATE = "CREATE TABLE IF NOT EXISTS " + ACCOUNT_TABLE_NAME + " (" +
			ACCOUNT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
			ACCOUNT_API + " TEXT UNIQUE NOT NULL," +
			ACCOUNT_ACC_ID + " TEXT UNIQUE NOT NULL," +
			ACCOUNT_NAME + " TEXT NOT NULL DEFAULT 'no_name_given' CHECK (length(name) <= " + NAME_LIMIT + ")," +
			ACCOUNT_ACC_NAME + " TEXT NOT NULL," +
			ACCOUNT_WORLD + " TEXT NOT NULL DEFAULT 'no_world'," +
			ACCOUNT_ACCESS + " TEXT NOT NULL CHECK (access IN ('None', 'PlayForFree', 'GuildWars2', 'HeartOfThorns'))," +
			ACCOUNT_STATE + " INT NOT NULL DEFAULT 1);";//0 - not accessible, 1 - accessible


	public DataBaseHelper(Context context) {
		super(context, DATABASE, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(TABLE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		String query = "DROP TABLE IF EXISTS ";
		db.execSQL(query + ACCOUNT_TABLE_NAME);
		onCreate(db);
	}
}
