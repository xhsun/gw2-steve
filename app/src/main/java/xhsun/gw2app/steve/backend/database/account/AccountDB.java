package xhsun.gw2app.steve.backend.database.account;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;
import xhsun.gw2api.guildwars2.model.account.Account;
import xhsun.gw2app.steve.backend.database.Manager;

/**
 * This handle all the database transactions for account
 * - once user create an new account, it cannot be modified
 * - user can delete an account if they so choose
 *
 * @author xhsun
 * @since 2017-02-05
 */
@SuppressWarnings("TryFinallyCanBeTryWithResources")
public class AccountDB {
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

	private Manager manager;

	AccountDB(Context context) {
		Timber.i("Open connection to database");
		manager = Manager.getInstance(context);
	}

	public static String createTable() {
		return "CREATE TABLE IF NOT EXISTS " + ACCOUNT_TABLE_NAME + " (" +
				ACCOUNT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
				ACCOUNT_API + " TEXT UNIQUE NOT NULL," +
				ACCOUNT_ACC_ID + " TEXT UNIQUE NOT NULL," +
				ACCOUNT_NAME + " TEXT NOT NULL," +
				ACCOUNT_WORLD + " TEXT NOT NULL DEFAULT 'No World'," +
				ACCOUNT_WORLD_ID + " INT NOT NULL DEFAULT 0," +
				ACCOUNT_ACCESS + " TEST NOT NULL," +
				ACCOUNT_STATE + " INT NOT NULL DEFAULT " + VALID + ");";//0 - not accessible, 1 - accessible
	}

	/**
	 * create new row in the database
	 *
	 * @param api     GW2 API key
	 * @param id      GUID of the account
	 * @param name    nickname
	 * @param worldID id of the world
	 * @param world   world
	 * @param access  access level
	 * @return true if success, false otherwise
	 */
	boolean createAccount(String api, String id, String name, int worldID, String world, Account.Access access) {
		Timber.i("Start creating new account (%s)", api);
		ContentValues values = populateCreateValue(api, id, name, worldID, world, access);
		try {
			return manager.open().insertOrThrow(ACCOUNT_TABLE_NAME, null, values) > 0;
		} catch (SQLException ex) {
			Timber.e(ex, "Unable to insert account (%s) into database", api);
			return false;
		} finally {
			manager.close();
		}
	}

	/**
	 * delete row from database using the API key
	 *
	 * @param api GW2 API key
	 * @return true if success, false otherwise
	 */
	boolean deleteAccount(String api) {
		Timber.i("Start deleting account (%s)", api);
		String selection = ACCOUNT_API + " = ?";
		String[] selectionArgs = {api};
		try {
			return manager.open().delete(ACCOUNT_TABLE_NAME, selection, selectionArgs) > 0;
		} catch (SQLException ex) {
			Timber.e(ex, "Unable to delete account (%s) from database", api);
			return false;
		} finally {
			manager.close();
		}
	}

	/**
	 * update account state to invalid
	 *
	 * @param api GW2 API key
	 * @return true on success, false otherwise
	 */
	boolean accountInvalid(String api) {
		Timber.i("Start marking account (%s) as invalid", api);
		String selection = ACCOUNT_API + " = ?";
		String[] selectionArgs = {api};
		ContentValues values = new ContentValues();
		values.put(ACCOUNT_STATE, INVALID);

		try {
			return manager.open().update(ACCOUNT_TABLE_NAME, values, selection, selectionArgs) > 0;
		} catch (SQLException ex) {
			Timber.e(ex, "Unable to mark account (%s) as invalid", api);
			return false;
		} finally {
			manager.close();
		}
	}

	/**
	 * update information for this api
	 *
	 * @param api     API key
	 * @param name    account name | null if don't need update
	 * @param worldID id of the world | -1 if don't need update
	 * @param world   world name
	 * @param access  access level | null if don't need update
	 * @return true on success, false otherwise
	 */
	boolean updateAccount(String api, String name, int worldID, String world, Account.Access access) {
		Timber.i("Start updating account (%s)", api);
		String selection = ACCOUNT_API + " = ?";
		String[] selectionArgs = {api};
		ContentValues values = populateUpdate(name, worldID, world, access);
		if (values == null) {
			Timber.i("Account (%s) is already up to date", api);
			return false;
		}

		try {
			return manager.open().update(ACCOUNT_TABLE_NAME, values, selection, selectionArgs) > 0;
		} catch (SQLException ex) {
			Timber.e(ex, "Unable to update account (%s)", api);
			return false;
		} finally {
			manager.close();
		}
	}

	/**
	 * get account detail using GW2 API key
	 *
	 * @param api GW2 API key
	 * @return account detail | null if not find
	 */
	AccountInfo getUsingAPI(String api) {
		if ("".equals(api)) return null;
		List<AccountInfo> list;
		if ((list = __get(" WHERE " + ACCOUNT_API + " = '" + api + "'")).isEmpty())
			return null;
		return list.get(0);
	}

