package xhsun.gw2app.steve.database.account;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import xhsun.gw2api.guildwars2.model.account.Account;
import xhsun.gw2app.steve.database.DataBaseHelper;

/**
 * Actual implementation for AccountDB,
 * This version is for KITKAT or below
 *
 * @author xhsun
 * @since 2017-02-05
 */
@SuppressWarnings("TryFinallyCanBeTryWithResources")
class AccountSourceLegacy extends AccountDB {
	private final String TAG = this.getClass().getSimpleName();
	AccountSourceLegacy(Context context) {
		super(context);
	}

	@Override
	boolean createAccount(String api, String id, String name, int worldID, String world, Account.Access access) {
		SQLiteDatabase database = helper.getWritableDatabase();
		ContentValues values = populateCreateValue(api, id, name, worldID, world, access);
		try {
			return database.insertOrThrow(DataBaseHelper.ACCOUNT_TABLE_NAME, null, values) > 0;
		} catch (SQLException ex) {
			Log.w(TAG, "createAccount: unable to insert account into database");
			return false;
		} finally {
			database.close();
		}
	}

	@Override
	boolean deleteAccount(String api) {
		SQLiteDatabase database = helper.getWritableDatabase();

		String selection = DataBaseHelper.ACCOUNT_API + " = ?";
		String[] selectionArgs = {api};

		try {
			return database.delete(DataBaseHelper.ACCOUNT_TABLE_NAME, selection, selectionArgs) > 0;
		} catch (SQLException ex) {
			Log.w(TAG, "deleteAccount: unable to detele account from database");
			return false;
		} finally {
			database.close();
		}
	}

	@Override
	boolean accountInvalid(String api) {
		SQLiteDatabase database = helper.getWritableDatabase();

		String selection = DataBaseHelper.ACCOUNT_API + " = ?";
		String[] selectionArgs = {api};
		ContentValues values = new ContentValues();
		values.put(DataBaseHelper.ACCOUNT_STATE, DataBaseHelper.INVALID);

		try {
			return database.update(DataBaseHelper.ACCOUNT_TABLE_NAME, values, selection, selectionArgs) > 0;
		} catch (SQLException ex) {
			Log.w(TAG, "accountInvalid: unable to mark account as invalid in database");
			return false;
		} finally {
			database.close();
		}
	}

	@Override
	boolean updateAccount(String api, String name, int worldID, String world, Account.Access access) {
		SQLiteDatabase database = helper.getWritableDatabase();

		String selection = DataBaseHelper.ACCOUNT_API + " = ?";
		String[] selectionArgs = {api};
		ContentValues values = populateUpdate(name, worldID, world, access);
		if (values == null) {
			Log.d(TAG, "updateAccount: don't need to update");
			return false;
		}

		try {
			return database.update(DataBaseHelper.ACCOUNT_TABLE_NAME, values, selection, selectionArgs) > 0;
		} catch (SQLException ex) {
			Log.w(TAG, "updateAccount: unable to update account");
			return false;
		} finally {
			database.close();
		}
	}

	@Override
	protected List<AccountInfo> __get(String flags) {
		String query = "SELECT * FROM " + DataBaseHelper.ACCOUNT_TABLE_NAME + flags;
		SQLiteDatabase database = helper.getWritableDatabase();
		try {
			Cursor cursor = database.rawQuery(query, null);
			try {
				return __parseGet(cursor);
			} finally {
				cursor.close();
			}
		} catch (SQLException e) {
			return new ArrayList<>();
		} finally {
			database.close();
		}
	}

	@Override
	protected List<AccountInfo> __getAPI(String flags) {
		String query = "SELECT " + DataBaseHelper.ACCOUNT_API + " FROM " + DataBaseHelper.ACCOUNT_TABLE_NAME + flags;
		SQLiteDatabase database = helper.getWritableDatabase();
		try {
			Cursor cursor = database.rawQuery(query, null);
			try {
				return __parseGetAPI(cursor);
			} finally {
				cursor.close();
			}
		} catch (SQLException e) {
			return new ArrayList<>();
		} finally {
			database.close();
		}
	}
}
