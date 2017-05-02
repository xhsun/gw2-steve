package xhsun.gw2app.steve.view.fragment;

import android.annotation.SuppressLint;
import android.os.Build;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;
import xhsun.gw2app.steve.R;
import xhsun.gw2app.steve.backend.util.Utility;
import xhsun.gw2app.steve.backend.util.wiki.QueryTextModifier;
import xhsun.gw2app.steve.backend.util.wiki.ResourceProvider;
import xhsun.gw2app.steve.backend.util.wiki.WebClient;

/**
 * WikiFragment is a subclass of {@link Fragment}<br/>
 * &#32;- call by main when user click on search wiki button in the side nav<br/>
 * &#32;- contains a search bar so user can search things on gw2 wiki<br/>
 * &#32;- web view are used to display gw2 wiki only
 *
 * @author xhsun
 * @since 2017-02-03
 */
public class WikiFragment extends Fragment implements ResourceProvider {
	private Menu menu;
	private SearchView search;
	@BindView(R.id.wiki_webview)
	WebView wiki;
	@BindView(R.id.wiki_progress)
	ProgressBar progress;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_wiki, container, false);
		ButterKnife.bind(this, view);

		Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
		toolbar.setTitle("Wiki");
		setHasOptionsMenu(true);

		setupWebView();
		Timber.i("Initialization complete");
		return view;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();//prevent constantly adding stuff to toolbar
		inflater.inflate(R.menu.fragment_wiki_toolbar, menu);
		this.menu = menu;//for forward/backward button manipulation
		setupSearchView();//set up search box
		super.onCreateOptionsMenu(menu, inflater);
		Timber.i("Toolbar setup finished");
	}

	/**
	 * controller for forward/backward button
	 *
	 * @param item menu item
	 * @return boolean
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.wiki_back:
				wiki.goBack();
				Timber.i("Go back one page");
				break;
			case R.id.wiki_forward:
				wiki.goForward();
				Timber.i("Go forward one page");
				break;
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * enable forward/backward button for web page
	 *
	 * @param button 0 for back button, 1 for forward button
	 */
	@Override
	public void enable(int button) {
		MenuItem menuItem;
		if (menu == null) return;
		switch (button) {
			case 0://can go back, switch back button on
				menuItem = menu.findItem(R.id.wiki_back);
				menuItem.setIcon(R.drawable.ic_action_back);
				Timber.i("Enable back button");
				break;
			case 1://can go forward, switch forward button on
				menuItem = menu.findItem(R.id.wiki_forward);
				menuItem.setIcon(R.drawable.ic_action_forward);
				Timber.i("Enable forward button");
				break;
			default:
				Timber.d("Unknown button number %d given", button);
				return;
		}
		menuItem.setEnabled(true);
	}

	/**
	 * disable forward/backward button for web page
	 *
	 * @param button 0 for back button, 1 for forward button
	 */
	@Override
	public void disable(int button) {
		MenuItem menuItem;
		if (menu == null) return;
		switch (button) {
			case 0://can't go back, switch back button off
				menuItem = menu.findItem(R.id.wiki_back);
				menuItem.setIcon(R.drawable.ic_action_back_disable);
				Timber.i("Disable back button");
				break;
			case 1://can't go forward, switch forward button off
				menuItem = menu.findItem(R.id.wiki_forward);
				menuItem.setIcon(R.drawable.ic_action_forward_disable);
				Timber.i("Disable forward button");
				break;
			default:
				Timber.d("Unknown button number %d given", button);
				return;
		}
		menuItem.setEnabled(false);
	}

	@Override
	public ProgressBar getProgressBar() {
		return progress;
	}

	@Override
	public WebView getWebView() {
		return wiki;
	}

	@Override
	public SearchView getSearchView() {
		return search;
	}

	/**
	 * setup web view with all the necessary settings
	 */
	@SuppressLint("SetJavaScriptEnabled")
	private void setupWebView() {
		if (wiki == null) return;
		wiki.getSettings().setBuiltInZoomControls(true);
		wiki.getSettings().setDisplayZoomControls(false);
		wiki.setInitialScale(Utility.getScale(this.getActivity()));
		wiki.getSettings().setJavaScriptEnabled(true);
		wiki.getSettings().setDomStorageEnabled(true);
		wiki.setWebViewClient(new WebClient(this));
		if (Build.VERSION.SDK_INT >= 19) wiki.setLayerType(View.LAYER_TYPE_HARDWARE, null);
		else wiki.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		Timber.i("WebView setup finished");
	}

	/**
	 * setup search with with search hint and listener
	 */
	private void setupSearchView() {
		search = (SearchView) menu.findItem(R.id.wiki_search).getActionView();
		search.setQueryHint("Search Wiki");
		search.setOnQueryTextListener(new QueryTextModifier(this));
		search.setIconified(false);
		search.requestFocus();
		Timber.i("SearchView setup finished");
	}
}
