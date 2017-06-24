package xhsun.gw2app.steve.backend.data.wrapper;

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

	private Manager() {
	}

	static synchronized Manager getInstance(Context context) {
		Timber.d("Init Manager");
		if (instance == null) instance = new Manager();
		if (helper == null) helper = Helper.getHelper(context);
		return instance;
	}

	public SQLiteDatabase writable() {
		Timber.d("Writable database connection");
		return helper.getWritableDatabase();
	}

	SQLiteDatabase readable() {
		Timber.d("Readable database connection");
		return helper.getReadableDatabase();
	}
}
