package xhsun.gw2app.steve.backend.util.items;

import android.support.v7.widget.RecyclerView;

import java.util.List;
import java.util.Set;

import xhsun.gw2app.steve.backend.database.account.AccountInfo;
import xhsun.gw2app.steve.backend.util.CancellableAsyncTask;

/**
 * Providing on load more and other class for lazy loading
 *
 * @author xhsun
 * @since 2017-04-02
 */

public interface OnLoadMoreListener<A, P> {

	/**
	 * tell the loader to start lazy loading process, by loading the first account
	 */
	void loadFirstAccount();

	/**
	 * loading more character inventory content for given account
	 *
	 * @param account account info
	 */
	void onLoadMore(AccountInfo account);

	/**
	 * display given characters without triggering loading process
	 * @param a account info
	 * @param shouldAdd list of character to display
	 */
	void displayWithoutLoad(AccountInfo a, Set<String> shouldAdd);

	/**
	 * @return true if loading | false if not loading
	 */
	boolean isLoading();

	/**
	 * change state of loading
	 * true - is loading something
	 * false - not loading anything
	 * @param loading new state
	 */
	void setLoading(boolean loading);

	/**
	 * @return true if there is still more to load | false otherwise
	 */
	boolean isMoreDataAvailable();

	/**
	 * @return true if program need to actively refresh content | false otherwise
	 */
	boolean isRefresh();

	/**
	 * get preference on what character should be displaying for the given account
	 * @param value used to get preference
	 * @return set of character that should be displaying
	 */
	Set<String> getPreferences(P value);

	/**
	 * for filter account that haven't loaded yet
	 * @return search query
	 */
	String getQuery();

	/**
	 * for cancel async task when needed
	 * @return set that keep track which task is still going
	 */
	Set<CancellableAsyncTask> getUpdates();

	/**
	 * @return account list adapter
	 */
	A getAdapter();

	/**
	 * @return all accounts that have character(s)
	 */
	List<AccountInfo> getAccounts();

	/**
	 * @return the outer most recycler view
	 */
	RecyclerView provideParentView();

	/**
	 * display everything
	 */
	void showContent();

	/**
	 * hide everything and reset list of accounts
	 */
	void hideContent();
}
