package xhsun.gw2app.steve.backend.util.inventory;

import android.content.SharedPreferences;

import java.util.List;

import xhsun.gw2app.steve.backend.database.account.AccountInfo;
import xhsun.gw2app.steve.backend.database.character.CharacterWrapper;
import xhsun.gw2app.steve.backend.database.character.StorageWrapper;
import xhsun.gw2app.steve.backend.util.storage.StorageTask;

/**
 * Created by hannah on 01/04/17.
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
