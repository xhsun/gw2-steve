package xhsun.gw2app.steve.backend.util.storage;

import java.util.Set;

import xhsun.gw2app.steve.backend.database.account.AccountInfo;

/**
 * for modify view depend on the preference change
 *
 * @author xhsun
 * @since 2017-05-03
 */

public interface OnPreferenceModifyListener {
	/**
	 * update what is currently displaying base on preference change
	 *
	 * @param preference accounts that have changed preference
	 */
	void processPreferenceChange(Set<AccountInfo> preference);
}
