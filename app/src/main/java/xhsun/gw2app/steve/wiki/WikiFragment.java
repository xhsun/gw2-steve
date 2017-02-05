package xhsun.gw2app.steve.wiki;

import android.annotation.SuppressLint;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ProgressBar;

import xhsun.gw2app.steve.R;


/**
 * WikiFragment is a subclass of {@link Fragment}.
 * - call by main when user click on search wiki button in the side nav
 * - contains a search bar so user can search things on gw2 wiki
 * - web view are used to display gw2 wiki only
 *
 * @author xhsun
 * @since 2017-02-03
 */
public class WikiFragment extends Fragment implements WikiWebHistoryListener {
	private static final double WIDTH = 800;

	private Menu menu;
	private WebView webView;
	private ProgressBar progressBar;

	public WikiFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_wiki, container, false);

		//setup action bar
		Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
		toolbar.setTitle("Wiki");
		setHasOptionsMenu(true);

		progressBar = (ProgressBar) view.findViewById(R.id.wiki_progress);

		//init web view
		webView = (WebView) view.findViewById(R.id.wiki_webview);
		setupWebView();
		return view;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();//to prevent this method keep adding stuff to the action bar
		inflater.inflate(R.menu.fragment_wiki_toolbar, menu);
		super.onCreateOptionsMenu(menu, inflater);

		this.menu = menu;//for enable/disable menu items
		setupSearchView();
	}

	/**
	 * controller for back/forward button
	 *
	 * @param item menu item
	 * @return boolean
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.wiki_back:
				webView.goBack();
				break;
			case R.id.wiki_forward:
				webView.goForward();
				break;
		}

		return super.onOptionsItemSelected(item);
	}


	/**
	 * enable given button
	 *
	 * @param item 0 for back button, 1 for forward button
	 */
	@Override
	public void switchEnable(int item) {
		MenuItem menuItem;
		if (menu == null) return;
		switch (item) {
			case 0://can go back, switch back button on
				menuItem = menu.findItem(R.id.wiki_back);
				menuItem.setIcon(R.drawable.ic_action_back);
				break;
			case 1://can go forward, switch forward button on
				menuItem = menu.findItem(R.id.wiki_forward);
				menuItem.setIcon(R.drawable.ic_action_forward);
				break;
			default:
				return;
		}
		menuItem.setEnabled(true);
	}

	/**
	 * disable given button
	 *
	 * @param item 0 for back button, 1 for forward button
	 */
	@Override
	public void switchDisable(int item) {
		MenuItem menuItem;
		if (menu == null) return;
		switch (item) {
			case 0://can't go back, switch back button off
				menuItem = menu.findItem(R.id.wiki_back);
				menuItem.setIcon(R.drawable.ic_action_back_disable);
				break;
			case 1://can't go forward, switch forward button off
				menuItem = menu.findItem(R.id.wiki_forward);
				menuItem.setIcon(R.drawable.ic_action_forward_disable);
				break;
			default:
				return;
		}
		menuItem.setEnabled(false);
	}

	/**
	 * setup web view with all the necessary settings
	 */
	@SuppressLint("SetJavaScriptEnabled")
	private void setupWebView() {
		if (webView == null) return;
		webView.getSettings().setBuiltInZoomControls(true);
		webView.getSettings().setDisplayZoomControls(false);
		webView.setInitialScale(getScale());
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setWebViewClient(new WikiWebViewClient(this, progressBar));
	}

	/**
	 * setup search with with search hint and listener
	 */
	private void setupSearchView() {
		SearchView searchView = (SearchView) menu.findItem(R.id.wiki_search).getActionView();
		searchView.setQueryHint("Search Wiki");
		searchView.setOnQueryTextListener(new WikiSearchListener(searchView, webView, progressBar));
		searchView.setIconified(false);
		searchView.requestFocus();
	}

	/**
	 * calculate scale for the web view base on display size
	 * this method is base on answer find in <a href="http://stackoverflow.com/a/3916700">stack overflow</a>
	 *
	 * @return scale value
	 */
	private int getScale() {
		Point size = new Point();
		getActivity().getWindowManager().getDefaultDisplay().getSize(size);
		int width = size.x;
		Double val = width / WIDTH;
		val = val * 100d;
		return val.intValue();
	}
}
