package xhsun.gw2app.steve.backend.util.items;

import android.support.v7.widget.SearchView;

import java.util.HashSet;
import java.util.Set;

import timber.log.Timber;

/**
 * Capture user input and send it to {@link StorageSearchListener} to do filter/restore
 *
 * @author xhsun
 * @since 2017-04-17
 */

public class QueryTextListener implements SearchView.OnQueryTextListener {
	private Set<StorageContentFragment> listeners;

	public QueryTextListener(StorageContentFragment listener) {
		listeners = new HashSet<>();
		listeners.add(listener);
	}

	public QueryTextListener(Set<StorageContentFragment> listeners) {
		this.listeners = listeners;
	}

//	public void detach(StorageContentFragment listener){
//		listeners.remove(listener);
//	}
//
//	public void attach(StorageContentFragment listener){
//		listeners.add(listener);
//	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		return false;
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		Timber.i("New query text: %s", newText);
		if (newText.equals("")) for (StorageSearchListener l : listeners) l.restore();
		else for (StorageSearchListener l : listeners) l.filter(newText.toLowerCase());

		return false;
	}
}
