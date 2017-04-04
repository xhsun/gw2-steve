package xhsun.gw2app.steve.backend.util.inventory;

import java.util.List;
import java.util.Set;

import xhsun.gw2app.steve.backend.database.account.AccountInfo;
import xhsun.gw2app.steve.backend.database.character.CharacterWrapper;
import xhsun.gw2app.steve.backend.database.character.StorageWrapper;
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

	CharacterWrapper getCharacterWrapper();

	StorageWrapper getStorageWrapper();

	Set<String> getPreferences(AccountInfo name);

	List<StorageTask> getUpdates();

	AccountListAdapter getAdapter();

	/**
	 * modify preference base on user selection
	 *
	 * @param name       account name
	 * @param characters list of character name
	 */
	void setPreference(String name, List<String> characters);

	/**
	 * loading more character inventory content for given account
	 *
	 * @param account account info
	 */
	void OnLoadMore(AccountInfo account);
}
