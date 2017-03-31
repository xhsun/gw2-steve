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
import xhsun.gw2api.guildwars2.model.character.CharacterInventory;
import xhsun.gw2api.guildwars2.model.util.Bag;
import xhsun.gw2api.guildwars2.model.util.Storage;
import xhsun.gw2app.steve.backend.database.account.AccountInfo;
import xhsun.gw2app.steve.backend.database.account.AccountWrapper;
import xhsun.gw2app.steve.backend.database.common.ItemWrapper;

/**
 * For manipulate storage items
 *
 * @author xhsun
 * @since 2017-03-29
 */

public class StorageWrapper {
	private GuildWars2 wrapper;
	private StorageDB storageDB;
	private AccountWrapper accountWrapper;
	private CharacterWrapper characterWrapper;
	private ItemWrapper itemWrapper;

	@Inject
	public StorageWrapper(GuildWars2 wrapper, AccountWrapper account, CharacterWrapper characterWrapper, ItemWrapper itemWrapper, StorageDB storage) {
		this.storageDB = storage;
		this.wrapper = wrapper;
		this.characterWrapper = characterWrapper;
		this.itemWrapper = itemWrapper;
		accountWrapper = account;
	}

	/**
	 * get all storage info base
	 * @param value character name if getting inventory info | api if getting bank info
	 * @param isBank true to get bank info | false to get inventory info
	 * @return list of storage info | empty if not find
	 */
	public List<StorageInfo> getAll(String value, boolean isBank) {
		if (isBank) return storageDB.getAllByAPI(value);
		return storageDB.getAllByHolder(value);
	}

	/**
	 * update inventory info for given character
	 *
	 * @param character character info
	 * @return list of item that is in this character | empty if there is nothing
	 * @throws GuildWars2Exception error when interacting with server
	 */
	public List<StorageInfo> updateInventoryInfo(CharacterInfo character) throws GuildWars2Exception {
		final String api = character.getApi();
		final String name = character.getName();
		try {
			wrapper.characterInventoryProcessor(api, name, new Callback<CharacterInventory>() {
				@SuppressWarnings("ThrowableResultOfMethodCallIgnored")
				@Override
				public void onResponse(Call<CharacterInventory> call, Response<CharacterInventory> response) {
					if (response.isSuccessful()) {
						CharacterInventory inventory = response.body();
						for (Bag bag : inventory.getBags())
							updateStorage(new ArrayList<Storage>(bag.getInventory()), api, name, false);
					} else {//process error responses
						try {
							characterWrapper.checkCharacterValid(ErrorCode.checkErrorResponse(response.code(), response.errorBody().string()).getErrorCode(), new AccountInfo(api), name);
						} catch (IOException ignored) {
						}
					}
				}

				@Override
				public void onFailure(Call<CharacterInventory> call, Throwable throwable) {
				}
			});
		} catch (GuildWars2Exception e) {
			throwOrMark(e, new AccountInfo(api));
		}
		return getAll(name, false);
	}

	//if item is in bank: value -> category name; else: value -> character name
	private void updateStorage(List<Storage> storage, String api, String value, boolean isBank) {
		List<StorageInfo> items = (isBank) ? storageDB.getAllByAPI(api) : storageDB.getAllByHolder(value);
		for (Storage s : storage) {
			if (s == null) continue;//nothing here, move on
			StorageInfo info = new StorageInfo(s, api, value, isBank);
			update(info, (items.contains(info)) ? items.get(items.indexOf(info)).getCount() : 0, isBank);
			items.remove(info);
		}
		//remove all outdated storage item from database
		for (StorageInfo i : items) storageDB.delete(i.getId(), isBank);
	}

	//update or add storage item
	private void update(StorageInfo info, long newCount, boolean isBank) {
		info.setCount(info.getCount() + newCount);
		if (itemWrapper.get(info.getItemInfo().getId()) == null)
			itemWrapper.updateOrAdd(info.getItemInfo().getId());
		storageDB.replace(info.getItemInfo().getId(), info.getCharacterName(), info.getApi(),
				info.getCount(), info.getCategoryName(), info.getBinding(), info.getBoundTo(), isBank);
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
