package xhsun.gw2app.steve.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * This handle all the database transactions for account
 * - once user create an new account, it cannot be modified
 * - user can delete an account if they so choose
 *
 * @author xhsun
 * @version 0.1
 * @since 2017-02-04
 */
class AccountSource {
	private AccountDBHelper helper;

	AccountSource(Context context) {
		helper = new AccountDBHelper(context);
	}

	/**
	 * create new row in the database
	 *
	 * @param api    GW2 API key
	 * @param id     GUID of the account
	 * @param usr    user name
	 * @param name   nickname
	 * @param world  world
	 * @param access access level
	 * @return true if success, false otherwise
	 */
	boolean createAccount(String api, String id, String usr, String name, String world, String access) {
		SQLiteDatabase database = null;
		try {
			database = helper.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put(AccountDBHelper.ACCOUNT_API, api);
			values.put(AccountDBHelper.ACCOUNT_ACC_ID, id);
			values.put(AccountDBHelper.ACCOUNT_ACC_NAME, usr);
			values.put(AccountDBHelper.ACCOUNT_NAME, name);
			values.put(AccountDBHelper.ACCOUNT_WORLD, world);
			values.put(AccountDBHelper.ACCOUNT_ACCESS, access);
			return database.insertOrThrow(AccountDBHelper.TABLE_NAME, null, values) > 0;
		} catch (SQLException ex) {
			ex.printStackTrace();
			return false;
		} finally {
			if (database != null) database.close();
		}
	}

	/**
	 * delete row from database using the API key
	 *
	 * @param api GW2 API key
	 * @return true if success, false otherwise
	 */
	boolean deleteAccount(String api) {
		SQLiteDatabase database = null;
		String selection = AccountDBHelper.ACCOUNT_API + " = ?";
		String[] selectionArgs = {api};
		try {
			database = helper.getWritableDatabase();
			return database.delete(AccountDBHelper.TABLE_NAME, selection, selectionArgs) > 0;
		} catch (SQLException ex) {
			ex.printStackTrace();
			return false;
		} finally {
			if (database != null) database.close();
		}
	}

	/**
	 * close connection to the database
	 */
	public void close() {
		helper.close();
	}
}
