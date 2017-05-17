package xhsun.gw2app.steve.backend.util.storage;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.widget.ProgressBar;

import java.util.List;
import java.util.Set;

import xhsun.gw2app.steve.backend.data.AccountInfo;
import xhsun.gw2app.steve.backend.util.vault.VaultType;

/**
 * helper for storage tab fragments
 *
 * @author xhsun
 * @since 2017-05-17
 */

public interface StorageTabHelper {
	List<AccountInfo> getData();

	Set<String> getPreference(VaultType type);

	FloatingActionButton getFAB();

	ViewPager getViewPager();

	ProgressBar getProgressBar();

	SearchView getSearchView();
}
