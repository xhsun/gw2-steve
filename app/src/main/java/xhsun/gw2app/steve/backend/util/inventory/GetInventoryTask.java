package xhsun.gw2app.steve.backend.util.inventory;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import timber.log.Timber;
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
	}

	@Override
	protected void onCancelled() {
		Timber.i("Retrieve character info cancelled");
		int index = provider.getAdapter().removeData(null);
		if (index >= 0) provider.getAdapter().notifyItemRemoved(index);
	}

	@Override
	protected CharacterInfo doInBackground(Void... params) {
		List<StorageInfo> info;
		List<CharacterInfo> known = account.getAllCharacters();
		CharacterInfo character = findNextChar();
		if (character == null) return null;
		if (known.contains(character)) {//inventory info is in the database, show it
			info = known.get(known.indexOf(character)).getInventory();
		} else {//not in database, ready to retrieve it
			known.add(character);
			info = new ArrayList<>();
		}

		character.setInventory(info);
		return character;
	}

	@Override
	protected void onPostExecute(CharacterInfo result) {
		int index;
		if (isCancelled() || isCancelled) return;
		if (result == null) {//no more character to load
			//remove progress bar if there is any
			index = provider.getAdapter().removeData(null);
			if (index >= 0) provider.getAdapter().notifyItemRemoved(index);
			//remove account if there is nothing displaying
			AccountInfo showing = provider.getAdapter().getData(account);
			if (showing != null && showing.getCharacters().size() == 0)
				provider.getAdapter().notifyItemRemoved(provider.getAdapter().removeData(account));
		} else {
			if (result.getInventory().size() > 0) {//find some inventory info in the database
				//remove progress bar if there is any
				index = provider.getAdapter().removeData(null);
				if (index >= 0) provider.getAdapter().notifyItemRemoved(index);
				//add and show character
				((CharacterListAdapter) account.getChild().getAdapter()).addData(result);
			}
			//start updating storage information for this character
			UpdateStorageTask task = new UpdateStorageTask(provider, account, result, true);
			provider.getUpdates().add(task);
			task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}
		provider.getUpdates().remove(this);
	}

	//find next character that haven't been searched for this account
	private CharacterInfo findNextChar() {
		if (account.isSearched()) return null;//nothing to find for this account

		Set<String> prefer = provider.getPreferences(account);
		Timber.i("Preference for %s is %s", account.getName(), prefer);

		List<String> names = account.getAllCharacterNames();//get list of searched names
		List<CharacterInfo> characters = account.getCharacters();
		if (characters.size() == names.size() || prefer.size() == 0) {//all character got searched
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
			if (!prefer.contains(name)) continue;
			if (showed.contains(character)) {
				character = showed.get(showed.indexOf(character));
				if (((CharacterListAdapter) account.getChild().getAdapter()).containData(character))
					continue;
				Timber.i("Return loaded character %s", name);
				return character;
			}

			//add this char to list of characters and return it
			Timber.i("Load new character %s", name);
			account.getCharacters().add(character);
			return character;
		}
		account.setSearched(true);//searched all char
		return null;
	}
}
