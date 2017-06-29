package xhsun.gw2app.steve.backend.util.support.account;

import xhsun.gw2app.steve.backend.data.model.AccountModel;

/**
 * onListItemClick listener for each account list item
 *
 * @author xhsun
 * @since 2017-02-05
 */

public interface ListOnClickListener {
	void onListItemClick(AccountModel account);
}
