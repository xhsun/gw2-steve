package xhsun.gw2app.steve.backend.util.support.vault;

import android.support.v7.widget.SearchView;

import timber.log.Timber;

/**
 * Capture user input and send it to {@link QueryTextCallback}
 *
 * @author xhsun
 * @since 2017-04-17
 */

public class QueryTextCallback implements SearchView.OnQueryTextListener {
	private QueryTextListener listener;

	public QueryTextCallback(QueryTextListener listener) {
		this.listener = listener;
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		return false;
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		Timber.i("New query text: %s", newText);
		listener.notifyQueryTest(newText);
		return false;
	}
}
