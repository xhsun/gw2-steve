package xhsun.gw2app.steve.backend.util.support.vault.preference;

import java.util.Set;

import xhsun.gw2app.steve.backend.data.model.AccountModel;

/**
 * for modify view depend on the preference change
 *
 * @author xhsun
 * @since 2017-05-03
 */

public interface PreferenceModifyCallback {
	/**
	 * update what is currently displaying base on preference change
	 *
	 * @param preference accounts that have changed preference
	 */
	void processChange(Set<AccountModel> preference);
}
