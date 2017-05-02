package xhsun.gw2app.steve.backend.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import timber.log.Timber;

/**
 * Database manager for synchronized access to database<br/>
 * Base on <a href="http://instinctcoder.com/android-studio-sqlite-database-multiple-tables-example/">this</a>
 *
 * @author xhsun
 * @since 2017-03-27
 */

public class Manager {
	private static Manager instance = null;
	private static Helper helper = null;
	private int writableOC;
	private int readableOC;
	private SQLiteDatabase writableDB;
	private SQLiteDatabase readableDB;
	private Manager() {
	}

	public static synchronized Manager getInstance(Context context) {
		Timber.i("Init Manager");
		if (instance == null) instance = new Manager();
		if (helper == null) helper = Helper.getHelper(context);
		return instance;
	}

	public SQLiteDatabase writable() {
		Timber.i("Writable database connection");
		return helper.getWritableDatabase();
	}

	public SQLiteDatabase readable() {
		Timber.i("Readable database connection");
		return helper.getReadableDatabase();
	}
}
