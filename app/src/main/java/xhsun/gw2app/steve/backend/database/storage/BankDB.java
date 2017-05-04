package xhsun.gw2app.steve.backend.database.storage;

import android.content.Context;
import android.database.Cursor;

import java.util.List;

import xhsun.gw2app.steve.backend.database.Database;
import xhsun.gw2app.steve.backend.database.account.AccountDB;
import xhsun.gw2app.steve.backend.database.account.AccountInfo;
import xhsun.gw2app.steve.backend.database.common.ItemDB;

/**
 * Handle all transaction for bank table
 *
 * @author xhsun
 * @since 2017-05-04
 */

public class BankDB extends Database<AccountInfo> {
	public static final String TABLE_NAME = "storage";
	private static final String ID = "id";
	private static final String ITEM_ID = "item_id";
	private static final String SKIN_ID = "skin_id";
	private static final String ACCOUNT_KEY = "api";
	private static final String COUNT = "count";
	private static final String BINDING = "binding";
	private static final String BOUND_TO = "bound";

	public BankDB(Context context) {
		super(context);
	}

	public static String createTable() {
		return "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
				ID + " INTEGER PRIMARY KEY," +
				ITEM_ID + " INTEGER NOT NULL," +
				ACCOUNT_KEY + " TEXT NOT NULL," +
				COUNT + " INTEGER NOT NULL CHECK(" + COUNT + " >= 0)," +
				SKIN_ID + " INTEGER DEFAULT 0," +
				BINDING + " TEXT DEFAULT ''," +
				BOUND_TO + " TEXT DEFAULT ''," +
				"FOREIGN KEY (" + ITEM_ID + ") REFERENCES " + ItemDB.TABLE_NAME + "(" + ItemDB.ID + ") ON DELETE CASCADE ON UPDATE CASCADE," +
				"FOREIGN KEY (" + ACCOUNT_KEY + ") REFERENCES " + AccountDB.TABLE_NAME + "(" + AccountDB.API + ") ON DELETE CASCADE ON UPDATE CASCADE);";
	}


//	/**
//	 * get all bank item that is in the given account
//	 *
//	 * @param api    API key
//	 * @return account info that contains bank info
//	 */
//	AccountInfo getBank(String api) {
//		List<AccountInfo> list;
//		if ((list = _get(BANK_TABLE_NAME, " WHERE s." + ACCOUNT_KEY + " = '" + api + "'")).isEmpty())
//			return null;
//		return list.get(0);
//	}
//
//	/**
//	 * get all bank info for all known accounts
//	 * @return list of accounts
//	 */
//	List<AccountInfo> getAllBank() {
//		return _get(BANK_TABLE_NAME, "");
//	}

	@Override
	protected List<AccountInfo> __parseGet(Cursor cursor) {
		return null;
	}
}
