package xhsun.gw2app.steve.backend.database.character;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;
import xhsun.gw2api.guildwars2.model.Item;
import xhsun.gw2api.guildwars2.model.character.Core;
import xhsun.gw2app.steve.backend.database.Database;
import xhsun.gw2app.steve.backend.database.account.AccountDB;

/**
 * This handle all the database transactions for character
 *
 * @author xhsun
 * @since 2017-03-29
 */
@SuppressWarnings("TryFinallyCanBeTryWithResources")
public class CharacterDB extends Database<CharacterInfo> {
	public static final String TABLE_NAME = "characters";
	static final String NAME = "name";
	private static final String ACCOUNT_KEY = "api";
	private static final String RACE = "race";
	private static final String GENDER = "gender";
	private static final String PROFESSION = "profession";
	private static final String LEVEL = "level";

	@Inject
	public CharacterDB(Context context) {
		super(context);
	}

	public static String createTable() {
		return "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
				NAME + " TEXT PRIMARY KEY NOT NULL," +
				ACCOUNT_KEY + " TEXT NOT NULL," +
				RACE + " TEXT NOT NULL," +
				GENDER + " TEXT NOT NULL," +
				PROFESSION + " TEXT NOT NULL," +
				LEVEL + " INTEGER NOT NULL CHECK(" + LEVEL + " >= 0)," +
				"FOREIGN KEY (" + ACCOUNT_KEY + ") REFERENCES " + AccountDB.TABLE_NAME + "(" + AccountDB.API + ") ON DELETE CASCADE ON UPDATE CASCADE);";
	}

//	/**
//	 * Insert if this character doesn't exist<br/>
//	 * Else, update the database
//	 *
//	 * @param name       character name
//	 * @param api        API key
//	 * @param race       race of the character
//	 * @param gender     gender of the character
//	 * @param profession profession of the character
//	 * @param level      level of the character
//	 * @return true on success, false otherwise
//	 */
//	boolean replace(String name, String api, Item.Restriction race, Core.Gender gender, Item.Restriction profession, int level) {
//		Timber.i("Start insert or replace character entry for %s", name);
//		return replace(TABLE_NAME, populateContent(name, api, race, gender, profession, level)) == 0;
//	}

	/**
	 * add new character to the database
	 * @param name       character name
	 * @param api        API key
	 * @param race       race of the character
	 * @param gender     gender of the character
	 * @param profession profession of the character
	 * @param level      level of the character
	 * @return true on success, false otherwise
	 */
	boolean add(String name, String api, Item.Restriction race, Core.Gender gender, Item.Restriction profession, int level) {
		Timber.i("Start adding character (%s) for %s", name, api);
		return insert(TABLE_NAME, populateContent(name, api, race, gender, profession, level)) > 0;
	}

	/**
	 * Update character info
	 * @param name       character name
	 * @param race       race of the character
	 * @param gender     gender of the character
	 * @param profession profession of the character
	 * @param level      level of the character
	 * @return true on success, false otherwise
	 */
	boolean update(String name, Item.Restriction race, Core.Gender gender, Item.Restriction profession, int level) {
		Timber.i("Start updating character info for %s", name);
		String selection = NAME + " = ?";
		String[] selectionArgs = {name};
		return update(TABLE_NAME, populateUpdate(race, gender, profession, level), selection, selectionArgs);
	}

	/**
	 * delete given character from the database
	 *
	 * @param name character name
	 * @return true on success, false otherwise
	 */
	boolean delete(String name) {
		Timber.i("Start deleting character %s", name);
		String selection = NAME + " = ?";
		String[] selectionArgs = {name};
		return delete(TABLE_NAME, selection, selectionArgs);
	}

	/**
	 * get character info using name
	 *
	 * @param name name of the character
	 * @return character info | null if not find
	 */
	CharacterInfo get(String name) {
		List<CharacterInfo> list;
		if ((list = super.__get(TABLE_NAME, " WHERE " + NAME + " = '" + name + "'")).isEmpty())
			return null;
		return list.get(0);
	}

	/**
	 * get all character info
	 *
	 * @return list of character info | empty on not find
	 */
	List<CharacterInfo> getAll() {
		return __get(TABLE_NAME, "");
	}

	/**
	 * get all character info for given API Key
	 *
	 * @param api API Key
	 * @return list of character info | empty on not find
	 */
	List<CharacterInfo> getAll(String api) {
		return __get(TABLE_NAME, " WHERE " + ACCOUNT_KEY + " = '" + api + "'");
	}

	//parse get result
	@Override
	protected List<CharacterInfo> __parseGet(Cursor cursor) {
		List<CharacterInfo> characters = new ArrayList<>();
		if (cursor.moveToFirst())
			while (!cursor.isAfterLast()) {
				CharacterInfo character = new CharacterInfo();
				character.setName(cursor.getString(cursor.getColumnIndex(NAME)));
				character.setApi(cursor.getString(cursor.getColumnIndex(ACCOUNT_KEY)));
				character.setGender(Core.Gender.valueOf(cursor.getString(cursor.getColumnIndex(GENDER))));
				character.setRace(Item.Restriction.valueOf(cursor.getString(cursor.getColumnIndex(RACE))));
				character.setProfession(Item.Restriction.valueOf(cursor.getString(cursor.getColumnIndex(PROFESSION))));
				character.setLevel(cursor.getInt(cursor.getColumnIndex(LEVEL)));
				characters.add(character);
				cursor.moveToNext();
			}
		return characters;
	}

	private ContentValues populateContent(String name, String api, Item.Restriction race, Core.Gender gender, Item.Restriction profession, int level) {
		ContentValues values = new ContentValues();
		values.put(NAME, name);
		values.put(ACCOUNT_KEY, api);
		values.put(RACE, race.name());
		values.put(GENDER, gender.name());
		values.put(PROFESSION, profession.name());
		values.put(LEVEL, level);
		return values;
	}

	private ContentValues populateUpdate(Item.Restriction race, Core.Gender gender, Item.Restriction profession, int level) {
		ContentValues values = new ContentValues();
		values.put(RACE, race.name());
		values.put(GENDER, gender.name());
		values.put(PROFESSION, profession.name());
		values.put(LEVEL, level);
		return values;
	}
}
