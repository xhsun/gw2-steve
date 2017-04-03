package xhsun.gw2app.steve.backend.util.inventory;

import android.support.annotation.NonNull;

import java.util.List;

import timber.log.Timber;
import xhsun.gw2api.guildwars2.err.GuildWars2Exception;
import xhsun.gw2app.steve.backend.database.account.AccountInfo;
import xhsun.gw2app.steve.backend.database.character.CharacterInfo;
import xhsun.gw2app.steve.backend.database.character.StorageInfo;
import xhsun.gw2app.steve.backend.util.storage.StorageTask;

/**
 * Async task for updating character inventory
 *
 * @author xhsun
 * @since 2017-04-01
 */

class UpdateStorageTask extends StorageTask<Void, Void, List<StorageInfo>> {
	private OnLoadMoreListener provider;
	private boolean isChanged = false, wasEmpty = false;
	private CharacterInfo character;
	private AccountInfo account;

	UpdateStorageTask(@NonNull OnLoadMoreListener provider, @NonNull AccountInfo account, @NonNull CharacterInfo character) {
		this.provider = provider;
		this.account = account;
		this.character = character;
		provider.getStorageWrapper().setCancelled(false);
	}

	@Override
	protected void onCancelled() {
		Timber.i("Retrieve character info cancelled");
		provider.getStorageWrapper().setCancelled(true);
	}

	@Override
	protected List<StorageInfo> doInBackground(Void... params) {
		try {
			if (character.getInventory().size() == 0) wasEmpty = true;
			List<StorageInfo> items = provider.getStorageWrapper().updateInventoryInfo(character);
			if (!items.equals(character.getInventory())) isChanged = true;
			return items;
		} catch (GuildWars2Exception e) {
			return null;
		}
	}

	@Override
	protected void onPostExecute(List<StorageInfo> result) {
		if (isCancelled) return;
		if (result == null) {
			//TODO show error
			//there is stuff in the inventory and it's different from what is already show
		} else {
			character.setInventory(result);
			if (wasEmpty) {//character wasn't shown before
				//first remove progress bar
				provider.getAdapter().notifyItemRemoved(provider.getAdapter().removeData(null));
				//if something is in the inventory update and show character; else, don't bother
				if (isChanged) account.getAdapter().addCharacter(character);
				else provider.setLoading(false);
				//char was showing and something is changed, update storage view
			} else if (isChanged) character.getAdapter().setData(result);
		}
		provider.getUpdates().remove(this);
	}
}