package xhsun.gw2app.steve.backend.util.items;

import android.support.v7.widget.SearchView;

import timber.log.Timber;

/**
 * Capture user input and send it to {@link StorageSearchListener} to do filter/restore
 *
 * @author xhsun
 * @since 2017-04-17
 */

public class QueryTextListener implements SearchView.OnQueryTextListener {
	private StorageSearchListener provider;

	public QueryTextListener(StorageSearchListener provider) {
		this.provider = provider;
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		return false;
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		Timber.i("New query text: %s", newText);
		if (newText.trim().equals("")) {
			provider.restore();
			return false;
		}
		provider.filter(newText.trim().toLowerCase());
		return false;
	}
}
