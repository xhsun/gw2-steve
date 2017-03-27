package xhsun.gw2app.steve.backend.database.wallet;

import android.content.Context;

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
	private static final String ID = "id";
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
				ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
				CURRENCY_ID + " INTEGER NOT NULL," +
				ACCOUNT_KEY + " TEXT NOT NULL," +
				VALUE + " INTEGER NOT NULL CHECK(" + VALUE + " > 0)," +
				"FOREIGN KEY (" + CURRENCY_ID + ") REFERENCES " + CurrencyDB.TABLE_NAME + "(" + CurrencyDB.ID + ")," +
				"FOREIGN KEY (" + ACCOUNT_KEY + ") REFERENCES " + AccountDB.TABLE_NAME + "(" + AccountDB.API + "));";
	}
}
