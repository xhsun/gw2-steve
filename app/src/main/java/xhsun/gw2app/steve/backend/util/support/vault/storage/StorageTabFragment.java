package xhsun.gw2app.steve.backend.util.support.vault.storage;

import android.support.annotation.CallSuper;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.View;

import com.annimon.stream.Stream;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import timber.log.Timber;
import xhsun.gw2app.steve.R;
import xhsun.gw2app.steve.backend.data.model.AccountModel;
import xhsun.gw2app.steve.backend.util.items.vault.VaultHeader;
import xhsun.gw2app.steve.backend.util.support.vault.VaultType;
import xhsun.gw2app.steve.backend.util.support.vault.load.AbstractContentFragment;

/**
 * template fragment for storage tab content
 *
 * @author xhsun
 * @since 2017-05-04
 */

public abstract class StorageTabFragment extends AbstractContentFragment<AccountModel> {
	private static final ReentrantLock lock = new ReentrantLock();

	private StorageTabHelper helper;
	protected List<AbstractFlexibleItem> refreshedContent;
	protected SwipeRefreshLayout refreshLayout;
	protected RecyclerView recyclerView;

	public StorageTabFragment(VaultType type) {
		super(type);
		refreshedContent = null;
	}

	public void setHelper(StorageTabHelper helper) {
		this.helper = helper;
	}

	/**
	 * reload data from helper into items
	 */
	protected boolean reloadData() {
		if (helper.getData() == null || helper.getData().size() == 0) return false;
		items = helper.getData();
		return true;
	}

	/**
	 * data set is updated, should reload display
	 */
	@CallSuper
	public void onDataUpdate() {
		if (reloadData() && recyclerView != null)
			startEndless();
	}

	@Override
	public void startEndless() {
		recyclerView.post(() -> {
			try {//calculate rough estimate of row size
				rows = (int) Math.floor(recyclerView.getHeight() / (recyclerView.getWidth() / columns));
			} catch (ArithmeticException ignored) {
			}
		});

		//init endless
		remaining = new ArrayDeque<>();
		Set<String> pref = getPreference();
		Stream.of(items).filterNot(a -> pref.contains(a.getAPI())).forEach(r -> remaining.add(r));

		adapter.setEndlessTargetCount(columns * 3)
				.setEndlessScrollListener(this, load);
		adapter.setLoadingMoreAtStartUp(true);
	}

	@Override
	public void onLoadMore(int lastPosition, int currentPage) {
		if (adapter.hasSearchText() || refreshedContent != null) {
			adapter.onLoadMoreComplete(null);
			return;
		}
		loadNextData();
	}

	@Override
	public void noMoreLoad(int newItemsSize) {
		Timber.i("No more to load for %s", getType());
		//check if should show content
		if (!isShowing() && (isRecyclerScrollable() || !shouldLoad())) show();
		//don't reset if there is search text
		if (adapter.hasSearchText()) return;
		//reset
		if (adapter.contains(load)) adapter.removeScrollableFooter(load);
	}

	@Override
	public void loadNextData() {
		VaultHeader header = generateContent();
		if (header == null) adapter.onLoadMoreComplete(null, 200);
		else if (header.getSubItemsCount() > 0)
			displayAccount(header);
	}

	@Override
	public void filter(String query) {
		if (recyclerView == null || refreshLayout == null) return;

		if (adapter.hasNewSearchText(query)) {
			adapter.setSearchText(query);
			adapter.filterItems(new ArrayList<>(content), 200);
		}
		// Disable SwipeRefresh and FAB if search is active!!
		if (adapter.hasSearchText()) {
			helper.getFAB().hide();
			refreshLayout.setEnabled(false);
		} else {
			helper.getFAB().show();
			refreshLayout.setEnabled(true);
		}
	}

	/**
	 * get preference for this fragment
	 *
	 * @param key this is ignored for storage tabs
	 * @return set of string that contains item that should be hidden
	 */
	@Override
	public Set<String> getPreference(String key) {
		return getPreference();
	}

	@Override
	protected boolean isShowing() {
		return helper.getViewPager().getVisibility() == View.VISIBLE &&
				recyclerView.getVisibility() == View.VISIBLE && refreshLayout.getVisibility() == View.VISIBLE;
	}

	@Override
	public void show() {
		helper.getProgressBar().setVisibility(View.GONE);
		recyclerView.setVisibility(View.VISIBLE);
		refreshLayout.setVisibility(View.VISIBLE);
		refreshLayout.setEnabled(true);
	}

	@Override
	public void hide() {
		helper.getProgressBar().setVisibility(View.VISIBLE);
		recyclerView.setVisibility(View.INVISIBLE);
		refreshLayout.setRefreshing(false);
		refreshLayout.setVisibility(View.INVISIBLE);
	}

	@Override
	public void stopRefresh() {
		refreshLayout.post(() -> refreshLayout.setRefreshing(false));
	}

	protected void setupRefreshLayout() {
		refreshLayout.setDistanceToTriggerSync(390);
		refreshLayout.setColorSchemeResources(R.color.colorAccent);
		refreshLayout.setEnabled(false);
		refreshLayout.setOnRefreshListener(StorageTabFragment.this::onRefresh);
	}

	protected void setupRecyclerView(View view) {
		recyclerView.setLayoutManager(createGridLayoutManager(view));
		recyclerView.setAdapter(adapter);
		recyclerView.setHasFixedSize(true);
		recyclerView.setItemAnimator(new DefaultItemAnimator());
		//for hide fab on scroll down and show on scroll up
		recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				if (dy > 0 && helper.getFAB().getVisibility() == View.VISIBLE) helper.getFAB().hide();
				else if (dy < 0 && helper.getFAB().getVisibility() != View.VISIBLE &&
						(adapter != null && !adapter.hasSearchText()))
					helper.getFAB().show();
			}
		});
	}

	/**
	 * get preference for this fragment
	 *
	 * @return set of string that contains item that should be hidden
	 */
	protected Set<String> getPreference() {
		return helper.getPreference(getType());
	}

	protected AccountModel getRemaining() {
		return remaining.pollFirst();
	}

	protected void addRemaining(AccountModel account) {
		if (!remaining.contains(account)) remaining.add(account);
	}

	protected boolean containRemaining(AccountModel account) {
		return remaining.contains(account);
	}

	protected boolean isRemainingEmpty() {
		return remaining.size() == 0;
	}

	/**
	 * called by swipe refresh layout
	 */
	protected abstract void onRefresh();

	protected abstract boolean isAllRefreshed();

	protected abstract VaultHeader generateContent();

	protected abstract void displayAccount(VaultHeader header);

	//check if there is more account to load or not
	protected abstract boolean checkAvailability();

	protected FloatingActionButton getFAB() {
		return helper.getFAB();
	}

	protected SearchView getSearchView() {
		return helper.getSearchView();
	}

	protected void addToContent(VaultHeader header) {
		//noinspection SuspiciousMethodCalls
		int index = items.indexOf(header.getData());
		if (index > content.size() - 1) content.add(header);
		else content.add(index, header);
	}

	protected void expandIfPossible(List<AbstractFlexibleItem> current,
	                                AbstractFlexibleItem item, List<AbstractFlexibleItem> child) {
		if (current.contains(item) && !isExpanded(current, child)) adapter.expand(item, false);
	}

	//check if given child is present in the adapter
	//if one of the child does, then the implied parent probably is expanded
	private boolean isExpanded(List<AbstractFlexibleItem> current, List<AbstractFlexibleItem> child) {
		return Stream.of(child).anyMatch(current::contains);
	}
}
