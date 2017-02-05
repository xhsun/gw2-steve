package xhsun.gw2app.steve.database.account;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import xhsun.gw2app.steve.database.DataBaseHelper;

/**
 * This handle all the database transactions for account
 * - once user create an new account, it cannot be modified
 * - user can delete an account if they so choose
 *
 * @author xhsun
 * @since 2017-02-05
 */
abstract class AccountDB {
	DataBaseHelper helper;

	AccountDB(Context context) {
		helper = new DataBaseHelper(context);
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
	abstract boolean createAccount(String api, String id, String usr, String name, String world, String access);

	/**
	 * delete row from database using the API key
	 *
	 * @param api GW2 API key
	 * @return true if success, false otherwise
	 */
	abstract boolean deleteAccount(String api);

	/**
	 * update account state to invalid
	 *
	 * @param api GW2 API key
	 */
	abstract boolean accountInvalid(String api);

	/**
	 * get account detail using GW2 API key
	 *
	 * @param api GW2 API key
	 * @return account detail
	 */
	Account getUsingAPI(String api) {
		if ("".equals(api)) return null;
		List<Account> list;
		if ((list = __get(" WHERE " + DataBaseHelper.ACCOUNT_API + "=" + api)).isEmpty())
			return null;
		return list.get(0);
	}

	/**
	 * get account detail using GW2 account id
	 *
	 * @param id GW2 account id
	 * @return account detail
	 */
	Account getUsingGUID(String id) {
		if ("".equals(id)) return null;
		List<Account> list;
		if ((list = __get(" WHERE " + DataBaseHelper.ACCOUNT_ACC_ID + "=" + id)).isEmpty())
			return null;
		return list.get(0);
	}

	/**
	 * return all accounts in detail
	 *
	 * @return list of all accounts
	 */
	List<Account> getAll() {
		return __get("");
	}

	/**
	 * return all valid/invalid accounts in detail
	 *
	 * @param isValid true for get all valid account, false otherwise
	 * @return list of all accounts
	 */
	List<Account> getAllWithState(boolean isValid) {
		return __get(" WHERE " + DataBaseHelper.ACCOUNT_STATE + "=" + ((isValid) ? 1 : 0));
	}

	/**
	 * get GW2 API key using GW2 account id
	 *
	 * @param id GW2 account id
	 * @return GW2 API key
	 */
	Account getAPI(String id) {
		if ("".equals(id)) return null;
		List<Account> list;
		if ((list = __getAPI(" WHERE " + DataBaseHelper.ACCOUNT_ACC_ID + "=" + id)).isEmpty())
			return null;
		return list.get(0);
	}

	/**
	 * return all API
	 *
	 * @return list of API in the database
	 */
	List<Account> getAllAPI() {
		return __getAPI("");
	}

	/**
	 * return all valid/invalid API
	 *
	 * @param isValid true for get all valid API, false otherwise
	 * @return list of API in the database
	 */
	List<Account> getAllAPIWithState(boolean isValid) {
		return __getAPI(" WHERE " + DataBaseHelper.ACCOUNT_STATE + "=" + ((isValid) ? 1 : 0));
	}

	//execute get with given flags
	protected abstract List<Account> __get(String flags);

	//parse get result
	List<Account> __parseGet(Cursor cursor) {
		String name;
		List<Account> accounts = new ArrayList<>();
		if (cursor.moveToFirst())
			while (!cursor.isAfterLast()) {
				name = cursor.getString(cursor.getColumnIndex(DataBaseHelper.ACCOUNT_NAME));
				Account account = new Account(cursor.getString(cursor.getColumnIndex(DataBaseHelper.ACCOUNT_API)),
						cursor.getString(cursor.getColumnIndex(DataBaseHelper.ACCOUNT_ACC_ID)),
						cursor.getString(cursor.getColumnIndex(DataBaseHelper.ACCOUNT_ACC_NAME)),
						((name).equals("no_name_given")) ? null : name,
						cursor.getString(cursor.getColumnIndex(DataBaseHelper.ACCOUNT_WORLD)),
						cursor.getString(cursor.getColumnIndex(DataBaseHelper.ACCOUNT_ACCESS)),
						(cursor.getInt(cursor.getColumnIndex(DataBaseHelper.ACCOUNT_STATE)) == DataBaseHelper.VALID));
				accounts.add(account);
			}
		return accounts;
	}

	//execute get API with flags
	protected abstract List<Account> __getAPI(String flags);

	//parse get api result
	List<Account> __parseGetAPI(Cursor cursor) {
		List<Account> accounts = new ArrayList<>();
		if (cursor.moveToFirst()) {
			while (!cursor.isAfterLast()) {
				Account account = new Account(cursor.getString(cursor.getColumnIndex(DataBaseHelper.ACCOUNT_API)));
				accounts.add(account);
			}
		}
		return accounts;
	}

	ContentValues populateCreateValue(String api, String id, String usr, String name, String world, String access) {
		ContentValues values = new ContentValues();
		values.put(DataBaseHelper.ACCOUNT_API, api);
		values.put(DataBaseHelper.ACCOUNT_ACC_ID, id);
		values.put(DataBaseHelper.ACCOUNT_ACC_NAME, usr);
		if (!"".equals(name)) values.put(DataBaseHelper.ACCOUNT_NAME, name);
		values.put(DataBaseHelper.ACCOUNT_WORLD, world);
		values.put(DataBaseHelper.ACCOUNT_ACCESS, access);

		return values;
	}

	/**
	 * close connection to the database
	 */
	void close() {
		helper.close();
	}
}
