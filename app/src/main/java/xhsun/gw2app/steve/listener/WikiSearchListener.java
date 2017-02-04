package xhsun.gw2app.steve.listener;

import android.support.v7.widget.SearchView;

/**
 * Created by hannah on 04/02/17.
 */

public class WikiSearchListener implements SearchView.OnQueryTextListener {
	private SearchView searchView;

	public WikiSearchListener(SearchView search) {
		searchView = search;
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		// Toast like print
		System.out.println("SearchOnQueryTextSubmit: " + query);
//		searchView.setIconified(true);
//		searchView.clearFocus();
		searchView.onActionViewCollapsed();
		return false;
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		System.out.println("SearchOnQueryTextChange: " + newText);
		return false;
	}
}
