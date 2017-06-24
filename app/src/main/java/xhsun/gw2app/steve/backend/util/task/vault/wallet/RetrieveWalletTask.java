package xhsun.gw2app.steve.backend.util.task.vault.wallet;

import java.util.List;

import me.xhsun.guildwars2wrapper.GuildWars2;
import timber.log.Timber;
import xhsun.gw2app.steve.backend.data.model.AccountModel;
import xhsun.gw2app.steve.backend.data.model.CurrencyModel;
import xhsun.gw2app.steve.backend.data.wrapper.account.AccountDB;
import xhsun.gw2app.steve.backend.data.wrapper.account.AccountWrapper;
import xhsun.gw2app.steve.backend.data.wrapper.common.CurrencyDB;
import xhsun.gw2app.steve.backend.data.wrapper.common.CurrencyWrapper;
import xhsun.gw2app.steve.backend.data.wrapper.wallet.WalletDB;
import xhsun.gw2app.steve.backend.data.wrapper.wallet.WalletWrapper;
import xhsun.gw2app.steve.backend.util.support.dialog.AddAccountListener;
import xhsun.gw2app.steve.backend.util.support.vault.wallet.FragmentInfoProvider;
import xhsun.gw2app.steve.backend.util.task.CancellableAsyncTask;
import xhsun.gw2app.steve.view.dialog.DialogManager;

/**
 * Get all wallet information, only update for ones that don't have any info cached
 *
 * @author xhsun
 * @since 2018-05-01
 */

public class RetrieveWalletTask extends CancellableAsyncTask<Void, Void, List<CurrencyModel>> {
	private FragmentInfoProvider provider;
	private AccountWrapper accountWrapper;
	private WalletWrapper walletWrapper;

	public RetrieveWalletTask(FragmentInfoProvider provider) {
		this.provider = provider;
		GuildWars2 wrapper = GuildWars2.getInstance();
		CurrencyWrapper currencyWrapper = new CurrencyWrapper(new CurrencyDB(provider.getContext()));
		accountWrapper = new AccountWrapper(new AccountDB(provider.getContext()), wrapper);
		walletWrapper = new WalletWrapper(new WalletDB(provider.getContext()), currencyWrapper,
				wrapper, accountWrapper);
	}

	@Override
	protected void onPreExecute() {
		provider.hideContent(true);
		provider.getTasks().add(this);
	}

	@Override
	protected List<CurrencyModel> doInBackground(Void... params) {
		List<AccountModel> accounts = accountWrapper.getAll(true);
		if (accounts.size() == 0) return null;
		for (AccountModel a : accounts) {
			if (isCancelled() || isCancelled) return null;
			//TODO uuugh.. this is going to slow things down, need change
			if (!walletWrapper.isWalletCached(a)) {
				walletWrapper.update(a);
			}
		}
		return walletWrapper.getAll();
	}

	@Override
	protected void onPostExecute(List<CurrencyModel> result) {
		if (isCancelled() || isCancelled) return;
		if (result == null) {
			Timber.i("No accounts in record, prompt add account");
			new DialogManager(provider.getFragmentManager()).promptAdd((AddAccountListener) provider);
		} else provider.getAdapter().setData(result);

		provider.getTasks().remove(this);
		provider.showContent(true);
	}

	@Override
	protected void onCancelled() {
		Timber.i("Get wallet info cancelled");
		provider.showContent(true);
		walletWrapper.setCancelled(true);
	}
}
