package xhsun.gw2app.steve.backend.util.inventory;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import timber.log.Timber;
import xhsun.gw2app.steve.backend.database.account.AccountInfo;
import xhsun.gw2app.steve.backend.database.character.CharacterInfo;
import xhsun.gw2app.steve.backend.database.storage.StorageInfo;
import xhsun.gw2app.steve.backend.util.CancellableAsyncTask;
import xhsun.gw2app.steve.backend.util.Utility;

/**
 * Async task to get character inventory from database
 *
 * @author xhsun
 * @since 2017-04-01
 */

public class GetInventoryTask extends CancellableAsyncTask<Void, Void, CharacterInfo> {
	private OnLoadMoreListener provider;
	private AccountInfo account;

	public GetInventoryTask(OnLoadMoreListener provider, AccountInfo account) {
		this.provider = provider;
		this.account = account;
	}

	@Override
	protected void onCancelled() {
		Timber.i("Retrieve character info cancelled");
		((CharacterListAdapter) account.getChild().getAdapter()).removeData(null);
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
			((CharacterListAdapter) account.getChild().getAdapter()).removeData(null);
			//remove account if there is nothing displaying
			AccountInfo showing = provider.getAdapter().getData(account);
			if (showing != null && showing.getCharacters().size() == 0)
				provider.getAdapter().removeData(account);
		} else {
			CharacterInfo query = new CharacterInfo(result);
			if (!provider.getQuery().equals(""))
				query.setInventory(Utility.filterStorage(provider.getQuery(), result.getInventory()));
			if (query.getInventory().size() > 0) {//find some inventory info in the database
				//remove progress bar if there is any
				((CharacterListAdapter) account.getChild().getAdapter()).removeData(null);
				//add and show character
				((CharacterListAdapter) account.getChild().getAdapter()).addData(query);
			}
			//this helps covers the case where search result block loading next inventory
			if (query.getInventory().size() == 0 && result.getInventory().size() != 0)
				provider.onLoadMore(account);
			//actively load only if needed to
			if (result.getInventory().size() == 0 || provider.isRefresh()) {
				//start updating storage information for this character
				UpdateStorageTask task = new UpdateStorageTask(provider, account, result, true);
				provider.getUpdates().add(task);
				task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			}
		}
		provider.getUpdates().remove(this);
	}

	//find next character that haven't been searched for this account
	private CharacterInfo findNextChar() {
		if (account.isSearched()) return null;//nothing to find for this account

		Set<String> prefer = provider.getPreferences(account);

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
				//skip any that is displaying or have already searched
				if (((CharacterListAdapter) account.getChild().getAdapter()).containData(character)
						|| character.getInventory().size() > 0)
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
