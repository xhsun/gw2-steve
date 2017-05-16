package xhsun.gw2app.steve.backend.util.inventory;

import android.content.Context;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;
import xhsun.gw2api.guildwars2.GuildWars2;
import xhsun.gw2api.guildwars2.err.GuildWars2Exception;
import xhsun.gw2app.steve.backend.data.AccountInfo;
import xhsun.gw2app.steve.backend.data.CharacterInfo;
import xhsun.gw2app.steve.backend.database.account.AccountDB;
import xhsun.gw2app.steve.backend.database.account.AccountWrapper;
import xhsun.gw2app.steve.backend.database.character.CharacterDB;
import xhsun.gw2app.steve.backend.database.character.CharacterWrapper;
import xhsun.gw2app.steve.backend.database.common.ItemDB;
import xhsun.gw2app.steve.backend.database.common.ItemWrapper;
import xhsun.gw2app.steve.backend.database.common.SkinDB;
import xhsun.gw2app.steve.backend.database.common.SkinWrapper;
import xhsun.gw2app.steve.backend.database.storage.InventoryDB;
import xhsun.gw2app.steve.backend.database.storage.InventoryWrapper;
import xhsun.gw2app.steve.backend.util.AddAccountListener;
import xhsun.gw2app.steve.backend.util.CancellableAsyncTask;
import xhsun.gw2app.steve.backend.util.dialog.DialogManager;
import xhsun.gw2app.steve.backend.util.vault.AbstractContentFragment;

/**
 * For retrieving all account information from database
 *
 * @author xhsun
 * @since 2017-04-27
 */

public class RetrieveAccountsTask extends CancellableAsyncTask<Void, Void, List<AccountInfo>> {
	private AbstractContentFragment<AccountInfo> fragment;
	private AccountWrapper accountWrapper;
	private CharacterWrapper characterWrapper;
	private InventoryWrapper inventoryWrapper;

	public RetrieveAccountsTask(AbstractContentFragment<AccountInfo> fragment) {
		this.fragment = fragment;
		this.fragment.getUpdates().add(this);
		//init wrappers
		Context context = fragment.getContext();
		GuildWars2 wrapper = GuildWars2.getInstance();
		accountWrapper = new AccountWrapper(new AccountDB(context), wrapper);
		characterWrapper = new CharacterWrapper(wrapper, accountWrapper, new CharacterDB(context));
		ItemWrapper itemWrapper = new ItemWrapper(wrapper, new ItemDB(context));
		SkinWrapper skinWrapper = new SkinWrapper(wrapper, new SkinDB(context));
		inventoryWrapper = new InventoryWrapper(wrapper, accountWrapper, characterWrapper, itemWrapper,
				skinWrapper, new InventoryDB(context));
	}

	@Override
	public void onPreExecute() {
		fragment.hide();
	}

	@Override
	protected void onCancelled() {
		Timber.d("Retrieve all account info cancelled");
		accountWrapper.setCancelled(true);
		characterWrapper.setCancelled(true);
		inventoryWrapper.setCancelled(true);
		fragment.show();
	}

	@Override
	protected List<AccountInfo> doInBackground(Void... params) {
		List<AccountInfo> accounts = accountWrapper.getAll(true);
		List<AccountInfo> inventories = inventoryWrapper.getAll();
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
			new DialogManager((fragment.getFragmentManager())).promptAdd((AddAccountListener) fragment);
		} else {//store all account info for account that actually have char and load first account
			for (AccountInfo a : result) {
				if (a.getAllCharacterNames().size() > 0) {
					fragment.getItems().add(a);
					//Try to add any character that is not in database to database
					AddUnknownCharacterTask task = new AddUnknownCharacterTask(fragment.getContext(),
							a.getAPI(), new ArrayList<>(a.getAllCharacterNames()), fragment.getUpdates());
					task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				}
			}
			fragment.startEndless();
		}

		fragment.getUpdates().remove(this);
	}
}
