package xhsun.gw2app.steve.backend.data.wrapper.storage;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import me.xhsun.guildwars2wrapper.model.v2.util.Storage;
import timber.log.Timber;
import xhsun.gw2app.steve.backend.data.model.AccountModel;
import xhsun.gw2app.steve.backend.data.model.CharacterModel;
import xhsun.gw2app.steve.backend.data.model.vault.item.InventoryItemModel;
import xhsun.gw2app.steve.backend.data.wrapper.account.AccountDB;
import xhsun.gw2app.steve.backend.data.wrapper.character.CharacterDB;
import xhsun.gw2app.steve.backend.data.wrapper.common.ItemDB;
import xhsun.gw2app.steve.backend.data.wrapper.common.SkinDB;
import xhsun.gw2app.steve.backend.util.support.vault.VaultType;

/**
 * This class handles all transaction for inventory table
 *
 * @author xhsun
 * @since 2017-03-29
 */

public class InventoryDB extends StorageDB<InventoryItemModel, InventoryItemModel> {
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

	long replace(InventoryItemModel info) {
		Timber.d("Start insert or update inventory entry for (%d, %s, %s)", info.getItemModel().getId(),
				info.getName(), info.getApi());
		return replaceAndReturn(TABLE_NAME, populateContent(info.getId(), info.getItemModel().getId(),
				info.getName(), info.getApi(), info.getCount(),
				(info.getSkinModel() == null) ? -1 : info.getSkinModel().getId(), info.getBinding(),
				info.getBoundTo()));
	}

	/**
	 * delete given item from database
	 *
	 * @param data contains storage id
	 * @return true on success, false otherwise
	 */
	@Override
	boolean delete(InventoryItemModel data) {
		return delete(data.getId(), TABLE_NAME);
	}

	@Override
	List<InventoryItemModel> get(String name) {
		List<AccountModel> list;
		if ((list = _get(TABLE_NAME, " WHERE " + CHARACTER_NAME + " = '" + name + "'")).isEmpty())
			return new ArrayList<>();
		return list.get(0).getAllCharacters().get(0).getInventory();
	}

	/**
	 * get all inventory info for all known accounts
	 *
	 * @return list of accounts
	 */
	List<AccountModel> getAll() {
		return _get(TABLE_NAME, "");
	}

	@Override
	protected List<AccountModel> __parseGet(Cursor cursor) {
		List<AccountModel> storage = new ArrayList<>();
		if (cursor.moveToFirst())
			while (!cursor.isAfterLast()) {
				AccountModel current = new AccountModel(cursor.getString(cursor.getColumnIndex(ACCOUNT_KEY)));
				if (storage.contains(current)) current = storage.get(storage.indexOf(current));
				else storage.add(current);

				InventoryItemModel temp = new InventoryItemModel();

				List<CharacterModel> characters = current.getAllCharacters();
				CharacterModel character = new CharacterModel(cursor.getString(cursor.getColumnIndex(CHARACTER_NAME)));
				if (characters.contains(character))
					character = characters.get(characters.indexOf(character));
				else characters.add(character);
				//add this item to character inventory
				character.getInventory().add(temp);
				temp.setName(character.getName());

				//fill item info
				temp.setItemData(getItem(cursor));
				//fill skin info, only if it exist
				temp.setSkinData(getSkin(cursor));
				//fill rest of the storage info
				temp.setId(cursor.getInt(cursor.getColumnIndex(ID)));
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
