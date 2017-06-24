package xhsun.gw2app.steve.backend.database.common;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import me.xhsun.guildwars2wrapper.model.v2.Item;
import me.xhsun.guildwars2wrapper.model.v2.Skin;
import me.xhsun.guildwars2wrapper.model.v2.util.comm.Type;
import timber.log.Timber;
import xhsun.gw2app.steve.backend.data.SkinData;
import xhsun.gw2app.steve.backend.database.Database;

/**
 * Handles all transaction for skin table
 *
 * @author xhsun
 * @since 2017-05-04
 */

public class SkinDB extends Database<SkinData> {
	public static final String TABLE_NAME = "skins";
	public static final String ID = "skin_id";
	public static final String NAME = "skin_name";
	public static final String TYPE = "skin_type";
	public static final String SUBTYPE = "skin_subtype";
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
				SUBTYPE + " TEXT," +
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
	boolean replace(int id, String name, Item.Type type, Type subtype, String icon, Item.Rarity rarity,
	                List<Item.Restriction> restrictions, List<Skin.Flag> flags, String desc) {
		Timber.d("Start insert or replace skin entry for %s", name);
		return replace(TABLE_NAME,
				populateValue(id, name, type, subtype, restrictions, icon, rarity, flags, desc)) == 0;
	}

	/**
	 * remove skin from database
	 *
	 * @param id skin id
	 * @return true on success, false otherwise
	 */
	boolean delete(int id) {
		Timber.d("Start deleting skin (%d)", id);
		String selection = ID + " = ?";
		String[] selectionArgs = {Integer.toString(id)};
		return delete(TABLE_NAME, selection, selectionArgs);
	}

	/**
	 * all skins in the database
	 *
	 * @return list of skin info | empty if not find
	 */
	List<SkinData> getAll() {
		return __get(TABLE_NAME, "");
	}

	/**
	 * get skin base on id
	 *
	 * @param id skin id
	 * @return skin info | null if not find
	 */
	SkinData get(int id) {
		List<SkinData> list;
		if ((list = super.__get(TABLE_NAME, " WHERE " + ID + " = " + id)).isEmpty())
			return null;
		return list.get(0);
	}

	@Override
	protected List<SkinData> __parseGet(Cursor cursor) {
		List<SkinData> skins = new ArrayList<>();
		if (cursor.moveToFirst())
			while (!cursor.isAfterLast()) {
				SkinData skin = new SkinData(cursor.getInt(cursor.getColumnIndex(ID)));
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

	private ContentValues populateValue(int id, String name, Item.Type type, Type subtype,
	                                    List<Item.Restriction> restrictions, String icon,
	                                    Item.Rarity rarity, List<Skin.Flag> flags, String desc) {
		ContentValues values = new ContentValues();
		values.put(ID, id);
		values.put(NAME, name);
		values.put(TYPE, type.name());
		if (subtype != null) values.put(SUBTYPE, subtype.name());
		if (restrictions != null && restrictions.size() > 0)
			values.put(RESTRICTION, arrayToString(restrictions));
		values.put(ICON, icon);
		values.put(RARITY, rarity.name());
		values.put(OVERRIDE, (SkinData.containOverride(flags)) ? OVERRIDING : NOT_OVERRIDING);
		if (desc != null && !desc.equals("")) values.put(DESCRIPTION, desc);
		return values;
	}

	private String arrayToString(List<Item.Restriction> restrictions) {
		StringBuilder out = new StringBuilder();
		for (Item.Restriction r : restrictions) out.append(r.name()).append(",");
		return out.toString().trim().substring(0, out.length() - 1);
	}

	/**
	 * translate given string to array of restrictions
	 *
	 * @param array string
	 * @return array | empty if string is empty or not restriction
	 */
	public static List<Item.Restriction> toRestrictionArray(String array) {
		if (array.equals("")) return new ArrayList<>();
		String[] tokens = array.split(",");
		List<Item.Restriction> restrictions = new ArrayList<>();
		try {
			for (String token : tokens) restrictions.add(Item.Restriction.valueOf(token));
		} catch (IllegalArgumentException e) {
			return new ArrayList<>();
		}
		return restrictions;
	}
}
