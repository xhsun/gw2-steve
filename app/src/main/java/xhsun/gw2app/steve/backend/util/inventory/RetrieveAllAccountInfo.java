package xhsun.gw2app.steve.backend.util.inventory;

import android.content.Context;
import android.os.AsyncTask;

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
import xhsun.gw2app.steve.backend.database.character.StorageWrapper;
import xhsun.gw2app.steve.backend.database.common.ItemDB;
import xhsun.gw2app.steve.backend.database.common.ItemWrapper;
import xhsun.gw2app.steve.backend.util.CancellableAsyncTask;
import xhsun.gw2app.steve.backend.util.dialog.DialogManager;
import xhsun.gw2app.steve.view.fragment.InventoryFragment;

/**
 * For retrieving all account information from database
 *
 * @author xhsun
 * @since 2017-04-27
 */

public class RetrieveAllAccountInfo extends CancellableAsyncTask<Void, Void, List<AccountInfo>> {
	private OnLoadMoreListener target;
	private AccountWrapper accountWrapper;
	private CharacterWrapper characterWrapper;
	private StorageWrapper storageWrapper;

	public RetrieveAllAccountInfo(OnLoadMoreListener listener) {
		target = listener;
		target.getUpdates().add(this);
		//init wrappers
		Context context = ((InventoryFragment) listener).getContext();
		GuildWars2 wrapper = GuildWars2.getInstance();
		accountWrapper = new AccountWrapper(new AccountDB(context), wrapper);
		characterWrapper = new CharacterWrapper(wrapper, accountWrapper, new CharacterDB(context));
		ItemWrapper itemWrapper = new ItemWrapper(wrapper, new ItemDB(context));
		storageWrapper = new StorageWrapper(wrapper, accountWrapper, characterWrapper, itemWrapper,
				new StorageDB(context));
	}

	@Override
	public void onPreExecute() {
		target.hideContent();
	}

	@Override
	protected void onCancelled() {
		Timber.i("Retrieve all account info cancelled");
		accountWrapper.setCancelled(true);
		characterWrapper.setCancelled(true);
		storageWrapper.setCancelled(true);
		target.showContent();
	}

	@Override
	protected List<AccountInfo> doInBackground(Void... params) {
		Timber.i("Retrieve all account info");
		List<AccountInfo> accounts = accountWrapper.getAll(true);
		List<AccountInfo> inventories = storageWrapper.getAll(false);
		for (AccountInfo account : accounts) {
			if (isCancelled() || isCancelled) break;
			if (inventories.contains(account))//add all known inventory info for this account
				account.setAllCharacters(inventories.get(inventories.indexOf(account)).getAllCharacters());
			try {//get all character names
				account.setAllCharacterNames(characterWrapper.getAllNames(account.getAPI()));
			} catch (GuildWars2Exception e) {//error, use cached character name
				List<CharacterInfo> characters = characterWrapper.getAll(account.getAPI());
				for (CharacterInfo c : characters) account.getAllCharacterNames().add(c.getName());
			}
		}
		return accounts;
	}

	@Override
	protected void onPostExecute(List<AccountInfo> result) {
		if (isCancelled() || isCancelled) return;
		if (result.size() == 0) {
			new DialogManager((((InventoryFragment) target).getFragmentManager()))
					.promptAdd(((InventoryFragment) target));
		} else {//store all account info for account that actually have char and load first account
			for (AccountInfo a : result) {
				if (a.getAllCharacterNames().size() > 0) {
					target.getAccounts().add(a);
					//Try to add any character that is not in database to database
					AddCharacters task = new AddCharacters(((InventoryFragment) target).getContext(),
							a.getAPI(), new ArrayList<>(a.getAllCharacterNames()), target.getUpdates());
					task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				}
			}
			target.loadFirstAccount();
		}
		target.getUpdates().remove(this);

		target.showContent();
	}
}