	/**
	 * get account detail using GW2 account id
	 *
	 * @param id GW2 account id
	 * @return account detail | null if not find
	 */
	AccountInfo getUsingGUID(String id) {
		if ("".equals(id)) return null;
		List<AccountInfo> list;
		if ((list = __get(" WHERE " + ACCOUNT_ACC_ID + " = '" + id + "'")).isEmpty())
			return null;
		return list.get(0);
	}

	/**
	 * return all accounts in detail
	 *
	 * @return list of all accounts | empty if not find
	 */
	List<AccountInfo> getAll() {
		return __get("");
	}

	/**
	 * return all valid/invalid accounts in detail
	 *
	 * @param isValid true for get all valid account, false otherwise
	 * @return list of all accounts | empty if not find
	 */
	List<AccountInfo> getAllWithState(boolean isValid) {
		return __get(" WHERE " + ACCOUNT_STATE + " = " + ((isValid) ? 1 : 0));
	}

	/**
	 * get GW2 API key using GW2 account id
	 *
	 * @param id GW2 account id
	 * @return GW2 API key | null if not find
	 */
	String getAPI(String id) {
		if ("".equals(id)) return null;
		List<String> list;
		if ((list = __getAPI(" WHERE " + ACCOUNT_ACC_ID + " = '" + id + "'")).isEmpty())
			return null;
		return list.get(0);
	}

	/**
	 * return all API
	 *
	 * @return list of API in the database | empty if not find
	 */
	List<String> getAllAPI() {
		return __getAPI("");
	}

	/**
	 * return all valid/invalid API
	 *
	 * @param isValid true for get all valid API, false otherwise
	 * @return list of API in the database | empty if not find
	 */
	List<String> getAllAPIWithState(boolean isValid) {
		return __getAPI(" WHERE " + ACCOUNT_STATE + "=" + ((isValid) ? 1 : 0));
	}

	//execute get with given flags
	private List<AccountInfo> __get(String flags) {
		String query = "SELECT * FROM " + ACCOUNT_TABLE_NAME + flags;
		try {
			Cursor cursor = manager.open().rawQuery(query, null);
			try {
				return __parseGet(cursor);
			} finally {
				cursor.close();
			}
		} catch (SQLException e) {
			Timber.e(e, "Unable to find any account that match the flags (%s)", flags);
			return new ArrayList<>();
		} finally {
			manager.close();
		}
	}

	//parse get result
	private List<AccountInfo> __parseGet(Cursor cursor) {
		List<AccountInfo> accounts = new ArrayList<>();
		if (cursor.moveToFirst())
			while (!cursor.isAfterLast()) {
				AccountInfo account = new AccountInfo(cursor.getString(cursor.getColumnIndex(ACCOUNT_API)),
						cursor.getString(cursor.getColumnIndex(ACCOUNT_ACC_ID)),
						cursor.getString(cursor.getColumnIndex(ACCOUNT_NAME)),
						cursor.getInt(cursor.getColumnIndex(ACCOUNT_WORLD_ID)),
						cursor.getString(cursor.getColumnIndex(ACCOUNT_WORLD)),
						Account.Access.valueOf(cursor.getString(cursor.getColumnIndex(ACCOUNT_ACCESS))),
						(cursor.getInt(cursor.getColumnIndex(ACCOUNT_STATE)) == VALID));
				accounts.add(account);
				cursor.moveToNext();
			}
		return accounts;
	}

	//execute get API with flags
	private List<String> __getAPI(String flags) {
		String query = "SELECT " + ACCOUNT_API + " FROM " + ACCOUNT_TABLE_NAME + flags;
		try {
			Cursor cursor = manager.open().rawQuery(query, null);
			try {
				return __parseGetAPI(cursor);
			} finally {
				cursor.close();
			}
		} catch (SQLException e) {
			Timber.e(e, "Unable to find any account that match the flags (%s)", flags);
			return new ArrayList<>();
		} finally {
			manager.close();
		}
	}

	//parse get api result
	private List<String> __parseGetAPI(Cursor cursor) {
		List<String> accounts = new ArrayList<>();
		if (cursor.moveToFirst())
			while (!cursor.isAfterLast()) {
				String API = cursor.getString(cursor.getColumnIndex(ACCOUNT_API));
				accounts.add(API);
				cursor.moveToNext();
			}
		return accounts;
	}

	private ContentValues populateCreateValue(String api, String id, String name, int worldID, String world, Account.Access access) {
		ContentValues values = new ContentValues();
		values.put(ACCOUNT_API, api);
		values.put(ACCOUNT_ACC_ID, id);
		values.put(ACCOUNT_NAME, name);
		values.put(ACCOUNT_WORLD, world);
		values.put(ACCOUNT_WORLD_ID, worldID);
		values.put(ACCOUNT_ACCESS, access.name());

		return values;
	}

	private ContentValues populateUpdate(String name, int worldID, String world, Account.Access access) {
		if (name == null && worldID == -1 && access == null) return null;
		ContentValues values = new ContentValues();
		if (name != null) values.put(ACCOUNT_NAME, name);
		if (worldID != -1) {
			values.put(ACCOUNT_WORLD, world);
			values.put(ACCOUNT_WORLD_ID, worldID);
		}
		if (access != null) values.put(ACCOUNT_ACCESS, access.name());
		return values;
	}
}
