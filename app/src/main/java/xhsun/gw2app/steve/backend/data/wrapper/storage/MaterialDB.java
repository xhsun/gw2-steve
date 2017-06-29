package xhsun.gw2app.steve.backend.data.wrapper.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.annimon.stream.Stream;

import java.util.ArrayList;
import java.util.List;

import me.xhsun.guildwars2wrapper.model.v2.util.Storage;
import timber.log.Timber;
import xhsun.gw2app.steve.backend.data.model.AccountModel;
import xhsun.gw2app.steve.backend.data.model.vault.MaterialStorageModel;
import xhsun.gw2app.steve.backend.data.model.vault.item.MaterialItemModel;
import xhsun.gw2app.steve.backend.data.wrapper.account.AccountDB;
import xhsun.gw2app.steve.backend.data.wrapper.common.ItemDB;
import xhsun.gw2app.steve.backend.util.support.vault.VaultType;

/**
 * Handle all transaction for material storage table
 *
 * @author xhsun
 * @since 2017-05-04
 */

public class MaterialDB extends StorageDB<MaterialStorageModel, MaterialItemModel> {
	public static final String TABLE_NAME = "materialStorage";

	public MaterialDB(Context context) {
		super(context, VaultType.MATERIAL);
	}

	public static String createTable() {
		return "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
				ID + " INTEGER PRIMARY KEY," +
				ITEM_ID + " INTEGER NOT NULL," +
				ACCOUNT_KEY + " TEXT NOT NULL," +
				COUNT + " INTEGER NOT NULL CHECK(" + COUNT + " >= 0)," +
				BINDING + " TEXT DEFAULT ''," +
				MATERIAL_ID + " INTEGER NOT NULL," +
				MATERIAL_NAME + " TEXT NOT NULL," +
				"FOREIGN KEY (" + ITEM_ID + ") REFERENCES " + ItemDB.TABLE_NAME + "(" + ItemDB.ID + ") ON DELETE CASCADE ON UPDATE CASCADE," +
				"FOREIGN KEY (" + ACCOUNT_KEY + ") REFERENCES " + AccountDB.TABLE_NAME + "(" + AccountDB.API + ") ON DELETE CASCADE ON UPDATE CASCADE);";
	}

	@Override
	long replace(MaterialItemModel info) {
		Timber.d("Start insert or update material entry for (%s, %d)", info.getApi(), info.getItemModel().getId());
		return replaceAndReturn(TABLE_NAME, populateContent(info.getId(), info.getItemModel().getId(),
				info.getApi(), info.getCount(), info.getBinding(), info.getCategoryID(), info.getCategoryName()));
	}

	void bulkInsert(List<MaterialItemModel> data) {
		Timber.d("Start bulk insert material entry");
		List<ContentValues> values = new ArrayList<>();
		Stream.of(data).forEach(m -> values.add(populateContent(m.getId(), m.getItemModel().getId(),
				m.getApi(), m.getCount(), m.getBinding(), m.getCategoryID(), m.getCategoryName())));
		bulkInsert(TABLE_NAME, values);
	}
	/**
	 * delete given item from database
	 *
	 * @param data contains material storage id
	 * @return true on success, false otherwise
	 */
	@Override
	boolean delete(MaterialItemModel data) {
		return delete(data.getId(), TABLE_NAME);
	}

	@Override
	void bulkDelete(List<MaterialItemModel> data) {
		if (data.size() < 1) return;
		Timber.d("Start bulk delete material entry");
		bulkDelete(Stream.of(data).map(MaterialItemModel::getId).toList(), TABLE_NAME);
	}

	@Override
	List<MaterialStorageModel> get(String api) {
		List<AccountModel> list;
		if ((list = _get(TABLE_NAME, " WHERE " + ACCOUNT_KEY + " = '" + api + "'")).isEmpty())
			return new ArrayList<>();
		return list.get(0).getMaterial();
	}

	@Override
	List<AccountModel> getAll() {
		return _get(TABLE_NAME, "");
	}


	@Override
	protected List<AccountModel> __parseGet(Cursor cursor) {
		List<AccountModel> storage = new ArrayList<>();
		if (cursor.moveToFirst())
			while (!cursor.isAfterLast()) {
				int index;
				AccountModel current = new AccountModel(cursor.getString(cursor.getColumnIndex(ACCOUNT_KEY)));
				if ((index = storage.indexOf(current)) >= 0) current = storage.get(index);
				else storage.add(current);

				int id = cursor.getInt(cursor.getColumnIndex(MATERIAL_ID));
				String name = cursor.getString(cursor.getColumnIndex(MATERIAL_NAME));

				MaterialStorageModel material = new MaterialStorageModel(id, name);
				if ((index = current.getMaterial().indexOf(material)) >= 0)
					material = current.getMaterial().get(index);
				else current.getMaterial().add(material);

				MaterialItemModel temp = new MaterialItemModel();
				//fill item info
				temp.setItemData(getItem(cursor));

				//fill rest of the material item
				temp.setCategoryID(id);
				temp.setCategoryName(name);
				temp.setId(cursor.getInt(cursor.getColumnIndex(ID)));
				temp.setApi(current.getAPI());
				temp.setCount(cursor.getInt(cursor.getColumnIndex(COUNT)));

				//check if there is a bind for this item
				String binding = cursor.getString(cursor.getColumnIndex(BINDING));
				if (binding.equals("")) temp.setBinding(null);
				else temp.setBinding(Storage.Binding.valueOf(binding));

				//add storage info to account
				material.getItems().add(temp);

				cursor.moveToNext();
			}
		return storage;
	}
}
