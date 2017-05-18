package xhsun.gw2app.steve.backend.util.items;

import android.support.v7.widget.SearchView;

import com.annimon.stream.Stream;

import java.util.HashSet;
import java.util.Set;

import timber.log.Timber;
import xhsun.gw2app.steve.backend.util.vault.AbstractContentFragment;
import xhsun.gw2app.steve.backend.util.vault.SearchCallback;

/**
 * Capture user input and send it to {@link SearchCallback} to do filter/restore
 *
 * @author xhsun
 * @since 2017-04-17
 */

public class QueryTextListener implements SearchView.OnQueryTextListener {
	private Set<AbstractContentFragment> listeners;

	public QueryTextListener(AbstractContentFragment listener) {
		listeners = new HashSet<>();
		listeners.add(listener);
	}

	public QueryTextListener(Set<AbstractContentFragment> listeners) {
		this.listeners = listeners;
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		return false;
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		Timber.i("New query text: %s", newText);
		Stream.of(listeners).forEach(l -> l.filter(newText.toLowerCase()));
		return false;
	}
}
