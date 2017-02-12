package xhsun.gw2app.steve.util.listener;

import xhsun.gw2app.steve.database.account.AccountInfo;

/**
 * onClick listener for each account list item
 *
 * @author xhsun
 * @since 2017-02-05
 */

public interface AccountListListener {
	void onClick(AccountInfo account);
}
