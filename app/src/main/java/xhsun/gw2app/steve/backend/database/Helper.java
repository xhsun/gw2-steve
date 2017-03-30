package xhsun.gw2app.steve.backend.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import timber.log.Timber;
import xhsun.gw2app.steve.backend.database.account.AccountDB;
import xhsun.gw2app.steve.backend.database.character.CharacterDB;
import xhsun.gw2app.steve.backend.database.character.StorageDB;
import xhsun.gw2app.steve.backend.database.wallet.CurrencyDB;
import xhsun.gw2app.steve.backend.database.wallet.WalletDB;

/**
 * database helper for account table
 *
 * @author xhsun
 * @since 2017-02-04
 */

class Helper extends SQLiteOpenHelper {
	private static final String DATABASE = "gw2Steve";
	private static final int DATABASE_VERSION = 4;
	private static Helper instance = null;

	//singleton to make sure there is only one helper
	static synchronized Helper getHelper(Context context) {
		if (instance == null) instance = new Helper(context);
		return instance;
	}

	private Helper(Context context) {
		super(context, DATABASE, null, DATABASE_VERSION);
		Timber.i("Init helper");
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Timber.i("Creating tables if it does not exist");
		db.execSQL(AccountDB.createTable());
		db.execSQL(CurrencyDB.createTable());
		db.execSQL(WalletDB.createTable());
		db.execSQL(CharacterDB.createTable());
		db.execSQL(StorageDB.createTable());
	}

	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
		if (!db.isReadOnly()) db.execSQL("PRAGMA foreign_keys=ON;");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Timber.i("Dropping tables if it does not exist");
		String query = "DROP TABLE IF EXISTS ";
		db.execSQL(query + AccountDB.TABLE_NAME);
		db.execSQL(query + CurrencyDB.TABLE_NAME);
		db.execSQL(query + WalletDB.TABLE_NAME);
		db.execSQL(query + CharacterDB.TABLE_NAME);
		db.execSQL(query + StorageDB.TABLE_NAME);
		onCreate(db);
	}
}
