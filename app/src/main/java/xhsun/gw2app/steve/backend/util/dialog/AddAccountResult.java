package xhsun.gw2app.steve.backend.util.dialog;

import xhsun.gw2app.steve.backend.database.account.AccountInfo;
import xhsun.gw2app.steve.backend.util.AsyncTaskResult;

/**
 * For async task in add account dialog
 *
 * @author xhsun
 * @since 2017-03-20
 */

public class AddAccountResult extends AsyncTaskResult<AccountInfo> {

	public AddAccountResult(Exception error) {
		super(error);
	}

	public AddAccountResult(AccountInfo result) {
		super(result);
	}
}
