package xhsun.gw2app.steve.backend.util.wallet;

import java.util.List;

import timber.log.Timber;
import xhsun.gw2api.guildwars2.GuildWars2;
import xhsun.gw2api.guildwars2.err.ErrorCode;
import xhsun.gw2api.guildwars2.err.GuildWars2Exception;
import xhsun.gw2app.steve.backend.data.CurrencyInfo;
import xhsun.gw2app.steve.backend.database.account.AccountDB;
import xhsun.gw2app.steve.backend.database.account.AccountWrapper;
import xhsun.gw2app.steve.backend.database.common.CurrencyDB;
import xhsun.gw2app.steve.backend.database.common.CurrencyWrapper;
import xhsun.gw2app.steve.backend.database.wallet.WalletDB;
import xhsun.gw2app.steve.backend.database.wallet.WalletWrapper;
import xhsun.gw2app.steve.backend.util.AsyncTaskResult;
import xhsun.gw2app.steve.backend.util.CancellableAsyncTask;
import xhsun.gw2app.steve.backend.util.dialog.AddAccountListener;
import xhsun.gw2app.steve.view.dialog.DialogManager;

/**
 * Update wallet infomation for all accounts
 *
 * @author xhsun
 * @since 2017-05-01
 */

public class UpdateWalletInfo extends CancellableAsyncTask<Void, Void, AsyncTaskResult<List<CurrencyInfo>>> {
	private FragmentInfoProvider provider;
	private WalletWrapper walletWrapper;

	public UpdateWalletInfo(FragmentInfoProvider provider) {
		this.provider = provider;
		GuildWars2 wrapper = GuildWars2.getInstance();
		AccountWrapper accountWrapper = new AccountWrapper(
				new AccountDB(provider.getContext()), wrapper);
		CurrencyWrapper currencyWrapper = new CurrencyWrapper(new CurrencyDB(provider.getContext()));
		walletWrapper = new WalletWrapper(new WalletDB(provider.getContext()), currencyWrapper, wrapper, accountWrapper);
	}

	@Override
	protected void onPreExecute() {
		provider.hideContent(false);
		provider.getTasks().add(this);
	}

	@Override
	protected void onCancelled() {
		Timber.i("Update wallet info cancelled");
		provider.showContent(false);
		walletWrapper.setCancelled(true);
	}

	@Override
	protected AsyncTaskResult<List<CurrencyInfo>> doInBackground(Void... params) {
		AsyncTaskResult<List<CurrencyInfo>> result;
		Boolean isSuccess = walletWrapper.update();
		if (isSuccess == null) {
			result = new AsyncTaskResult<>();
			result.setData(null);
			return result;
		}

		List<CurrencyInfo> data = walletWrapper.getAll();
		if (!isSuccess) {
			result = new AsyncTaskResult<>(new GuildWars2Exception(ErrorCode.Server, ""));
			result.setData(data);
			return result;
		} else return new AsyncTaskResult<>(data);
	}

	@Override
	protected void onPostExecute(AsyncTaskResult<List<CurrencyInfo>> result) {
		if (isCancelled() || isCancelled) return;//task cancelled, abort
		if (result.getData() == null) {
			Timber.i("No accounts in record, prompt add account");
			new DialogManager(provider.getFragmentManager()).promptAdd((AddAccountListener) provider);
		} else provider.getAdapter().setData(result.getData());

		if (result.getError() != null) provider.displayError();

		provider.getTasks().remove(this);
		provider.showContent(false);
	}


}
