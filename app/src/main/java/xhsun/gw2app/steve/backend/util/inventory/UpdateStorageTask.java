package xhsun.gw2app.steve.backend.util.inventory;

import java.util.List;

import timber.log.Timber;
import xhsun.gw2api.guildwars2.err.GuildWars2Exception;
import xhsun.gw2app.steve.backend.database.character.CharacterInfo;
import xhsun.gw2app.steve.backend.database.character.StorageInfo;
import xhsun.gw2app.steve.backend.util.storage.StorageTask;

/**
 * Async task for updating character inventory
 *
 * @author xhsun
 * @since 2017-04-01
 */

class UpdateStorageTask extends StorageTask<CharacterInfo, Void, CharacterInfo> {
	private OnLoadMoreListener provider;
	private boolean isChanged = false;

	UpdateStorageTask(OnLoadMoreListener provider) {
		this.provider = provider;
		provider.getStorageWrapper().setCancelled(false);
	}

	@Override
	protected void onCancelled() {
		Timber.i("Retrieve character info cancelled");
		provider.getStorageWrapper().setCancelled(true);
	}

	@Override
	protected CharacterInfo doInBackground(CharacterInfo... params) {
		CharacterInfo info = params[0];
		if (info == null) return null;
		try {
			List<StorageInfo> items = provider.getStorageWrapper().updateInventoryInfo(info);
			if (!items.equals(info.getInventory())) {
				info.setInventory(items);
				isChanged = true;
			}
			return info;
		} catch (GuildWars2Exception e) {
			return null;
		}
	}

	@Override
	protected void onPostExecute(CharacterInfo result) {
		if (isCancelled) return;
		if (result == null) {
			//TODO show error
			//there is stuff in the inventory and it's different from what is already show
		} else if (result.getInventory().size() > 0 && isChanged)
			result.getAdapter().notifyDataSetChanged();
		provider.getUpdates().remove(this);
	}
}