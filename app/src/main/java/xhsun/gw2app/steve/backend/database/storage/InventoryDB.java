package xhsun.gw2app.steve.backend.database.storage;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;
import xhsun.gw2api.guildwars2.model.util.Storage;
import xhsun.gw2app.steve.backend.data.AccountInfo;
import xhsun.gw2app.steve.backend.data.CharacterInfo;
import xhsun.gw2app.steve.backend.data.StorageInfo;
import xhsun.gw2app.steve.backend.database.account.AccountDB;
import xhsun.gw2app.steve.backend.database.character.CharacterDB;
import xhsun.gw2app.steve.backend.database.common.ItemDB;
import xhsun.gw2app.steve.backend.database.common.SkinDB;
import xhsun.gw2app.steve.backend.util.vault.VaultType;

/**
 * This class handles all transaction for inventory table
 *
 * @author xhsun
 * @since 2017-03-29
 */

public class InventoryDB extends StorageDB {
	public static final String TABLE_NAME = "charInventory";

	@Inject
	public InventoryDB(Context context) {
		super(context, VaultType.INVENTORY);
	}

	public static String createTable() {
		return "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
				ID + " INTEGER PRIMARY KEY," +
				ITEM_ID + " INTEGER NOT NULL," +
				CHARACTER_NAME + " TEXT NOT NULL," +
				ACCOUNT_KEY + " TEXT NOT NULL," +
				COUNT + " INTEGER NOT NULL CHECK(" + COUNT + " >= 0)," +
				SKIN_ID + " INTEGER DEFAULT NULL," +
				BINDING + " TEXT DEFAULT ''," +
				BOUND_TO + " TEXT DEFAULT ''," +
				"FOREIGN KEY (" + ACCOUNT_KEY + ") REFERENCES " + AccountDB.TABLE_NAME + "(" + AccountDB.API + ") ON DELETE CASCADE ON UPDATE CASCADE," +
				"FOREIGN KEY (" + ITEM_ID + ") REFERENCES " + ItemDB.TABLE_NAME + "(" + ItemDB.ID + ") ON DELETE CASCADE ON UPDATE CASCADE," +
				"FOREIGN KEY (" + SKIN_ID + ") REFERENCES " + SkinDB.TABLE_NAME + "(" + SkinDB.ID + ") ON DELETE CASCADE ON UPDATE CASCADE," +
				"FOREIGN KEY (" + CHARACTER_NAME + ") REFERENCES " + CharacterDB.TABLE_NAME + "(" + CharacterDB.NAME + ") ON DELETE CASCADE ON UPDATE CASCADE);";
	}

	long replace(StorageInfo info) {
		Timber.d("Start insert or update inventory entry for (%d, %s, %s)", info.getItemInfo().getId(),
				info.getCharacterName(), info.getApi());
		return replaceAndReturn(TABLE_NAME, populateContent(info.getId(), info.getItemInfo().getId(),
				info.getCharacterName(), info.getApi(), info.getCount(),
				(info.getSkinInfo() == null) ? -1 : info.getSkinInfo().getId(), info.getBinding(),
				info.getBoundTo()));
	}

	/**
	 * delete given item from database
	 *
	 * @param id storage id
	 * @return true on success, false otherwise
	 */
	boolean delete(long id) {
		return delete(id, TABLE_NAME);
	}

	@Override
	List<StorageInfo> get(String name) {
		List<AccountInfo> list;
		if ((list = _get(TABLE_NAME, " WHERE " + CHARACTER_NAME + " = '" + name + "'")).isEmpty())
			return new ArrayList<>();
		return list.get(0).getAllCharacters().get(0).getInventory();
	}

	/**
	 * get all inventory info for all known accounts
	 *
	 * @return list of accounts
	 */
	List<AccountInfo> getAll() {
		return _get(TABLE_NAME, "");
	}

	@Override
	protected List<AccountInfo> __parseGet(Cursor cursor) {
		List<AccountInfo> storage = new ArrayList<>();
		if (cursor.moveToFirst())
			while (!cursor.isAfterLast()) {
				AccountInfo current = new AccountInfo(cursor.getString(cursor.getColumnIndex(ACCOUNT_KEY)));
				if (storage.contains(current)) current = storage.get(storage.indexOf(current));
				else storage.add(current);

				StorageInfo temp = new StorageInfo();

				List<CharacterInfo> characters = current.getAllCharacters();
				CharacterInfo character = new CharacterInfo(cursor.getString(cursor.getColumnIndex(CHARACTER_NAME)));
				if (characters.contains(character))
					character = characters.get(characters.indexOf(character));
				else characters.add(character);
				//add this item to character inventory
				character.getInventory().add(temp);
				temp.setCharacterName(character.getName());

				//fill item info
				temp.setItemInfo(getItem(cursor));
				//fill skin info, only if it exist
				temp.setSkinInfo(getSkin(cursor));
				//fill rest of the storage info
				temp.setId(cursor.getLong(cursor.getColumnIndex(ID)));
				temp.setApi(current.getAPI());
				temp.setCount(cursor.getInt(cursor.getColumnIndex(COUNT)));
				//check if there is a bind for this item
				String binding = cursor.getString(cursor.getColumnIndex(BINDING));
				if (binding.equals(""))
					temp.setBinding(null);
				else temp.setBinding(Storage.Binding.valueOf(binding));

				temp.setBoundTo(cursor.getString(cursor.getColumnIndex(BOUND_TO)));

				cursor.moveToNext();
			}
		return storage;
	}
}
