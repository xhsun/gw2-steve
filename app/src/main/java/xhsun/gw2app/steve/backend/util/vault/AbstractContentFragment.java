package xhsun.gw2app.steve.backend.util.vault;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;

import com.annimon.stream.Stream;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.common.SmoothScrollGridLayoutManager;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import timber.log.Timber;
import xhsun.gw2app.steve.R;
import xhsun.gw2app.steve.backend.util.CancellableAsyncTask;
import xhsun.gw2app.steve.backend.util.Utility;
import xhsun.gw2app.steve.backend.util.items.ProgressItem;

/**
 * template fragment class for various fragments that need to display items in grid
 *
 * @author xhsun
 * @since 2017-05-03
 */

public abstract class AbstractContentFragment<T> extends Fragment implements SearchCallback,
		PreferenceModifyCallback, FlexibleAdapter.EndlessScrollListener, FlexibleAdapter.OnUpdateListener,
		ShouldLoadCheckHelper, UpdateDataCallback {
	private VaultType type;
	private Set<CancellableAsyncTask> updates;

	protected ProgressItem load;
	public FlexibleAdapter<AbstractFlexibleItem> adapter;
	protected List<AbstractFlexibleItem> content;
	protected List<T> items;
	protected volatile ArrayDeque<T> remaining;

	protected int columns = -1, rows = -1;

	public AbstractContentFragment(VaultType type) {
		this.type = type;
		remaining = new ArrayDeque<>();
		updates = new HashSet<>();
		content = new ArrayList<>();
		items = new ArrayList<>();
		load = new ProgressItem();

		//setting up adapter without endless loading
		adapter = new VaultAdapter(content, this, true);

		adapter.expandItemsAtStartUp()
				.setAutoCollapseOnExpand(false)
				.setMinCollapsibleLevel(1)
				.setAutoScrollOnExpand(false)
				.setNotifyMoveOfFilteredItems(false)
				.setNotifyChangeOfUnfilteredItems(true)
				.setAnimationOnScrolling(true)
				.setAnimationOnReverseScrolling(true);
	}

	@Override
	public void onPause() {
		Timber.i("Paused %s fragment", type);
		super.onPause();
		cancelAllTask();
	}

	/**
	 * start endless loading
	 */
	public abstract void startEndless();

	/**
	 * load next data set to display
	 */
	public abstract void loadNextData();

	/**
	 * get preference that is associate with given key
	 *
	 * @param key for find the correct preference
	 * @return {@link Set} of string
	 */
	public abstract Set<String> getPreference(String key);

	/**
	 * show content
	 */
	public abstract void show();

	/**
	 * set refreshing to false for refresh layout
	 */
	public abstract void stopRefresh();

	/**
	 * hide content from display
	 */
	public abstract void hide();

	/**
	 * check if display is showing content
	 *
	 * @return true if it is showing | false otherwise
	 */
	protected abstract boolean isShowing();

	public int getColumns() {
		return columns;
	}

	public ProgressItem getProgressItem() {
		return load;
	}

	public VaultType getType() {
		return type;
	}

	/**
	 * get the set that is used to manage async tasks
	 *
	 * @return {@link CancellableAsyncTask} manager
	 */
	public Set<CancellableAsyncTask> getUpdates() {
		return updates;
	}

	/**
	 * get list of raw data stored
	 *
	 * @return list of {@link T}
	 */
	public List<T> getItems() {
		return items;
	}

	/**
	 * create grid layout manager with span size lookup<br/>
	 * also set columns field
	 *
	 * @param view view
	 * @return {@link GridLayoutManager}
	 */
	protected GridLayoutManager createGridLayoutManager(View view) {
		columns = Utility.calculateColumns(view);
		GridLayoutManager gridLayoutManager = new SmoothScrollGridLayoutManager(getActivity(), columns);
		gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
			@Override
			public int getSpanSize(int position) {
				switch (adapter.getItemViewType(position)) {
					case R.layout.item_header_vault:
					case R.layout.item_subheader_vault:
					case R.layout.item_progress:
						return columns;
					default:
						return 1;
				}
			}
		});
		return gridLayoutManager;
	}

	/**
	 * check if recyclerview is scrollable yet
	 *
	 * @return true if scrollable | false otherwise
	 */
	protected boolean isRecyclerScrollable() {
		int rows;
		List<AbstractFlexibleItem> current = adapter.getCurrentItems();
		if (isShowing() || this.rows < 0) return true;//no need for the cal if its already showing

		int temp = (int) Stream.of(current).filter(h -> h instanceof VaultHeader || h instanceof VaultSubHeader).count();
		rows = temp + (int) Math.ceil((current.size() - temp) / columns) + (((current.size() - temp) % columns > 0) ? 1 : 0);

		return !(rows == 0 || rows < 0) && this.rows <= rows;
	}

	protected T getNextAvailable(String key, String value, int origin) {
		Set<String> pref = null;
		if (!key.equals("")) pref = getPreference(key);
		for (int i = origin + 1; i < items.size(); i++) {
			T a = items.get(i);
			if (pref == null || !pref.contains(value)) return a;
		}
		return null;
	}

	//cancel all running tasks
	protected void cancelAllTask() {
		Stream.of(updates).filter(t -> t.getStatus() != AsyncTask.Status.FINISHED).forEach(r -> {
			r.cancel(true);
			r.setCancelled();
		});
	}
}
