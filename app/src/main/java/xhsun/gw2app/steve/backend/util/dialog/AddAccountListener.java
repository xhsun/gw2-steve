package xhsun.gw2app.steve.backend.util.dialog;

import xhsun.gw2app.steve.backend.data.AccountData;

/**
 * Listener for add account dialog
 *
 * @author xhsun
 * @since 2017-03-27
 */

public interface AddAccountListener {

	/**
	 * callback to retrieve result from add account
	 *
	 * @param account account added
	 */
	void addAccountCallback(AccountData account);
}
