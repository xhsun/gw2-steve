package xhsun.gw2app.steve.backend.util.support.vault.storage;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;

import java.util.List;
import java.util.Set;

import xhsun.gw2app.steve.backend.data.model.AccountModel;
import xhsun.gw2app.steve.backend.util.support.vault.VaultType;

/**
 * helper for storage tab fragments
 *
 * @author xhsun
 * @since 2017-05-17
 */

public interface StorageTabHelper {
	List<AccountModel> getData();

	Set<String> getPreference(VaultType type);

	FloatingActionButton getFAB();

	ViewPager getViewPager();

	SearchView getSearchView();
}
