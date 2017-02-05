package xhsun.gw2app.steve.wiki;

import android.support.v7.widget.SearchView;
import android.view.View;
import android.webkit.WebView;
import android.widget.ProgressBar;

/**
 * wiki search listener
 * - this listener will take user's query text and submit to gw2 wiki as a special search item
 * @author xhsun
 * @version 0.4
 * @since 2017-02-04
 */

public class WikiSearchListener implements SearchView.OnQueryTextListener {
	private WebView webView;
	private SearchView searchView;
	private ProgressBar progressBar;


	public WikiSearchListener(SearchView search, WebView web, ProgressBar progress) {
		webView = web;
		searchView = search;
		progressBar = progress;
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		String url = "https://wiki.guildwars2.com/wiki/Special:Search/";
		if (webView.getVisibility() == View.GONE) progressBar.setVisibility(View.VISIBLE);
		searchView.onActionViewCollapsed();
		webView.loadUrl(url + query);
		return false;
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		return false;
	}
}
