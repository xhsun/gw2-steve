package xhsun.gw2app.steve.backend.util.inventory;

import android.content.Context;
import android.os.AsyncTask;

import com.annimon.stream.Stream;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import me.xhsun.guildwars2wrapper.GuildWars2;
import me.xhsun.guildwars2wrapper.error.GuildWars2Exception;
import timber.log.Timber;
import xhsun.gw2app.steve.backend.data.AccountData;
import xhsun.gw2app.steve.backend.data.CharacterData;
import xhsun.gw2app.steve.backend.database.account.AccountDB;
import xhsun.gw2app.steve.backend.database.account.AccountWrapper;
import xhsun.gw2app.steve.backend.database.character.CharacterDB;
import xhsun.gw2app.steve.backend.database.character.CharacterWrapper;
import xhsun.gw2app.steve.backend.util.CancellableAsyncTask;
import xhsun.gw2app.steve.backend.util.dialog.AddAccountListener;
import xhsun.gw2app.steve.backend.util.vault.AbstractContentFragment;
import xhsun.gw2app.steve.backend.util.vault.UpdateVaultTask;
import xhsun.gw2app.steve.view.dialog.DialogManager;

/**
 * {@link CancellableAsyncTask} for refreshing accounts in preparation for refreshing inventory info
 *
 * @author xhsun
 * @since 2017-05-14
 */
public class RefreshAccountsTask extends CancellableAsyncTask<Void, Void, List<AccountData>> {
	private AbstractContentFragment<AccountData> fragment;
	private CharacterWrapper characterWrapper;

	public RefreshAccountsTask(AbstractContentFragment<AccountData> fragment) {
		this.fragment = fragment;
		this.fragment.getUpdates().add(this);
		//init wrappers
		Context context = fragment.getContext();
		GuildWars2 wrapper = GuildWars2.getInstance();
		AccountWrapper accountWrapper = new AccountWrapper(new AccountDB(context), wrapper);
		characterWrapper = new CharacterWrapper(wrapper, accountWrapper, new CharacterDB(context));
	}

	@Override
	protected void onCancelled() {
		Timber.d("Refresh all account info cancelled");
		characterWrapper.setCancelled(true);
		fragment.stopRefresh();
	}

	@Override
	protected List<AccountData> doInBackground(Void... params) {
		for (AccountData account : fragment.getItems()) {
			if (isCancelled() || isCancelled) break;
			try {//get all character names
				account.setAllCharacterNames(characterWrapper.getAllNames(account.getAPI()));
			} catch (GuildWars2Exception e) {//error, use cached character name
				Stream.of(characterWrapper.getAll(account.getAPI()))
						.forEach(c -> account.getAllCharacterNames().add(c.getName()));
			}
		}
		return fragment.getItems();
	}

	@Override
	protected void onPostExecute(List<AccountData> accounts) {
		if (isCancelled() || isCancelled) return;
		if (accounts.size() == 0) {
			new DialogManager((fragment.getFragmentManager())).promptAdd((AddAccountListener) fragment);
		} else {//store all account info for account that actually have char and load first account
			for (AccountData a : accounts) {
				List<String> names = a.getAllCharacterNames();
				Set<String> prefer = fragment.getPreference(a.getAPI());
				if (names.size() > 0 && (names.size() - prefer.size()) > 0) {
					List<CharacterData> chars = a.getAllCharacters();
					for (String n : names) {
						if (prefer.contains(n)) continue;
						CharacterData temp = new CharacterData(a.getAPI(), n);
						if (!chars.contains(temp)) a.getAllCharacters().add(temp);
						new UpdateVaultTask(fragment, a, chars.get(chars.indexOf(temp)), true)
								.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					}
				}
				//Try to add any character that is not in database to database
				AddUnknownCharacterTask task = new AddUnknownCharacterTask(fragment.getContext(),
						a.getAPI(), new ArrayList<>(a.getAllCharacterNames()), fragment.getUpdates());
				task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			}
		}

		fragment.getUpdates().remove(this);
	}
}
