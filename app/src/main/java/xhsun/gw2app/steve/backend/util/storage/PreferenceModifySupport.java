package xhsun.gw2app.steve.backend.util.storage;

import java.util.List;
import java.util.Set;

import xhsun.gw2app.steve.backend.database.account.AccountInfo;
import xhsun.gw2app.steve.backend.util.dialog.selectAccount.AccountHolder;
import xhsun.gw2app.steve.backend.util.items.StorageType;

/**
 * for modifying preference
 *
 * @author xhsun
 * @since 2017-05-03
 */

public interface PreferenceModifySupport {
	/**
	 * get preference on what character should be displaying for the given account
	 *
	 * @param type type of the storage
	 * @return set of character that should be displaying
	 */
	Set<String> getPreferences(StorageType type);

	/**
	 * modify preference base on user selection
	 *
	 * @param type     type of the storage
	 * @param accounts list that contains select result from user
	 */
	void setPreference(StorageType type, Set<AccountHolder> accounts);

	/**
	 * @return all accounts that have character(s)
	 */
	List<AccountInfo> getAccounts();
}
