package xhsun.gw2app.steve.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Account database helper
 *
 * @author xhsun
 * @version 0.1
 * @since 2017-02-04
 */

class AccountDBHelper extends SQLiteOpenHelper {
	static final String TABLE_NAME = "accounts";
	static final String ACCOUNT_API = "api_key";
	static final String ACCOUNT_ACC_ID = "acc_id";
	static final String ACCOUNT_NAME = "name";
	static final String ACCOUNT_ACC_NAME = "acc_name";
	static final String ACCOUNT_WORLD = "world";
	static final String ACCOUNT_ACCESS = "access";
	static final int NAME_LIMIT = 25;

	private static final String ACCOUNT_ID = "id";
	private static final int DATABASE_VERSION = 3;

	private static final String TABLE_DELETE = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";
	private static final String TABLE_CREATE = "CREATE TABLE " + TABLE_NAME + " (" +
			ACCOUNT_ID + " INT PRIMARY KEY AUTOINCREMENT NOT NULL," +
			ACCOUNT_API + " TEXT UNIQUE NOT NULL," +
			ACCOUNT_ACC_ID + " TEXT UNIQUE NOT NULL," +
			ACCOUNT_NAME + " TEXT NOT NULL DEFAULT 'no_name_given' CHECK (length(name) <= " + NAME_LIMIT + ")," +
			ACCOUNT_ACC_NAME + " TEXT NOT NULL," +
			ACCOUNT_WORLD + " TEXT NOT NULL DEFAULT 'no_world'," +
			ACCOUNT_ACCESS + " TEXT NOT NULL CHECK (access IN ('None', 'PlayForFree', 'GuildWars2', 'HeartOfThorns')));";


	AccountDBHelper(Context context) {
		super(context, TABLE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(TABLE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(TABLE_DELETE);
		onCreate(db);
	}
}
