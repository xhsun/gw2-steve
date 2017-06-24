package xhsun.gw2app.steve.backend.util.wallet;

import java.util.List;

import me.xhsun.guildwars2wrapper.GuildWars2;
import me.xhsun.guildwars2wrapper.error.ErrorCode;
import me.xhsun.guildwars2wrapper.error.GuildWars2Exception;
import timber.log.Timber;
import xhsun.gw2app.steve.backend.data.CurrencyData;
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

public class UpdateWalletInfo extends CancellableAsyncTask<Void, Void, AsyncTaskResult<List<CurrencyData>>> {
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
	protected AsyncTaskResult<List<CurrencyData>> doInBackground(Void... params) {
		AsyncTaskResult<List<CurrencyData>> result;
		Boolean isSuccess = walletWrapper.update();
		if (isSuccess == null) {
			result = new AsyncTaskResult<>();
			result.setData(null);
			return result;
		}

		List<CurrencyData> data = walletWrapper.getAll();
		if (!isSuccess) {
			result = new AsyncTaskResult<>(new GuildWars2Exception(ErrorCode.Server, ""));
			result.setData(data);
			return result;
		} else return new AsyncTaskResult<>(data);
	}

	@Override
	protected void onPostExecute(AsyncTaskResult<List<CurrencyData>> result) {
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
