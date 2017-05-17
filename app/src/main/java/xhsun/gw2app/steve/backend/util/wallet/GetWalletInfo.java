package xhsun.gw2app.steve.backend.util.wallet;

import java.util.List;

import timber.log.Timber;
import xhsun.gw2api.guildwars2.GuildWars2;
import xhsun.gw2app.steve.backend.data.AccountInfo;
import xhsun.gw2app.steve.backend.data.CurrencyInfo;
import xhsun.gw2app.steve.backend.database.account.AccountDB;
import xhsun.gw2app.steve.backend.database.account.AccountWrapper;
import xhsun.gw2app.steve.backend.database.common.CurrencyDB;
import xhsun.gw2app.steve.backend.database.common.CurrencyWrapper;
import xhsun.gw2app.steve.backend.database.wallet.WalletDB;
import xhsun.gw2app.steve.backend.database.wallet.WalletWrapper;
import xhsun.gw2app.steve.backend.util.AddAccountListener;
import xhsun.gw2app.steve.backend.util.CancellableAsyncTask;
import xhsun.gw2app.steve.view.dialog.DialogManager;

/**
 * Get all wallet information, only update for ones that don't have any info cached
 *
 * @author xhsun
 * @since 2018-05-01
 */

public class GetWalletInfo extends CancellableAsyncTask<Void, Void, List<CurrencyInfo>> {
	private FragmentInfoProvider provider;
	private AccountWrapper accountWrapper;
	private WalletWrapper walletWrapper;

	public GetWalletInfo(FragmentInfoProvider provider) {
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
	protected List<CurrencyInfo> doInBackground(Void... params) {
		List<AccountInfo> accounts = accountWrapper.getAll(true);
		if (accounts.size() == 0) return null;
		for (AccountInfo a : accounts) {
			if (isCancelled() || isCancelled) return null;
			if (!walletWrapper.isWalletCached(a)) {
				walletWrapper.update(a);
			}
		}
		return walletWrapper.getAll();
	}

	@Override
	protected void onPostExecute(List<CurrencyInfo> result) {
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
