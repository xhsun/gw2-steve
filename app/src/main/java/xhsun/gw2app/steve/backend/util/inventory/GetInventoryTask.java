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

public class GetInventoryTask extends StorageTask<Void, Void, CharacterInfo> {
	private OnLoadMoreListener provider;
	private AccountInfo account;

	public GetInventoryTask(OnLoadMoreListener provider, AccountInfo account) {
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
		CharacterInfo character = findNextChar();
		if (character == null) return null;
		List<StorageInfo> info = provider.getStorageWrapper().getAll(character.getName(), false);
		character.setInventory(info);
		return character;
	}

	@Override
	protected void onPostExecute(CharacterInfo result) {
		if (isCancelled() || isCancelled) return;
		//remove progress bar

		if (result == null) {
			provider.getAdapter().notifyItemRemoved(provider.getAdapter().removeData(null));
			//TODO show error
		} else {
			if (result.getInventory().size() > 0) {//nothing in the database
				provider.getAdapter().notifyItemRemoved(provider.getAdapter().removeData(null));
				//add and show character
				account.getAdapter().addCharacter(result);
			}
			//start updating storage information for this character
			UpdateStorageTask task = new UpdateStorageTask(provider, account, result);
			provider.getUpdates().add(task);
			task.execute();
		}
		provider.getUpdates().remove(this);
	}

	//find next character that haven't been searched for this account
	private CharacterInfo findNextChar() {
		if (account.isSearched()) return null;//nothing to find for this account

		Set<String> prefer = provider.getPreferences().getStringSet(account.getName(), null);
		Timber.i("Preference for %s is %s", account.getName(), prefer);

		List<String> names = account.getCharacterNames();//get list of searched names
		List<CharacterInfo> characters = account.getCharacters();
		if (characters.size() == names.size()) {//all character got searched
			account.setSearched(true);//set searched to true and return nothing
			return null;
		}
		return __findNext(characters, names, prefer);
	}

	//actual searching
	private CharacterInfo __findNext(List<CharacterInfo> showed, List<String> names, Set<String> prefer) {
		for (String name : names) {
			CharacterInfo character = new CharacterInfo(account.getAPI(), name);
			if (isCancelled) return null;
			if (showed.contains(character) ||
					(prefer != null && prefer.size() > 0 && !prefer.contains(name))) continue;

			//update character info in background
			UpdateCharacter task = new UpdateCharacter();
			provider.getUpdates().add(task);
			task.execute(character);

			//add this char to list of characters and return it
			Timber.i("Name for this session is %s", name);
			account.getCharacters().add(character);
			return character;
		}
		account.setSearched(true);//searched all char
		return null;
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
