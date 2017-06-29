package xhsun.gw2app.steve.backend.util.task.vault.inventory;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import timber.log.Timber;
import xhsun.gw2app.steve.backend.data.model.AccountModel;
import xhsun.gw2app.steve.backend.data.model.CharacterModel;
import xhsun.gw2app.steve.backend.data.model.vault.item.InventoryItemModel;
import xhsun.gw2app.steve.backend.util.support.vault.load.AbstractContentFragment;
import xhsun.gw2app.steve.backend.util.task.CancellableAsyncTask;
import xhsun.gw2app.steve.backend.util.task.vault.UpdateVaultTask;

/**
 * Async task to get character inventory from database
 *
 * @author xhsun
 * @since 2017-04-01
 */

public class RetrieveInventoryTask extends CancellableAsyncTask<Void, Void, CharacterModel> {
	private AbstractContentFragment<AccountModel> fragment;
	private AccountModel account;

	public RetrieveInventoryTask(AbstractContentFragment<AccountModel> fragment, AccountModel account) {
		this.fragment = fragment;
		this.account = account;
		fragment.getUpdates().add(this);
	}

	@Override
	protected CharacterModel doInBackground(Void... params) {
		List<InventoryItemModel> info;
		List<CharacterModel> known = account.getAllCharacters();
		CharacterModel character = findNextChar(known);
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
	protected void onPostExecute(CharacterModel result) {
		if (isCancelled() || isCancelled) return;
		if (result == null) {//no more character to load
			fragment.loadNextData();
		} else {
			//actively load only if needed to
			if (result.getInventory().size() == 0) {
				Timber.d("No inventory info for %s, start loading from server", result.getName());
				//start updating storage information for this character
				new UpdateVaultTask<InventoryItemModel>(fragment, account, result)
						.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			} else {
				Timber.d("Find inventory info for %s", result.getName());
				fragment.updateData(account);
			}
		}

		fragment.getUpdates().remove(this);
	}

	//find next character that haven't been searched for this account
	private CharacterModel findNextChar(List<CharacterModel> characters) {
		if (account.isSearched()) return null;//nothing to find for this account

		Set<String> prefer = fragment.getPreference(account.getAPI());
		List<String> names = account.getAllCharacterNames();//get list of searched names

		return __findNext(characters, names, prefer);
	}

	//actual searching
	private CharacterModel __findNext(List<CharacterModel> characters, List<String> names, Set<String> prefer) {
		for (String name : names) {
			CharacterModel character = new CharacterModel(account.getAPI(), name);
			if (isCancelled) return null;
			if (prefer.contains(name)) continue;
			if (characters.contains(character)) {
				character = characters.get(characters.indexOf(character));
				//skip any that is displaying or have already searched
				if (character.getInventory().size() > 0) continue;
				return character;
			}

			return character;
		}
		account.setSearched(true);//searched all char
		return null;
	}
}
