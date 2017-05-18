package xhsun.gw2app.steve.backend.util.vault;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;
import xhsun.gw2api.guildwars2.GuildWars2;
import xhsun.gw2api.guildwars2.err.GuildWars2Exception;
import xhsun.gw2app.steve.backend.data.AccountData;
import xhsun.gw2app.steve.backend.data.CharacterData;
import xhsun.gw2app.steve.backend.data.StorageData;
import xhsun.gw2app.steve.backend.database.account.AccountDB;
import xhsun.gw2app.steve.backend.database.account.AccountWrapper;
import xhsun.gw2app.steve.backend.database.character.CharacterDB;
import xhsun.gw2app.steve.backend.database.character.CharacterWrapper;
import xhsun.gw2app.steve.backend.database.common.ItemDB;
import xhsun.gw2app.steve.backend.database.common.ItemWrapper;
import xhsun.gw2app.steve.backend.database.common.SkinDB;
import xhsun.gw2app.steve.backend.database.common.SkinWrapper;
import xhsun.gw2app.steve.backend.database.storage.BankDB;
import xhsun.gw2app.steve.backend.database.storage.BankWrapper;
import xhsun.gw2app.steve.backend.database.storage.InventoryDB;
import xhsun.gw2app.steve.backend.database.storage.InventoryWrapper;
import xhsun.gw2app.steve.backend.database.storage.MaterialDB;
import xhsun.gw2app.steve.backend.database.storage.MaterialWrapper;
import xhsun.gw2app.steve.backend.database.storage.StorageWrapper;
import xhsun.gw2app.steve.backend.database.storage.WardrobeDB;
import xhsun.gw2app.steve.backend.database.storage.WardrobeWrapper;
import xhsun.gw2app.steve.backend.util.CancellableAsyncTask;

/**
 * Async task for updating vault info
 *
 * @author xhsun
 * @since 2017-04-01
 */

public class UpdateVaultTask extends CancellableAsyncTask<Void, Void, List<StorageData>> {
	private VaultType type;
	private AccountData account;
	private CharacterData character;
	private StorageWrapper storageWrapper;
	private AbstractContentFragment fragment;
	private boolean isChanged = false, wasEmpty = false, isRefresh = false;


	public UpdateVaultTask(@NonNull AbstractContentFragment fragment, @NonNull AccountData account) {
		init(fragment, account);
	}

	public UpdateVaultTask(@NonNull AbstractContentFragment<AccountData> fragment,
	                       @NonNull AccountData account, @NonNull CharacterData character) {
		this.character = character;
		init(fragment, account);
	}

	public UpdateVaultTask(@NonNull AbstractContentFragment fragment, @NonNull AccountData account, boolean isRefresh) {
		this.isRefresh = isRefresh;
		init(fragment, account);
	}

	public UpdateVaultTask(@NonNull AbstractContentFragment<AccountData> fragment,
	                       @NonNull AccountData account, @NonNull CharacterData character, boolean isRefresh) {
		this.isRefresh = isRefresh;
		this.character = character;
		init(fragment, account);
	}

	@Override
	protected void onCancelled() {
		Timber.i("Update %s info cancelled", type);
		storageWrapper.setCancelled(true);
	}

	@Override
	protected List<StorageData> doInBackground(Void... params) {
		String key = "";
		List<StorageData> items, original = new ArrayList<>();
		switch (type) {
			case INVENTORY:
				key = storageWrapper.concatCharacterName(account.getAPI(), character.getName());
				original = character.getInventory();
				break;
			case BANK:
				original = account.getBank();
				break;
			case MATERIAL:
				original = account.getMaterial();
				break;
			case WARDROBE:
				original = account.getWardrobe();
				break;
		}
		if (key.equals("")) key = account.getAPI();

		if (!isRefresh && original.size() == 0) wasEmpty = true;
		try {
			items = storageWrapper.update(key);
		} catch (GuildWars2Exception ignored) {
			return original;
		}
		if (items.size() == 0) return original;

		if (!items.equals(original)) isChanged = true;
		return items;
	}

	@Override
	protected void onPostExecute(List<StorageData> result) {
		Timber.i("Completed update %s for %s", type, (type == VaultType.INVENTORY) ? character.getName() : account.getAPI());
		if (isCancelled) return;
		switch (type) {//update storage info for appropriate category
			case INVENTORY:
				character.setApi(account.getAPI());
				character.setInventory(result);
				break;
			case BANK:
				account.setBank(result);
				break;
			case MATERIAL:
				account.setMaterial(result);
				break;
			case WARDROBE:
				account.setWardrobe(result);
				break;
		}

		//notify fragment
		if (wasEmpty && isChanged) fragment.updateData(account);
		else fragment.refreshData((type == VaultType.INVENTORY) ? character : account);

		fragment.getUpdates().remove(this);
	}

	private void init(@NonNull AbstractContentFragment fragment, @NonNull AccountData account) {
		//noinspection unchecked
		fragment.getUpdates().add(this);
		this.account = account;
		this.fragment = fragment;
		this.type = fragment.getType();

		Context context = fragment.getContext();
		GuildWars2 wrapper = GuildWars2.getInstance();
		AccountWrapper accountWrapper = new AccountWrapper(new AccountDB(context), wrapper);
		ItemWrapper itemWrapper = new ItemWrapper(wrapper, new ItemDB(context));
		SkinWrapper skinWrapper = new SkinWrapper(wrapper, new SkinDB(context));
		switch (type) {
			case INVENTORY:
				CharacterWrapper characterWrapper = new CharacterWrapper(wrapper, accountWrapper,
						new CharacterDB(context));
				storageWrapper = new InventoryWrapper(wrapper, accountWrapper, characterWrapper, itemWrapper,
						skinWrapper, new InventoryDB(context));
				break;
			case BANK:
				storageWrapper = new BankWrapper(wrapper, new BankDB(context), accountWrapper,
						itemWrapper, skinWrapper);
				break;
			case MATERIAL:
				storageWrapper = new MaterialWrapper(wrapper, accountWrapper, itemWrapper, skinWrapper,
						new MaterialDB(context));
				break;
			case WARDROBE:
				storageWrapper = new WardrobeWrapper(wrapper, accountWrapper, itemWrapper, skinWrapper,
						new WardrobeDB(context));
				break;
		}
	}
}
