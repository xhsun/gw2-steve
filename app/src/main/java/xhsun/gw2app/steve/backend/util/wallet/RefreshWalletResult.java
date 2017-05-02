package xhsun.gw2app.steve.backend.util.wallet;

import java.util.List;

import xhsun.gw2app.steve.backend.database.wallet.CurrencyInfo;
import xhsun.gw2app.steve.backend.util.AsyncTaskResult;

/**
 * For async task in wallet fragment
 *
 * @author xhsun
 * @since 2017-03-27
 */

public class RefreshWalletResult extends AsyncTaskResult<List<CurrencyInfo>> {

	public RefreshWalletResult(Exception e) {
		super(e);
	}

	public RefreshWalletResult(List<CurrencyInfo> data) {
		super(data);
	}
}
