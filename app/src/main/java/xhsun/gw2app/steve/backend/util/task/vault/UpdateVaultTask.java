package xhsun.gw2app.steve.backend.util.task.vault;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import me.xhsun.guildwars2wrapper.GuildWars2;
import me.xhsun.guildwars2wrapper.error.GuildWars2Exception;
import timber.log.Timber;
import xhsun.gw2app.steve.backend.data.model.AbstractModel;
import xhsun.gw2app.steve.backend.data.model.AccountModel;
import xhsun.gw2app.steve.backend.data.model.CharacterModel;
import xhsun.gw2app.steve.backend.data.model.vault.MaterialStorageModel;
import xhsun.gw2app.steve.backend.data.model.vault.WardrobeModel;
import xhsun.gw2app.steve.backend.data.model.vault.item.BankItemModel;
import xhsun.gw2app.steve.backend.data.model.vault.item.InventoryItemModel;
import xhsun.gw2app.steve.backend.data.wrapper.account.AccountDB;
import xhsun.gw2app.steve.backend.data.wrapper.account.AccountWrapper;
import xhsun.gw2app.steve.backend.data.wrapper.character.CharacterDB;
import xhsun.gw2app.steve.backend.data.wrapper.character.CharacterWrapper;
import xhsun.gw2app.steve.backend.data.wrapper.common.ItemDB;
import xhsun.gw2app.steve.backend.data.wrapper.common.ItemWrapper;
import xhsun.gw2app.steve.backend.data.wrapper.common.SkinDB;
import xhsun.gw2app.steve.backend.data.wrapper.common.SkinWrapper;
import xhsun.gw2app.steve.backend.data.wrapper.storage.BankDB;
import xhsun.gw2app.steve.backend.data.wrapper.storage.BankWrapper;
import xhsun.gw2app.steve.backend.data.wrapper.storage.InventoryDB;
import xhsun.gw2app.steve.backend.data.wrapper.storage.InventoryWrapper;
import xhsun.gw2app.steve.backend.data.wrapper.storage.MaterialDB;
import xhsun.gw2app.steve.backend.data.wrapper.storage.MaterialWrapper;
import xhsun.gw2app.steve.backend.data.wrapper.storage.StorageWrapper;
import xhsun.gw2app.steve.backend.data.wrapper.storage.WardrobeDB;
import xhsun.gw2app.steve.backend.data.wrapper.storage.WardrobeWrapper;
import xhsun.gw2app.steve.backend.util.support.vault.VaultType;
import xhsun.gw2app.steve.backend.util.support.vault.load.AbstractContentFragment;
import xhsun.gw2app.steve.backend.util.task.CancellableAsyncTask;

/**
 * Async task for updating vault info
 *
 * @author xhsun
 * @since 2017-04-01
 */

public class UpdateVaultTask<T extends AbstractModel> extends CancellableAsyncTask<Object, Void, List<T>> {
	private VaultType type;
	private AccountModel account;
	private CharacterModel character;
	private StorageWrapper storageWrapper;
	private AbstractContentFragment fragment;
	private boolean isChanged = false, wasEmpty = false, isRefresh = false;


	public UpdateVaultTask(@NonNull AbstractContentFragment fragment, @NonNull AccountModel account) {
		init(fragment, account);
	}

	public UpdateVaultTask(@NonNull AbstractContentFragment<AccountModel> fragment,
	                       @NonNull AccountModel account, @NonNull CharacterModel character) {
		this.character = character;
		init(fragment, account);
	}

	public UpdateVaultTask(@NonNull AbstractContentFragment fragment, @NonNull AccountModel account, boolean isRefresh) {
		this.isRefresh = isRefresh;
		init(fragment, account);
	}

	public UpdateVaultTask(@NonNull AbstractContentFragment<AccountModel> fragment,
	                       @NonNull AccountModel account, @NonNull CharacterModel character, boolean isRefresh) {
		this.isRefresh = isRefresh;
		this.character = character;
		init(fragment, account);
	}

	@Override
	protected void onCancelled() {
		Timber.i("Update %s info cancelled", type);
		storageWrapper.setCancelled(true);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected List<T> doInBackground(Object... params) {
		String key = "";
		List<T> items, original = new ArrayList<>();
		switch (type) {
			case INVENTORY:
				key = storageWrapper.concatCharacterName(account.getAPI(), character.getName());
				original = (List<T>) character.getInventory();
				break;
			case BANK:
				original = (List<T>) account.getBank();
				break;
			case MATERIAL:
				original = (List<T>) account.getMaterial();
				break;
			case WARDROBE:
				original = (List<T>) account.getWardrobe();
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

	@SuppressWarnings("unchecked")
	@Override
	protected void onPostExecute(List<T> result) {
		Timber.i("Completed update %s for %s", type, (type == VaultType.INVENTORY) ? character.getName() : account.getAPI());
		if (isCancelled) return;
		switch (type) {//update storage info for appropriate category
			case INVENTORY:
				character.setApi(account.getAPI());
				character.setInventory((List<InventoryItemModel>) result);
				break;
			case BANK:
				account.setBank((List<BankItemModel>) result);
				break;
			case MATERIAL:
				account.setMaterial((List<MaterialStorageModel>) result);
				break;
			case WARDROBE:
				account.setWardrobe((List<WardrobeModel>) result);
				break;
		}

		//notify fragment
		if (wasEmpty && isChanged) fragment.updateData(account);
		else fragment.refreshData((type == VaultType.INVENTORY) ? character : account);

		fragment.getUpdates().remove(this);
	}

	private void init(@NonNull AbstractContentFragment fragment, @NonNull AccountModel account) {
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
				storageWrapper = new MaterialWrapper(wrapper, accountWrapper, itemWrapper,
						new MaterialDB(context));
				break;
			case WARDROBE:
				storageWrapper = new WardrobeWrapper(wrapper, accountWrapper, skinWrapper,
						new WardrobeDB(context));
				break;
		}
	}
}
