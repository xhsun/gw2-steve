package xhsun.gw2app.steve.backend.database.character;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import xhsun.gw2api.guildwars2.GuildWars2;
import xhsun.gw2api.guildwars2.err.ErrorCode;
import xhsun.gw2api.guildwars2.err.GuildWars2Exception;
import xhsun.gw2api.guildwars2.model.Item;
import xhsun.gw2api.guildwars2.model.Skin;
import xhsun.gw2api.guildwars2.model.character.CharacterInventory;
import xhsun.gw2api.guildwars2.model.character.Core;
import xhsun.gw2api.guildwars2.model.util.Bag;
import xhsun.gw2api.guildwars2.model.util.Inventory;
import xhsun.gw2app.steve.backend.database.account.AccountInfo;
import xhsun.gw2app.steve.backend.database.account.AccountWrapper;

/**
 * Created by hannah on 29/03/17.
 */

public class StorageWrapper {
	private GuildWars2 wrapper;
	private CharacterDB character;
	private StorageDB storage;
	private AccountWrapper accountWrapper;

	@Inject
	public StorageWrapper(CharacterDB character, StorageDB storage, GuildWars2 wrapper, AccountWrapper account) {
		this.character = character;
		this.storage = storage;
		this.wrapper = wrapper;
		accountWrapper = account;
	}

	/**
	 * update character inventory info<br/>
	 * If there is nothing, add new base on account info
	 *
	 * @return list of accounts with character inventory info | empty if there is nothing | null if there is no account
	 * @throws GuildWars2Exception error when interacting with server
	 */
	public List<AccountInfo> update() throws GuildWars2Exception {
		List<AccountInfo> accounts = accountWrapper.getAll(true);
		if (accounts.size() == 0) return null;

		for (final AccountInfo a : accounts) {
			try {
				//get all character names and update or insert into the database
				List<String> characterNames = wrapper.getAllCharacterName(a.getAPI());
				for (final String name : characterNames)
					wrapper.characterInformationProcessor(a.getAPI(), name, new Callback<Core>() {
						@Override
						public void onResponse(Call<Core> call, Response<Core> response) {
							if (response.isSuccessful()) {
								Core info = response.body();
								character.replace(name, a.getAPI(), info.getRace(), info.getGender(), info.getProfession(), info.getLevel());
							} else {
								try {
									processError(response.code(), response.errorBody().string(), a, name);
								} catch (IOException ignored) {
								}
							}
						}

						@Override
						public void onFailure(Call<Core> call, Throwable throwable) {
						}
					});

				//get all character in the database
				List<CharacterInfo> allCharacters = character.getAll();
				for (final CharacterInfo c : allCharacters) {
					if (!characterNames.contains(c.getName())) character.delete(c.getName());
					else {//getting inventory info from this character and update database
						wrapper.characterInventoryProcessor(a.getAPI(), c.getName(), new Callback<CharacterInventory>() {
							@Override
							public void onResponse(Call<CharacterInventory> call, Response<CharacterInventory> response) {
								if (response.isSuccessful()) {//get response
									CharacterInventory inventory = response.body();
									List<StorageInfo> items = new ArrayList<>();
									List<StorageInfo> dbItems = storage.getAllByHolder(c.getName());
									for (Bag b : inventory.getBags()) { //at most 8 iterations
										for (Inventory i : Arrays.asList(b.getInventory())) { // at most 20 iterations
											if (i == null) continue;//nothing here, move on
											StorageInfo s = new StorageInfo(c.getName(), i.getItemId(), i.getSkin(), i.getStats(), i.getBound_to());
											if (!items.contains(s)) {//first time see it
												String skinName = getSkinName(i.getSkin());
												if (!dbItems.contains(s)) {//and it is not in database
													if (handleNewItem(a.getAPI(), skinName, s, i)) items.add(s);
												} else {//it is in the database, update database
													handleSeenItem(dbItems, s, skinName);
												}
											} else {//already see it, increase count
												StorageInfo old = items.get(items.indexOf(s));
												old.setCount(old.getCount() + i.getCount());
												storage.update(old.getId(), old.getCount(), "", "", "", false);
											}
										}
									}
								} else {//process error responses
									try {
										processError(response.code(), response.errorBody().string(), a, c.getName());
									} catch (IOException ignored) {
									}
								}
							}

							@Override
							public void onFailure(Call<CharacterInventory> call, Throwable throwable) {
							}
						});
					}
				}
			} catch (GuildWars2Exception e) {
				switch (e.getErrorCode()) {
					case Server:
					case Limit:
						throw e;
					case Key:
						accountWrapper.markInvalid(a);
				}
			}
		}

		//TODO call getAll with condition?
		return null;
	}

	//process error response
	private void processError(int code, String message, AccountInfo account, String name) {
		try {
			throw ErrorCode.checkErrorResponse(code, message);
		} catch (GuildWars2Exception e) {
			if (e.getErrorCode() == ErrorCode.Key) {
				accountWrapper.markInvalid(account);
				character.delete(name);
			} else if (e.getErrorCode() == ErrorCode.Character) {
				character.delete(name);
			}
		}
	}

	private String getSkinName(long id) {
		String skinName = "";
		try {
			if (id != 0) {
				List<Skin> skin = wrapper.getSkinInfo(new long[]{id});
				if (skin.size() > 0) skinName = skin.get(0).getName();
			}
		} catch (GuildWars2Exception ignored) {
		}
		return skinName;
	}

	private boolean handleNewItem(String api, String skin, StorageInfo info, Inventory inventory) {
		try {
			List<Item> item = wrapper.getItemInfo(new long[]{info.getItemID()});
			if (item.size() <= 0) return false;

			long result = storage.create(info.getItemID(), info.getCharacterName(), api,
					item.get(0).getName(), inventory.getCount(), skin,
					(inventory.getStats() == null) ? "" : inventory.getStats().getName(), inventory.getBinding(),
					(inventory.getBound_to() == null) ? "" : inventory.getBound_to(), false);

			if (result > 0) {
				info.setId(result);
				info.setCount(inventory.getCount());
				return true;
			}
		} catch (GuildWars2Exception ignored) {
		}
		return false;
	}

	private boolean handleSeenItem(List<StorageInfo> items, StorageInfo info, String skin) {
		boolean result = false;
		StorageInfo original = items.get(items.indexOf(info));
		long count = original.getCount() + info.getCount();
		if (storage.update(original.getId(), count,
				skin, info.getStatsName(), info.getBoundTo(), false)) {
			info.setId(original.getId());
			info.setCount(count);
			result = true;
		}
		items.remove(original);
		return result;
	}
}
