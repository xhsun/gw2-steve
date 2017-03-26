package xhsun.gw2app.steve.backend.util.account;

import xhsun.gw2app.steve.backend.database.account.AccountInfo;

/**
 * onListItemClick listener for each account list item
 *
 * @author xhsun
 * @since 2017-02-05
 */

public interface ListOnClickListener {
	void onListItemClick(AccountInfo account);
}
