package xhsun.gw2app.steve.backend.database.common;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;
import xhsun.gw2api.guildwars2.model.Item;
import xhsun.gw2api.guildwars2.model.Skin;
import xhsun.gw2app.steve.backend.data.SkinInfo;
import xhsun.gw2app.steve.backend.database.Database;

/**
 * Handles all transaction for skin table
 *
 * @author xhsun
 * @since 2017-05-04
 */

public class SkinDB extends Database<SkinInfo> {
	public static final String TABLE_NAME = "skins";
	public static final String ID = "skin_id";
	public static final String NAME = "skin_name";
	public static final String TYPE = "skin_type";
	public static final String RESTRICTION = "skin_restriction";
	public static final String ICON = "skin_icon";
	public static final String RARITY = "skin_rarity";
	public static final String DESCRIPTION = "skin_description";
	public static final String OVERRIDE = "skin_overrideRarity";
	public static final int OVERRIDING = 1;
	private static final int NOT_OVERRIDING = 0;


	public SkinDB(Context context) {
		super(context);
	}

	public static String createTable() {
		return "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
				ID + " INTEGER PRIMARY KEY NOT NULL," +
				NAME + " TEXT NOT NULL," +
				TYPE + " TEXT NOT NULL," +
				ICON + " TEXT NOT NULL," +
				RARITY + " TEXT NOT NULL," +
				RESTRICTION + " TEXT DEFAULT ''," +
				OVERRIDE + " INT NOT NULL DEFAULT " + NOT_OVERRIDING + ", " +
				DESCRIPTION + " TEXT DEFAULT '');";
	}

	/**
	 * replace or add skin to databse
	 *
	 * @param id           skin id
	 * @param name         skin name
	 * @param type         skin type
	 * @param icon         icon
	 * @param rarity       rarity
	 * @param restrictions list of restrictions
	 * @param flags        list of flags
	 * @param desc         description | empty if not find
	 * @return true on success | false otherwise
	 */
	boolean replace(long id, String name, Item.Type type, String icon, Item.Rarity rarity,
	                Item.Restriction[] restrictions, Skin.Flag[] flags, String desc) {
		Timber.d("Start insert or replace skin entry for %s", name);
		return replace(TABLE_NAME,
				populateValue(id, name, type, restrictions, icon, rarity, flags, desc)) == 0;
	}

	/**
	 * remove skin from database
	 *
	 * @param id skin id
	 * @return true on success, false otherwise
	 */
	boolean delete(long id) {
		Timber.d("Start deleting skin (%d)", id);
		String selection = ID + " = ?";
		String[] selectionArgs = {Long.toString(id)};
		return delete(TABLE_NAME, selection, selectionArgs);
	}

	/**
	 * all skins in the database
	 *
	 * @return list of skin info | empty if not find
	 */
	List<SkinInfo> getAll() {
		return __get(TABLE_NAME, "");
	}

	/**
	 * get skin base on id
	 *
	 * @param id skin id
	 * @return skin info | null if not find
	 */
	SkinInfo get(long id) {
		List<SkinInfo> list;
		if ((list = super.__get(TABLE_NAME, " WHERE " + ID + " = " + id)).isEmpty())
			return null;
		return list.get(0);
	}

	@Override
	protected List<SkinInfo> __parseGet(Cursor cursor) {
		List<SkinInfo> skins = new ArrayList<>();
		if (cursor.moveToFirst())
			while (!cursor.isAfterLast()) {
				SkinInfo skin = new SkinInfo(cursor.getLong(cursor.getColumnIndex(ID)));
				skin.setName(cursor.getString(cursor.getColumnIndex(NAME)));
				skin.setType(Item.Type.valueOf(cursor.getString(cursor.getColumnIndex(TYPE))));
				skin.setIcon(cursor.getString(cursor.getColumnIndex(ICON)));
				skin.setRarity(Item.Rarity.valueOf(cursor.getString(cursor.getColumnIndex(RARITY))));
				skin.setRestriction(toRestrictionArray(cursor.getString(cursor.getColumnIndex(RESTRICTION))));
				skin.setOverride((cursor.getInt(cursor.getColumnIndex(OVERRIDE)) == OVERRIDING));
				skin.setDescription(cursor.getString(cursor.getColumnIndex(DESCRIPTION)));
				skins.add(skin);
				cursor.moveToNext();
			}
		return skins;
	}

	private ContentValues populateValue(long id, String name, Item.Type type,
	                                    Item.Restriction[] restrictions, String icon,
	                                    Item.Rarity rarity, Skin.Flag[] flags, String desc) {
		ContentValues values = new ContentValues();
		values.put(ID, id);
		values.put(NAME, name);
		values.put(TYPE, type.name());
		if (restrictions.length > 0) values.put(RESTRICTION, arrayToString(restrictions));
		values.put(ICON, icon);
		values.put(RARITY, rarity.name());
		values.put(OVERRIDE, (SkinInfo.containOverride(flags)) ? OVERRIDING : NOT_OVERRIDING);
		if (desc != null && !desc.equals("")) values.put(DESCRIPTION, desc);
		return values;
	}

	private String arrayToString(Item.Restriction[] restrictions) {
		String out = restrictions[0].name();
		for (int i = 1; i < restrictions.length; i++)
			out += "," + restrictions[i].name();
		return out;
	}

	/**
	 * translate given string to array of restrictions
	 *
	 * @param array string
	 * @return array | empty if string is empty or not restriction
	 */
	public static Item.Restriction[] toRestrictionArray(String array) {
		if (array.equals("")) return new Item.Restriction[0];
		String[] tokens = array.split(",");
		Item.Restriction[] restrictions = new Item.Restriction[tokens.length];
		try {
			for (int i = 0; i < tokens.length; i++)
				restrictions[i] = Item.Restriction.valueOf(tokens[i]);
		} catch (IllegalArgumentException e) {
			return new Item.Restriction[0];
		}
		return restrictions;
	}
}
