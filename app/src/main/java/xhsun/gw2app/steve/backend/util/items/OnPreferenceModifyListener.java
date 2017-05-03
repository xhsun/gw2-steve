package xhsun.gw2app.steve.backend.util.items;

import java.util.List;
import java.util.Set;

import xhsun.gw2app.steve.backend.database.account.AccountInfo;
import xhsun.gw2app.steve.backend.util.dialog.AccountHolder;

/**
 * for make changes in preference related to items
 *
 * @author xhsun
 * @since 2017-05-03
 */

public interface OnPreferenceModifyListener {

	/**
	 * get preference on what character should be displaying for the given account
	 * @param name account info
	 * @return set of character that should be displaying
	 */
	Set<String> getPreferences(AccountInfo name);

	/**
	 * modify preference base on user selection
	 *
	 * @param holders list of updated preference
	 */
	void setPreference(List<AccountHolder> holders);

	/**
	 * @return all accounts that have character(s)
	 */
	List<AccountInfo> getAccounts();
}
