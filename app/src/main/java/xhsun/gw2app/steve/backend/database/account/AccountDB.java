package xhsun.gw2app.steve.backend.database.account;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import me.xhsun.guildwars2wrapper.model.v2.account.Account;
import timber.log.Timber;
import xhsun.gw2app.steve.backend.data.AccountData;
import xhsun.gw2app.steve.backend.database.Database;

/**
 * This handle all the database transactions for account
 * - once user create an new account, it cannot be modified
 * - user can delete an account if they so choose
 *
 * @author xhsun
 * @since 2017-02-05
 */
@SuppressWarnings("TryFinallyCanBeTryWithResources")
public class AccountDB extends Database<AccountData> {
	public static final String TABLE_NAME = "accounts";
	public static final String API = "api_key";
	private static final String ACCOUNT_ID = "acc_id";
	private static final String NAME = "name";
	private static final String WORLD = "world";
	private static final String WORLD_ID = "world_id";
	private static final String ACCESS = "access";
	private static final String STATE = "state";
	private static final int VALID = 1;
	private static final int INVALID = 0;

	@Inject
	public AccountDB(Context context) {
		super(context);
	}

	public static String createTable() {
		return "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
				API + " TEXT PRIMARY KEY NOT NULL," +
				ACCOUNT_ID + " TEXT UNIQUE NOT NULL," +
				NAME + " TEXT NOT NULL," +
				WORLD + " TEXT NOT NULL DEFAULT 'No World'," +
				WORLD_ID + " INT NOT NULL DEFAULT 0," +
				ACCESS + " TEST NOT NULL," +
				STATE + " INT NOT NULL DEFAULT " + VALID + ");";//0 - not accessible, 1 - accessible
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
		Timber.d("Start creating new account (%s)", api);
		return insert(TABLE_NAME, populateCreateValue(api, id, name, worldID, world, access)) > 0;
	}

	/**
	 * delete row from database using the API key
	 *
	 * @param api GW2 API key
	 * @return true if success, false otherwise
	 */
	boolean deleteAccount(String api) {
		Timber.d("Start deleting account (%s)", api);
		String selection = API + " = ?";
		String[] selectionArgs = {api};
		return delete(TABLE_NAME, selection, selectionArgs);
	}

	/**
	 * update account state to invalid
	 *
	 * @param api GW2 API key
	 * @return true on success, false otherwise
	 */
	boolean accountInvalid(String api) {
		Timber.d("Start marking account (%s) as invalid", api);
		SQLiteDatabase database = manager.writable();
		String selection = API + " = ?";
		String[] selectionArgs = {api};
		ContentValues values = new ContentValues();
		values.put(STATE, INVALID);

		try {
			return database.update(TABLE_NAME, values, selection, selectionArgs) > 0;
		} catch (SQLException ex) {
			Timber.e(ex, "Unable to mark account (%s) as invalid", api);
			return false;
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
		Timber.d("Start updating account (%s)", api);
		String selection = API + " = ?";
		String[] selectionArgs = {api};
		ContentValues values = populateUpdate(name, worldID, world, access);
		if (values == null) {
			Timber.d("Account (%s) is already up to date", api);
			return false;
		}

		return update(TABLE_NAME, values, selection, selectionArgs);
	}

	/**
	 * get account detail using GW2 API key
	 *
	 * @param api GW2 API key
	 * @return account detail | null if not find
	 */
	AccountData getUsingAPI(String api) {
		if ("".equals(api)) return null;
		List<AccountData> list;
		if ((list = __get(TABLE_NAME, " WHERE " + API + " = '" + api + "'")).isEmpty())
			return null;
		return list.get(0);
	}

	/**
	 * get account detail using GW2 account id
	 *
	 * @param id GW2 account id
	 * @return account detail | null if not find
	 */
	AccountData getUsingGUID(String id) {
		if ("".equals(id)) return null;
		List<AccountData> list;
		if ((list = __get(TABLE_NAME, " WHERE " + ACCOUNT_ID + " = '" + id + "'")).isEmpty())
			return null;
		return list.get(0);
	}

	/**
	 * return all accounts in detail
	 *
	 * @return list of all accounts | empty if not find
	 */
	List<AccountData> getAll() {
		return __get(TABLE_NAME, "");
	}

	/**
	 * return all valid/invalid accounts in detail
	 *
	 * @param isValid true for get all valid account, false otherwise
	 * @return list of all accounts | empty if not find
	 */
	List<AccountData> getAllWithState(boolean isValid) {
		return __get(TABLE_NAME, " WHERE " + STATE + " = " + ((isValid) ? 1 : 0));
	}

	//parse get result
	@Override
	protected List<AccountData> __parseGet(Cursor cursor) {
		List<AccountData> accounts = new ArrayList<>();
		if (cursor.moveToFirst())
			while (!cursor.isAfterLast()) {
				AccountData account = new AccountData(cursor.getString(cursor.getColumnIndex(API)),
						cursor.getString(cursor.getColumnIndex(ACCOUNT_ID)),
						cursor.getString(cursor.getColumnIndex(NAME)),
						cursor.getInt(cursor.getColumnIndex(WORLD_ID)),
						cursor.getString(cursor.getColumnIndex(WORLD)),
						Account.Access.valueOf(cursor.getString(cursor.getColumnIndex(ACCESS))),
						(cursor.getInt(cursor.getColumnIndex(STATE)) == VALID));
				accounts.add(account);
				cursor.moveToNext();
			}
		return accounts;
	}

	private ContentValues populateCreateValue(String api, String id, String name, int worldID, String world, Account.Access access) {
		ContentValues values = new ContentValues();
		values.put(API, api);
		values.put(ACCOUNT_ID, id);
		values.put(NAME, name);
		values.put(WORLD, world);
		values.put(WORLD_ID, worldID);
		values.put(ACCESS, access.name());

		return values;
	}

	private ContentValues populateUpdate(String name, int worldID, String world, Account.Access access) {
		if (name == null && worldID == -1 && access == null) return null;
		ContentValues values = new ContentValues();
		if (name != null) values.put(NAME, name);
		if (worldID != -1) {
			values.put(WORLD, world);
			values.put(WORLD_ID, worldID);
		}
		if (access != null) values.put(ACCESS, access.name());
		return values;
	}
}
