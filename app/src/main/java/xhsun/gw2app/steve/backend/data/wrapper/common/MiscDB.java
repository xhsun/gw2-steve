package xhsun.gw2app.steve.backend.data.wrapper.common;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.annimon.stream.Stream;

import java.util.ArrayList;
import java.util.List;

import me.xhsun.guildwars2wrapper.model.identifiable.Unlockable;
import me.xhsun.guildwars2wrapper.model.v2.Mini;
import timber.log.Timber;
import xhsun.gw2app.steve.backend.data.model.MiscItemModel;
import xhsun.gw2app.steve.backend.data.wrapper.Database;

import static xhsun.gw2app.steve.backend.data.model.MiscItemModel.SPLIT;
import static xhsun.gw2app.steve.backend.data.model.MiscItemModel.formatID;

/**
 * Handle all transaction for misc table
 *
 * @author xhsun
 * @since 2017-06-27
 */

public class MiscDB extends Database<MiscItemModel> {
	public static final String TABLE_NAME = "miscItems";
	public static final String ID = "misc_id";
	public static final String NAME = "misc_name";
	public static final String ICON = "misc_icon";

	public MiscDB(Context context) {
		super(context);
	}

	public static String createTable() {
		return "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
				ID + " TEXT PRIMARY KEY NOT NULL," +
				NAME + " TEXT NOT NULL," +
				ICON + " TEXT NOT NULL );";
	}

	/**
	 * insert or update misc item in the database
	 *
	 * @param type misc item type
	 * @param id   misc item id
	 * @param name name
	 * @param icon icon
	 * @return true on success, false otherwise
	 */
	boolean replace(MiscItemModel.MiscItemType type, int id, String name, String icon) {
		Timber.d("Start insert or replace misc item entry for %s", name);
		return replace(TABLE_NAME, populateValue(type, id, name, icon)) == 0;
	}

	/**
	 * bulk insert or update misc items into database
	 *
	 * @param data items to insert or update
	 */
	void bulkReplace(MiscItemModel.MiscItemType type, List<Unlockable> data) {
		Timber.d("Start bulk insert misc item entries");
		List<ContentValues> values = new ArrayList<>();
		Stream.of(data).forEach(i -> values.add(populateValue(type, i.getId(), i.getName(), i.getIcon())));
		bulkReplace(TABLE_NAME, values);
	}

	void bulkReplaceMini(List<Mini> data) {
		Timber.d("Start bulk insert misc item entries");
		List<ContentValues> values = new ArrayList<>();
		Stream.of(data).forEach(i -> values.add(populateValue(MiscItemModel.MiscItemType.MINI, i.getId(),
				i.getName(), i.getIcon())));
		bulkReplace(TABLE_NAME, values);
	}

	/**
	 * remove misc item from database
	 *
	 * @param id   misc item id
	 * @param type misc item type
	 * @return true on success, false otherwise
	 */
	boolean delete(MiscItemModel.MiscItemType type, int id) {
		Timber.d("Start deleting misc item (%d)", id);
		String selection = ID + " = ?";
		String[] selectionArgs = {formatID(type, id)};
		return delete(TABLE_NAME, selection, selectionArgs);
	}

	/**
	 * all misc item in the database
	 *
	 * @return list of item info | empty if not find
	 */
	List<MiscItemModel> getAll() {
		return __get(TABLE_NAME, "");
	}

	/**
	 * get misc item base on id
	 *
	 * @param id   misc item id
	 * @param type misc item type
	 * @return item info | null if not find
	 */
	MiscItemModel get(MiscItemModel.MiscItemType type, int id) {
		List<MiscItemModel> list;
		if ((list = super.__get(TABLE_NAME, " WHERE " + ID + " = " + formatID(type, id))).isEmpty())
			return null;
		return list.get(0);
	}

	@Override
	protected List<MiscItemModel> __parseGet(Cursor cursor) {
		List<MiscItemModel> miscs = new ArrayList<>();
		if (cursor.moveToFirst())
			while (!cursor.isAfterLast()) {
				String[] idStr = cursor.getString(cursor.getColumnIndex(ID)).split(SPLIT);
				MiscItemModel misc = new MiscItemModel(MiscItemModel.MiscItemType.valueOf(idStr[0]), Integer.valueOf(idStr[1]));
				misc.setName(cursor.getString(cursor.getColumnIndex(NAME)));
				misc.setIcon(cursor.getString(cursor.getColumnIndex(ICON)));
				miscs.add(misc);
				cursor.moveToNext();
			}
		return miscs;
	}

	private ContentValues populateValue(MiscItemModel.MiscItemType type, int id, String name, String icon) {
		ContentValues values = new ContentValues();
		values.put(ID, formatID(type, id));
		values.put(NAME, name);
		values.put(ICON, icon);
		return values;
	}
}
