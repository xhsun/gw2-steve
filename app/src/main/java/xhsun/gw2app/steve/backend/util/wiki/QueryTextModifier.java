package xhsun.gw2app.steve.backend.util.wiki;

import android.support.v7.widget.SearchView;
import android.view.View;

import timber.log.Timber;

/**
 * Take user's query text and submit to gw2 wiki as a special search item
 *
 * @author xhsun
 * @since 2017-02-04
 */

public class QueryTextModifier implements SearchView.OnQueryTextListener {
	private static final String URL = "https://wiki.guildwars2.com/wiki/Special:Search/";
	private ResourceProvider provider;

	public QueryTextModifier(ResourceProvider provider) {
		this.provider = provider;
	}

	/**
	 * Take user's query text and submit to gw2 wiki as a special search item
	 *
	 * @param query user input
	 * @return nothing
	 */
	@Override
	public boolean onQueryTextSubmit(String query) {
		if (provider.getWebView().getVisibility() == View.GONE)
			provider.getProgressBar().setVisibility(View.VISIBLE);
		provider.getSearchView().onActionViewCollapsed();
		provider.getWebView().loadUrl(URL + query);
		Timber.i("Load %s", URL + query);
		return false;
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		return false;
	}
}
