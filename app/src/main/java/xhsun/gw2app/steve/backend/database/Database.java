package xhsun.gw2app.steve.backend.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Template class for all database
 *
 * @author xhsun
 * @since 2017-03-29
 */
@SuppressWarnings("TryFinallyCanBeTryWithResources")
public abstract class Database<T> {
	protected Manager manager;

	protected Database(Context context) {
		Timber.i("Open connection to database");
		manager = Manager.getInstance(context);
	}

	/**
	 * insert values into database
	 *
	 * @param table  table name
	 * @param values values to insert
	 * @return true on success, false otherwise
	 */
	protected boolean insert(String table, ContentValues values) {
		SQLiteDatabase database = manager.writable();
		try {
			return database.insertOrThrow(table, null, values) > 0;
		} catch (SQLException ex) {
			Timber.e(ex, "Unable to insert for %s", table);
			return false;
		} finally {
			database.close();
		}
	}

	/**
	 * update given item in the database
	 *
	 * @param table     table name
	 * @param values    new value
	 * @param selection selection string
	 * @param args      selection arguments
	 * @return true on success, false otherwise
	 */
	protected boolean update(String table, ContentValues values, String selection, String[] args) {
		SQLiteDatabase database = manager.writable();
		try {
			return database.update(table, values, selection, args) > 0;
		} catch (SQLException ex) {
			Timber.e(ex, "Unable to update for %s", table);
			return false;
		} finally {
			database.close();
		}
	}

	/**
	 * generic insert or replace method
	 *
	 * @param table  table name
	 * @param values content value
	 * @return true on success, false otherwise
	 */
	protected boolean replace(String table, ContentValues values) {
		SQLiteDatabase database = manager.writable();
		try {
			return database.replaceOrThrow(table, null, values) > 0;
		} catch (SQLException ex) {
			Timber.e(ex, "Unable to insert or replace for %s", table);
			return false;
		} finally {
			database.close();
		}
	}

	/**
	 * delete given args from database
	 *
	 * @param table     table name
	 * @param selection selection flag
	 * @param args      things to get deleted
	 * @return true on success, false otherwise
	 */
	protected boolean delete(String table, String selection, String[] args) {
		SQLiteDatabase database = manager.writable();
		try {
			return database.delete(table, selection, args) > 0;
		} catch (SQLException ex) {
			Timber.e(ex, "Unable to delete for %s with flag %s", table, selection);
			return false;
		} finally {
			database.close();
		}
	}

	//execute get with given flags
	protected List<T> __get(String table, String flags) {
		String query = "SELECT * FROM " + table + flags;
		SQLiteDatabase database = manager.readable();
		try {
			Cursor cursor = database.rawQuery(query, null);
			try {
				return __parseGet(cursor);
			} finally {
				cursor.close();
			}
		} catch (SQLException e) {
			Timber.e(e, "Unable to find any account that match the flags (%s)", flags);
			return new ArrayList<>();
		} finally {
			database.close();
		}
	}

	protected abstract List<T> __parseGet(Cursor cursor);
}