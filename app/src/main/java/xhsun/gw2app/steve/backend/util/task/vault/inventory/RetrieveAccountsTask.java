package xhsun.gw2app.steve.backend.util.task.vault.inventory;

import android.content.Context;
import android.os.AsyncTask;

import com.annimon.stream.Stream;

import java.util.ArrayList;
import java.util.List;

import me.xhsun.guildwars2wrapper.GuildWars2;
import me.xhsun.guildwars2wrapper.error.GuildWars2Exception;
import timber.log.Timber;
import xhsun.gw2app.steve.backend.data.model.AccountModel;
import xhsun.gw2app.steve.backend.data.wrapper.account.AccountDB;
import xhsun.gw2app.steve.backend.data.wrapper.account.AccountWrapper;
import xhsun.gw2app.steve.backend.data.wrapper.character.CharacterDB;
import xhsun.gw2app.steve.backend.data.wrapper.character.CharacterWrapper;
import xhsun.gw2app.steve.backend.data.wrapper.common.ItemDB;
import xhsun.gw2app.steve.backend.data.wrapper.common.ItemWrapper;
import xhsun.gw2app.steve.backend.data.wrapper.common.SkinDB;
import xhsun.gw2app.steve.backend.data.wrapper.common.SkinWrapper;
import xhsun.gw2app.steve.backend.data.wrapper.storage.InventoryDB;
import xhsun.gw2app.steve.backend.data.wrapper.storage.InventoryWrapper;
import xhsun.gw2app.steve.backend.util.support.dialog.AddAccountListener;
import xhsun.gw2app.steve.backend.util.support.vault.load.AbstractContentFragment;
import xhsun.gw2app.steve.backend.util.task.CancellableAsyncTask;
import xhsun.gw2app.steve.view.dialog.DialogManager;

/**
 * For retrieving all account information from database
 *
 * @author xhsun
 * @since 2017-04-27
 */

public class RetrieveAccountsTask extends CancellableAsyncTask<Void, Void, List<AccountModel>> {
	private AbstractContentFragment<AccountModel> fragment;
	private AccountWrapper accountWrapper;
	private CharacterWrapper characterWrapper;
	private InventoryWrapper inventoryWrapper;

	public RetrieveAccountsTask(AbstractContentFragment<AccountModel> fragment) {
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
	protected List<AccountModel> doInBackground(Void... params) {
		List<AccountModel> accounts = accountWrapper.getAll(true);
		List<AccountModel> inventories = inventoryWrapper.getAll();
		for (AccountModel account : accounts) {
			if (isCancelled() || isCancelled) break;
			if (inventories.contains(account))//add all known inventory info for this account
				account.setAllCharacters(inventories.get(inventories.indexOf(account)).getAllCharacters());
			try {//get all character names
				account.setAllCharacterNames(characterWrapper.getAllNames(account.getAPI()));
			} catch (GuildWars2Exception e) {//error, use cached character name
				Stream.of(characterWrapper.getAll(account.getAPI()))
						.forEach(c -> account.getAllCharacterNames().add(c.getName()));
			}
		}
		return accounts;
	}

	@Override
	protected void onPostExecute(List<AccountModel> result) {
		if (isCancelled() || isCancelled) return;
		if (result.size() == 0) {
			new DialogManager((fragment.getFragmentManager())).promptAdd((AddAccountListener) fragment);
		} else {//store all account info for account that actually have char and load first account
			Stream.of(result).filter(r -> r.getAllCharacterNames().size() > 0)
					.forEach(a -> {
						fragment.getItems().add(a);
						//Try to add any character that is not in database to database
						AddUnknownCharacterTask task = new AddUnknownCharacterTask(fragment.getContext(),
								a.getAPI(), new ArrayList<>(a.getAllCharacterNames()), fragment.getUpdates());
						task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					});
			fragment.startEndless();
		}

		fragment.getUpdates().remove(this);
	}
}
