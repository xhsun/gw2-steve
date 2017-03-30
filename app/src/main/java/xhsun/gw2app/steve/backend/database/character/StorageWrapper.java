package xhsun.gw2app.steve.backend.database.character;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import xhsun.gw2api.guildwars2.GuildWars2;
import xhsun.gw2api.guildwars2.err.ErrorCode;
import xhsun.gw2api.guildwars2.err.GuildWars2Exception;
import xhsun.gw2api.guildwars2.model.Item;
import xhsun.gw2api.guildwars2.model.character.CharacterInventory;
import xhsun.gw2api.guildwars2.model.character.Core;
import xhsun.gw2api.guildwars2.model.util.Bag;
import xhsun.gw2api.guildwars2.model.util.Inventory;
import xhsun.gw2api.guildwars2.model.util.Storage;
import xhsun.gw2app.steve.backend.database.account.AccountInfo;
import xhsun.gw2app.steve.backend.database.account.AccountWrapper;

/**
 * For manipulate storage items
 *
 * @author xhsun
 * @since 2017-03-29
 */

public class StorageWrapper {
	private GuildWars2 wrapper;
	private CharacterDB characterDB;
	private StorageDB storageDB;
	private AccountWrapper accountWrapper;

	@Inject
	public StorageWrapper(CharacterDB character, StorageDB storage, GuildWars2 wrapper, AccountWrapper account) {
		this.characterDB = character;
		this.storageDB = storage;
		this.wrapper = wrapper;
		accountWrapper = account;
	}

	/**
	 * update character inventory info
	 *
	 * @param character character information needed
	 * @return list of item that is in this character | empty if there is nothing
	 * @throws GuildWars2Exception error when interacting with server
	 */
	public List<StorageInfo> updateInventoryInfo(final CharacterInfo character) throws GuildWars2Exception {
		try {
			wrapper.characterInventoryProcessor(character.getApi(), character.getName(), new Callback<CharacterInventory>() {
				@Override
				public void onResponse(Call<CharacterInventory> call, Response<CharacterInventory> response) {
					if (response.isSuccessful()) {
						CharacterInventory inventory = response.body();
						for (Bag bag : inventory.getBags())
							updateStorage(new ArrayList<Storage>(bag.getInventory()), character.getApi(), character.getName(), false);
					} else {//process error responses
						try {
							processError(response.code(), response.errorBody().string(),
									new AccountInfo(character.getApi()), character.getName());
						} catch (IOException ignored) {
						}
					}
				}

				@Override
				public void onFailure(Call<CharacterInventory> call, Throwable throwable) {
				}
			});
		} catch (GuildWars2Exception e) {
			throwOrMark(e, new AccountInfo(character.getApi()));
		}
		return storageDB.getAllByHolder(character.getName());
	}

	/**
	 * update all character info for this account
	 *
	 * @param api API key
	 * @return list of all character info | empty if not find
	 * @throws GuildWars2Exception server issue
	 */
	public List<CharacterInfo> updateCharacterInfo(final String api) throws GuildWars2Exception {
		try {
			List<String> characterNames = wrapper.getAllCharacterName(api);
			for (final String name : characterNames)
				wrapper.characterInformationProcessor(api, name, new Callback<Core>() {
					@Override
					public void onResponse(Call<Core> call, Response<Core> response) {
						if (response.isSuccessful()) {
							Core info = response.body();
							characterDB.replace(name, api, info.getRace(), info.getGender(), info.getProfession(), info.getLevel());
						} else {
							try {
								processError(response.code(), response.errorBody().string(), new AccountInfo(api), name);
							} catch (IOException ignored) {
							}
						}
					}

					@Override
					public void onFailure(Call<Core> call, Throwable throwable) {
					}
				});
		} catch (GuildWars2Exception e) {
			throwOrMark(e, new AccountInfo(api));
		}

		return characterDB.getAll();
	}

	//if item is in bank: value -> category name; else: value -> character name
	private void updateStorage(List<Storage> storage, String api, String value, boolean isBank) {
		List<StorageInfo> counter = new ArrayList<>();
		List<StorageInfo> items = (isBank) ? storageDB.getAllByAPI(api) : storageDB.getAllByHolder(value);
		for (Storage s : storage) {
			if (s == null) continue;//nothing here, move on
			//String api, long itemID, String boundTo, String value, boolean isBank
			StorageInfo info = new StorageInfo();
			if (!isBank)
				info = new StorageInfo(api, s.getItemId(), ((Inventory) s).getBound_to(), value, false);
			//TODO handle when storage is an bank item/material item/shared inventory item
			if (!counter.contains(info)) {//first time see it
				if (!items.contains(info)) {//not in database
					if (handleNewItem(info, s, isBank)) counter.add(info);
				} else handleSeenItem(items, info, isBank);
			} else {
				StorageInfo old = items.get(items.indexOf(info));
				old.setCount(old.getCount() + s.getCount());
				storageDB.update(old.getId(), old.getCount(), "", isBank);
			}
		}
		//remove all outdated storage item from database
		for (StorageInfo i : items) storageDB.delete(i.getId(), isBank);
	}

	//true on successfully create new database entry | false otherwise
	private boolean handleNewItem(StorageInfo info, Storage storage, boolean isBank) {
		long result=-1;
		try {
			List<Item> item = wrapper.getItemInfo(new long[]{info.getItemID()});
			if (item.size() <= 0) return false;
			if (!isBank)
				result = storageDB.create(info.getItemID(), info.getCharacterName(), info.getApi(),
						item.get(0).getName(), storage.getCount(), info.getCategoryName(), ((Inventory) storage).getBinding(),
						(((Inventory) storage).getBound_to() == null) ? "" : ((Inventory) storage).getBound_to(), false);
			//TODO handle when storage is an bank item/material item/shared inventory item
			if (result > 0) {
				info.setId(result);
				info.setCount(storage.getCount());
				return true;
			}
		} catch (GuildWars2Exception ignored) { }
		return false;
	}

	//true on successfully update database | false otherwise
	//items is list of items from database
	private boolean handleSeenItem(List<StorageInfo> items, StorageInfo info, boolean isBank) {
		boolean result = false;
		StorageInfo original = items.get(items.indexOf(info));
		long count = original.getCount() + info.getCount();
		//long id, long count, String boundTo, boolean isBank
		if (!isBank) result = storageDB.update(original.getId(), count, info.getBoundTo(), false);
		//TODO handle when storage is an bank item/material item/shared inventory item
		if (result) {
			info.setId(original.getId());
			info.setCount(count);
		}
		items.remove(original);
		return result;
	}

	//process error response
	private void processError(int code, String message, AccountInfo account, String name) {
		try {
			throw ErrorCode.checkErrorResponse(code, message);
		} catch (GuildWars2Exception e) {
			if (e.getErrorCode() == ErrorCode.Key) {
				accountWrapper.markInvalid(account);
				characterDB.delete(name);
			} else if (e.getErrorCode() == ErrorCode.Character) {
				characterDB.delete(name);
			}
		}
	}

	//throw error for server issue or mark account as invalid
	private void throwOrMark(GuildWars2Exception e, AccountInfo info) throws GuildWars2Exception {
		switch (e.getErrorCode()) {
			case Server:
			case Limit:
				throw e;
			case Key:
				accountWrapper.markInvalid(info);
		}
	}
}
