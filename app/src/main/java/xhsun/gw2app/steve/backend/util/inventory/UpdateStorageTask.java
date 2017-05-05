package xhsun.gw2app.steve.backend.util.inventory;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;
import xhsun.gw2api.guildwars2.GuildWars2;
import xhsun.gw2api.guildwars2.err.GuildWars2Exception;
import xhsun.gw2app.steve.backend.database.account.AccountDB;
import xhsun.gw2app.steve.backend.database.account.AccountInfo;
import xhsun.gw2app.steve.backend.database.account.AccountWrapper;
import xhsun.gw2app.steve.backend.database.character.CharacterDB;
import xhsun.gw2app.steve.backend.database.character.CharacterInfo;
import xhsun.gw2app.steve.backend.database.character.CharacterWrapper;
import xhsun.gw2app.steve.backend.database.common.ItemDB;
import xhsun.gw2app.steve.backend.database.common.ItemWrapper;
import xhsun.gw2app.steve.backend.database.common.SkinDB;
import xhsun.gw2app.steve.backend.database.common.SkinWrapper;
import xhsun.gw2app.steve.backend.database.storage.InventoryDB;
import xhsun.gw2app.steve.backend.database.storage.InventoryWrapper;
import xhsun.gw2app.steve.backend.database.storage.StorageInfo;
import xhsun.gw2app.steve.backend.util.CancellableAsyncTask;
import xhsun.gw2app.steve.backend.util.Utility;
import xhsun.gw2app.steve.backend.util.items.OnLoadMoreListener;
import xhsun.gw2app.steve.backend.util.items.StorageGridAdapter;
import xhsun.gw2app.steve.view.fragment.InventoryFragment;

/**
 * Async task for updating character inventory
 *
 * @author xhsun
 * @since 2017-04-01
 */

public class UpdateStorageTask extends CancellableAsyncTask<Void, Void, List<StorageInfo>> {
	private OnLoadMoreListener<AccountListAdapter, AccountInfo> provider;
	private InventoryWrapper inventoryWrapper;
	private boolean isChanged = false, wasEmpty = false, isLoading = false;
	private CharacterInfo character;
	private AccountInfo account;

	public UpdateStorageTask(@NonNull OnLoadMoreListener<AccountListAdapter, AccountInfo> provider,
	                         @NonNull AccountInfo account, @NonNull CharacterInfo character,
	                         boolean isLoading) {
		this.provider = provider;
		this.account = account;
		this.character = character;
		this.isLoading = isLoading;
		//init wrappers
		GuildWars2 wrapper = GuildWars2.getInstance();
		AccountWrapper accountWrapper = new AccountWrapper(
				new AccountDB(((InventoryFragment) provider).getContext()), wrapper);
		CharacterWrapper characterWrapper = new CharacterWrapper(wrapper, accountWrapper,
				new CharacterDB(((InventoryFragment) provider).getContext()));
		ItemWrapper itemWrapper = new ItemWrapper(wrapper,
				new ItemDB(((InventoryFragment) provider).getContext()));
		SkinWrapper skinWrapper = new SkinWrapper(wrapper, new SkinDB(((InventoryFragment) provider).getContext()));
		inventoryWrapper = new InventoryWrapper(wrapper, accountWrapper, characterWrapper, itemWrapper,
				skinWrapper, new InventoryDB(((InventoryFragment) provider).getContext()));
	}

	@Override
	protected void onCancelled() {
		Timber.i("Retrieve character info cancelled");
		inventoryWrapper.setCancelled(true);
		((CharacterListAdapter) account.getChild().getAdapter()).removeData(null);
	}

	@Override
	protected List<StorageInfo> doInBackground(Void... params) {
		List<StorageInfo> items = new ArrayList<>();
		if (character.getInventory().size() == 0) wasEmpty = true;
		try {
			items = inventoryWrapper.update(character);
		} catch (GuildWars2Exception ignored) {
		}
		if (!items.equals(character.getInventory())) isChanged = true;
		return items;
	}

	@Override
	protected void onPostExecute(List<StorageInfo> result) {
		Timber.i("Update inventory info for %s is done", character.getName());
		if (isCancelled) return;
		//store inventory info and get adapter
		character.setInventory(result);
		StorageGridAdapter adapter = account.getAllCharacters()
				.get(account.getAllCharacters().indexOf(character)).getAdapter();
		//display info base on search query
		CharacterInfo query = new CharacterInfo(character);
		if (!provider.getQuery().equals(""))
			query.setInventory(Utility.filterStorage(provider.getQuery(), result));

		if (wasEmpty) {//character wasn't shown before
			//remove progress bar if there is any
			((CharacterListAdapter) account.getChild().getAdapter()).removeData(null);
			//if something is in the inventory update and show character; else, don't bother
			if (isChanged && query.getInventory().size() != 0) {
				if (isLoading) ((CharacterListAdapter) account.getChild().getAdapter()).addData(query);
				else ((CharacterListAdapter) account.getChild().getAdapter()).addDataWithoutLoad(query);
			}
			provider.setLoading(false);
			//this helps covers the case where search result block loading next inventory
			if (query.getInventory().size() == 0 && result.size() != 0)
				provider.onLoadMore(account);
			//char was showing and something is changed, update storage view
		} else if (isChanged && adapter != null && query.getInventory().size() != 0)
			adapter.setData(query.getInventory());
		provider.getUpdates().remove(this);
	}
}