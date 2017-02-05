package xhsun.gw2app.steve.database.account;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;

import java.util.List;

import xhsun.gw2app.steve.database.DataBaseHelper;

/**
 * Actual implementation for AccountDB,
 * This version is for KITKAT or above
 *
 * @author xhsun
 * @since 2017-02-04
 */
class AccountSource extends AccountDB {
	AccountSource(Context context) {
		super(context);
	}

	@Override
	@TargetApi(Build.VERSION_CODES.KITKAT)
	boolean createAccount(String api, String id, String usr, String name, String world, String access) {
		ContentValues values = populateCreateValue(api, id, usr, name, world, access);
		try (SQLiteDatabase database = helper.getWritableDatabase()) {
			return database.insertOrThrow(DataBaseHelper.ACCOUNT_TABLE_NAME, null, values) > 0;
		} catch (SQLException ex) {
			ex.printStackTrace();
			return false;
		}
	}

	@Override
	@TargetApi(Build.VERSION_CODES.KITKAT)
	boolean deleteAccount(String api) {
		String selection = DataBaseHelper.ACCOUNT_API + " = ?";
		String[] selectionArgs = {api};
		try (SQLiteDatabase database = helper.getWritableDatabase()) {
			return database.delete(DataBaseHelper.ACCOUNT_TABLE_NAME, selection, selectionArgs) > 0;
		} catch (SQLException ex) {
			ex.printStackTrace();
			return false;
		}
	}

	@Override
	@TargetApi(Build.VERSION_CODES.KITKAT)
	boolean accountInvalid(String api) {
		String selection = DataBaseHelper.ACCOUNT_API + " = ?";
		String[] selectionArgs = {api};
		ContentValues values = new ContentValues();
		values.put(DataBaseHelper.ACCOUNT_STATE, DataBaseHelper.INVALID);
		try (SQLiteDatabase database = helper.getWritableDatabase()) {
			return database.update(DataBaseHelper.ACCOUNT_TABLE_NAME, values, selection, selectionArgs) > 0;
		} catch (SQLException ex) {
			ex.printStackTrace();
			return false;
		}
	}

	@Override
	@TargetApi(Build.VERSION_CODES.KITKAT)
	protected List<Account> __get(String flags) {
		String query = "SELECT * FROM " + DataBaseHelper.ACCOUNT_TABLE_NAME + flags;
		try (SQLiteDatabase database = helper.getWritableDatabase()) {
			try (Cursor cursor = database.rawQuery(query, null)) {
				return __parseGet(cursor);
			}
		}
	}

	@Override
	@TargetApi(Build.VERSION_CODES.KITKAT)
	protected List<Account> __getAPI(String flags) {
		String query = "SELECT " + DataBaseHelper.ACCOUNT_API + " FROM " + DataBaseHelper.ACCOUNT_TABLE_NAME + flags;
		try (SQLiteDatabase database = helper.getWritableDatabase()) {
			try (Cursor cursor = database.rawQuery(query, null)) {
				return __parseGetAPI(cursor);
			}
		}
	}
}
