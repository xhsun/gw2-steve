package xhsun.gw2app.steve.backend.util.inventory;

import java.util.List;
import java.util.Set;

import xhsun.gw2app.steve.backend.database.account.AccountInfo;
import xhsun.gw2app.steve.backend.util.dialog.AccountHolder;
import xhsun.gw2app.steve.backend.util.storage.StorageTask;

/**
 * Providing on load more and other class for lazy loading
 *
 * @author xhsun
 * @since 2017-04-02
 */

public interface OnLoadMoreListener {

	boolean isLoading();

	void setLoading(boolean loading);

	boolean isMoreDataAvailable();

	Set<String> getPreferences(AccountInfo name);

	Set<StorageTask> getUpdates();

	AccountListAdapter getAdapter();

	List<AccountInfo> getAccounts();

	void displayWithoutLoad(AccountInfo a, Set<String> shouldAdd);

	/**
	 * modify preference base on user selection
	 *
	 * @param holders list of updated preference
	 */
	void setPreference(List<AccountHolder> holders);

	/**
	 * loading more character inventory content for given account
	 *
	 * @param account account info
	 */
	void onLoadMore(AccountInfo account);

	void loadFirstAccount();

	void showContent();

	void hideContent();
}
