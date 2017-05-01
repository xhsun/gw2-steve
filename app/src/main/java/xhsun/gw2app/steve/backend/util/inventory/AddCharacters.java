package xhsun.gw2app.steve.backend.util.inventory;

import android.content.Context;

import java.util.List;
import java.util.Set;

import timber.log.Timber;
import xhsun.gw2api.guildwars2.GuildWars2;
import xhsun.gw2api.guildwars2.err.GuildWars2Exception;
import xhsun.gw2app.steve.backend.database.account.AccountDB;
import xhsun.gw2app.steve.backend.database.account.AccountWrapper;
import xhsun.gw2app.steve.backend.database.character.CharacterDB;
import xhsun.gw2app.steve.backend.database.character.CharacterWrapper;
import xhsun.gw2app.steve.backend.util.storage.StorageTask;

/**
 * for adding new character information in the background
 *
 * @author xhsun
 * @since 2017-04-26
 */
class AddCharacters extends StorageTask<Void, Void, Void> {
	private String api;
	private List<String> names;
	private Set<StorageTask> updates;
	private CharacterWrapper characterWrapper;

	AddCharacters(Context context, String api, List<String> names, Set<StorageTask> update) {
		GuildWars2 wrapper = GuildWars2.getInstance();
		AccountWrapper accountWrapper = new AccountWrapper(new AccountDB(context), wrapper);
		characterWrapper = new CharacterWrapper(wrapper, accountWrapper, new CharacterDB(context));
		this.api = api;
		this.names = names;
		updates = update;
		updates.add(this);
	}

	@Override
	protected void onCancelled() {
		Timber.i("Add character info cancelled");
		characterWrapper.setCancelled(true);
	}

	@Override
	protected Void doInBackground(Void... params) {
		if (isCancelled() || isCancelled) return null;
		Timber.i("Try to add all unknown characters to database for account (%s)", api);
		try {
			characterWrapper.update(api, names);
		} catch (GuildWars2Exception ignored) {
		}
		updates.remove(this);
		return null;
	}
}
