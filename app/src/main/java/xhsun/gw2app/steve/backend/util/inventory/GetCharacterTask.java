package xhsun.gw2app.steve.backend.util.inventory;

import java.util.List;
import java.util.Set;

import timber.log.Timber;
import xhsun.gw2api.guildwars2.err.GuildWars2Exception;
import xhsun.gw2app.steve.backend.database.account.AccountInfo;
import xhsun.gw2app.steve.backend.database.character.CharacterInfo;
import xhsun.gw2app.steve.backend.database.character.StorageInfo;
import xhsun.gw2app.steve.backend.util.storage.StorageTask;

/**
 * Async task to get character inventory from database
 *
 * @author xhsun
 * @since 2017-04-01
 */

public class GetCharacterTask extends StorageTask<Void, Void, CharacterInfo> {
	private WrapperProvider provider;
	private AccountInfo account;

	public GetCharacterTask(WrapperProvider provider, AccountInfo account) {
		this.provider = provider;
		this.account = account;
		provider.getCharacterWrapper().setCancelled(false);
		provider.getStorageWrapper().setCancelled(false);
	}

	@Override
	protected void onCancelled() {
		Timber.i("Retrieve character info cancelled");
		provider.getCharacterWrapper().setCancelled(true);
		provider.getStorageWrapper().setCancelled(true);
	}

	@Override
	protected CharacterInfo doInBackground(Void... params) {
		CharacterInfo character = getCurrentName();
		if (character == null) return null;
		List<StorageInfo> info = provider.getStorageWrapper().getAll(character.getName(), false);
		character.setInventory(info);
		return character;
	}

	@Override
	protected void onPostExecute(CharacterInfo result) {
		if (isCancelled() || isCancelled) return;
		if (result == null) {
			//TODO show error
		} else {
//			account.getCharacters().remove(position-1);
//			account.getAdapter().notifyItemRemoved(position);//remove progress bar
			int index = account.getCharacters().indexOf(result);
			if (index == -1) {
				account.getCharacters().add(result);
				account.getAdapter().notifyItemInserted(account.getCharacters().size());
			} else {
				CharacterInfo info = account.getCharacters().get(index);
				info.setInventory(result.getInventory());
				info.getAdapter().notifyDataSetChanged();
			}
			//start updating storage information for this character
			UpdateStorageTask task = new UpdateStorageTask(provider);
			provider.getUpdates().add(task);
			task.execute(result);
		}
		provider.getUpdates().remove(this);
	}

	private CharacterInfo getCurrentName() {
		String name = findName(account, account.getCharacterNames());
		Timber.i("Name for this session is %s", name);
		if (!name.equals("")) {//find a match
			CharacterInfo info = new CharacterInfo(account.getAPI(), name);

			//update character in background for next session
			UpdateCharacter task = new UpdateCharacter();
			provider.getUpdates().add(task);
			task.execute(info);
			return info;
		}
		return null;
	}

	private String findName(AccountInfo account, List<String> names) {
		Set<String> prefer = provider.getPreferences().getStringSet(account.getName(), null);
		Timber.i("Preference for %s is %s", account.getName(), prefer);
		for (String c : names) {
			if (isCancelled) return "";
			if (account.getCharacters().contains(new CharacterInfo(c))) continue;
			if (!(prefer != null && prefer.size() > 0 && !prefer.contains(c))) return c;
		}
		account.setSearched(true);
		return "";
	}

	//for updating character information in the background
	private class UpdateCharacter extends StorageTask<CharacterInfo, Void, Void> {

		@Override
		protected Void doInBackground(CharacterInfo... params) {
			if (params[0] == null) return null;
			try {
				provider.getCharacterWrapper().update(params[0].getApi(), params[0].getName());
			} catch (GuildWars2Exception ignored) {
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			provider.getUpdates().remove(this);
		}
	}
}
