package xhsun.gw2app.steve.backend.util.wallet;

import android.content.Context;
import android.support.v4.app.FragmentManager;

import java.util.Set;

import xhsun.gw2app.steve.backend.util.CancellableAsyncTask;

/**
 * Interface that provides things needed for async task
 *
 * @author xhsun
 * @since 2017-05-01
 */

public interface FragmentInfoProvider {
	void showContent(boolean hideEverything);

	void hideContent(boolean hideEverything);

	CurrencyListAdapter getAdapter();

	Set<CancellableAsyncTask> getTasks();

	Context getContext();

	FragmentManager getFragmentManager();

	void displayError();
}
