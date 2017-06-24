package xhsun.gw2app.steve.backend.util.support.wiki;

import android.support.v7.widget.SearchView;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import xhsun.gw2app.steve.view.fragment.WikiFragment;

/**
 * For changing availability of forward/backward button in {@link WikiFragment}
 *
 * @author xhsun
 * @since 2017-02-03
 */

public interface ResourceProvider {

	/**
	 * enable forward/backward button for web page
	 *
	 * @param button 0 for back button, 1 for forward button
	 */
	void enable(int button);

	/**
	 * disable forward/backward button for web page
	 *
	 * @param button 0 for back button, 1 for forward button
	 */
	void disable(int button);

	/**
	 * @return {@link ProgressBar}
	 */
	RelativeLayout getProgressBar();

	/**
	 * @return {@link WebView}
	 */
	WebView getWebView();

	/**
	 * @return {@link SearchView}
	 */
	SearchView getSearchView();
}
