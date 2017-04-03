package xhsun.gw2app.steve.backend.util.inventory;

import android.content.SharedPreferences;

import java.util.List;

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

	SharedPreferences getPreferences();

	List<StorageTask> getUpdates();

	AccountListAdapter getAdapter();

	/**
	 * loading more character inventory content for given account
	 *
	 * @param account account info
	 */
	void OnLoadMore(AccountInfo account);
}
