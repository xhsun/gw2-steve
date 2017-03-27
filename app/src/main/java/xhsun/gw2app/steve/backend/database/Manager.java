package xhsun.gw2app.steve.backend.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Database manager for synchronized access to database<br/>
 * Base on <a href="http://instinctcoder.com/android-studio-sqlite-database-multiple-tables-example/">this</a>
 *
 * @author xhsun
 * @since 2017-03-27
 */

public class Manager {
	private static Manager instance;
	private static Helper helper;
	private SQLiteDatabase database;
	private int open = 0;

	private Manager() {
	}

	public static synchronized Manager getInstance(Context context) {
		if (instance == null) {
			instance = new Manager();
			helper = Helper.getHelper(context);
		}

		return instance;
	}

	public synchronized SQLiteDatabase open() {
		open += 1;
		if (open == 1) database = helper.getWritableDatabase();
		return database;
	}

	public synchronized void close() {
		open -= 1;
		if (open == 0) database.close();
	}
}
