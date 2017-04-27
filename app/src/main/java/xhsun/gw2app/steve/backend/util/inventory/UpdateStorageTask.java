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
import xhsun.gw2app.steve.backend.database.character.StorageDB;
import xhsun.gw2app.steve.backend.database.character.StorageInfo;
import xhsun.gw2app.steve.backend.database.character.StorageWrapper;
import xhsun.gw2app.steve.backend.database.common.ItemDB;
import xhsun.gw2app.steve.backend.database.common.ItemWrapper;
import xhsun.gw2app.steve.backend.util.storage.StorageTask;
import xhsun.gw2app.steve.view.fragment.InventoryFragment;

/**
 * Async task for updating character inventory
 *
 * @author xhsun
 * @since 2017-04-01
 */

public class UpdateStorageTask extends StorageTask<Void, Void, List<StorageInfo>> {
	private OnLoadMoreListener provider;
	private StorageWrapper storageWrapper;
	private boolean isChanged = false, wasEmpty = false, isLoading = false;
	private CharacterInfo character;
	private AccountInfo account;

	public UpdateStorageTask(@NonNull OnLoadMoreListener provider, @NonNull AccountInfo account,
	                         @NonNull CharacterInfo character, boolean isLoading) {
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
		storageWrapper = new StorageWrapper(wrapper, accountWrapper, characterWrapper, itemWrapper,
				new StorageDB(((InventoryFragment) provider).getContext()));
	}

	@Override
	protected void onCancelled() {
		Timber.i("Retrieve character info cancelled");
		storageWrapper.setCancelled(true);
		int index = provider.getAdapter().removeData(null);
		if (index >= 0) provider.getAdapter().notifyItemRemoved(index);
	}

	@Override
	protected List<StorageInfo> doInBackground(Void... params) {
		List<StorageInfo> items = new ArrayList<>();
		if (character.getInventory().size() == 0) wasEmpty = true;
		try {
			items = storageWrapper.updateInventoryInfo(character);
		} catch (GuildWars2Exception ignored) {
		}
		if (!items.equals(character.getInventory())) isChanged = true;
		return items;
	}

	@Override
	protected void onPostExecute(List<StorageInfo> result) {
		if (isCancelled) return;
		character.setInventory(result);
		if (wasEmpty) {//character wasn't shown before
			//remove progress bar if there is any
			int index = provider.getAdapter().removeData(null);
			if (index >= 0) provider.getAdapter().notifyItemRemoved(index);
			//if something is in the inventory update and show character; else, don't bother
			if (isChanged) {
				if (isLoading) ((CharacterListAdapter) account.getChild().getAdapter()).addData(character);
				else ((CharacterListAdapter) account.getChild().getAdapter()).addDataWithoutLoad(
						account.getAllCharacterNames().indexOf(character.getName()), character);
			} else if (isLoading) provider.setLoading(false);
			//char was showing and something is changed, update storage view
		} else if (isChanged && character.getAdapter() != null) character.getAdapter().setData(result);
		provider.getUpdates().remove(this);
	}
}