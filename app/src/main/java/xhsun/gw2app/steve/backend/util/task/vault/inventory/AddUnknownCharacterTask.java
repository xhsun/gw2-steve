package xhsun.gw2app.steve.backend.util.task.vault.inventory;

import android.content.Context;

import java.util.List;
import java.util.Set;

import me.xhsun.guildwars2wrapper.GuildWars2;
import me.xhsun.guildwars2wrapper.error.GuildWars2Exception;
import timber.log.Timber;
import xhsun.gw2app.steve.backend.data.wrapper.account.AccountDB;
import xhsun.gw2app.steve.backend.data.wrapper.account.AccountWrapper;
import xhsun.gw2app.steve.backend.data.wrapper.character.CharacterDB;
import xhsun.gw2app.steve.backend.data.wrapper.character.CharacterWrapper;
import xhsun.gw2app.steve.backend.util.task.CancellableAsyncTask;

/**
 * for adding new character information in the background
 *
 * @author xhsun
 * @since 2017-04-26
 */
class AddUnknownCharacterTask extends CancellableAsyncTask<Void, Void, Void> {
	private String api;
	private List<String> names;
	private Set<CancellableAsyncTask> updates;
	private CharacterWrapper characterWrapper;

	AddUnknownCharacterTask(Context context, String api, List<String> names, Set<CancellableAsyncTask> update) {
		GuildWars2 wrapper = GuildWars2.getInstance();
		AccountWrapper accountWrapper = new AccountWrapper(new AccountDB(context), wrapper);
		characterWrapper = new CharacterWrapper(wrapper, accountWrapper, new CharacterDB(context));
		this.api = api;
		this.names = names;
		updates = update;
		if (updates != null) updates.add(this);
	}

	@Override
	protected void onCancelled() {
		Timber.d("Add unknown character info cancelled");
		characterWrapper.setCancelled(true);
	}

	@Override
	protected Void doInBackground(Void... params) {
		if (isCancelled() || isCancelled) return null;
		try {
			characterWrapper.update(api, names);
		} catch (GuildWars2Exception ignored) {
		}
		if (updates != null) updates.remove(this);
		return null;
	}
}
