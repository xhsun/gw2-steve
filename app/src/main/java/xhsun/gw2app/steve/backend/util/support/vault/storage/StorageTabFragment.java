package xhsun.gw2app.steve.backend.util.support.vault.storage;

import android.os.AsyncTask;
import android.support.annotation.CallSuper;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.InputType;
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
import xhsun.gw2app.steve.backend.data.model.AbstractModel;
import xhsun.gw2app.steve.backend.data.model.AccountModel;
import xhsun.gw2app.steve.backend.util.items.vault.VaultHeader;
import xhsun.gw2app.steve.backend.util.support.vault.VaultType;
import xhsun.gw2app.steve.backend.util.support.vault.load.AbstractContentFragment;
import xhsun.gw2app.steve.backend.util.task.vault.UpdateVaultTask;

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
	public boolean shouldLoad() {
		List<AbstractFlexibleItem> current = adapter.getCurrentItems();
		Set<String> prefer = getPreference();

		Stream.of(items)
				.filter(a -> !prefer.contains(a.getAPI()) && !current.contains(new VaultHeader<>(a)))
				.forEach(r -> r.setSearched(false));
		return Stream.of(items).anyMatch(a -> !a.isSearched());
	}

	@Override
	public void loadNextData() {
		VaultHeader header = generateContent();
		if (header == null) adapter.onLoadMoreComplete(null, 200);
		else if (header.getSubItemsCount() > 0)
			displayAccount(header);
	}

	@Override
	public void updateData(AbstractModel data) {
		VaultHeader header;
		AccountModel account = (AccountModel) data;
		account.setSearched(true);

		if ((header = generateHeader(account)).getSubItemsCount() == 0) {
			adapter.onLoadMoreComplete(null, 200);
			return;//welp... something is really wrong
		}

		displayAccount(header);
	}

	@Override
	public void refreshData(AbstractModel data) {
		if (refreshedContent == null) return;
		AccountModel account = (AccountModel) data;
		int index = items.indexOf(account);

		//get account
		VaultHeader header = generateHeader((AccountModel) data);
		if (!refreshedContent.contains(header)) {
			if (index < refreshedContent.size()) refreshedContent.add(index, header);
			else refreshedContent.add(header);
		} else refreshedContent.set(refreshedContent.indexOf(header), header);

		if (isAllRefreshed()) {
			content = refreshedContent;
			refreshedContent = null;
			adapter.updateDataSet(content, true);
			refreshLayout.post(() -> {
				getSearchView().setInputType(InputType.TYPE_TEXT_VARIATION_FILTER);
				refreshLayout.setRefreshing(false);
				getFAB().show();
			});
			onUpdateEmptyView(0);
		}
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
	public void processChange(Set<AccountModel> preference) {
		cancelAllTask();
		Stream.of(preference)
				.filter(a -> adapter.contains(new VaultHeader<AccountModel, AbstractFlexibleItem>(a)))
				.forEach(r -> {
					VaultHeader temp = new VaultHeader<>(r);
					adapter.removeItem(adapter.getGlobalPositionOf(temp));
					content.remove(temp);
				});

		if (shouldLoad()) loadNextData();
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

	protected VaultHeader generateContent() {
		VaultHeader header;

		AccountModel next = getRemaining();
		if (next == null) {
			if (checkAvailability()) next = getRemaining();
			else return null;
		}

		//check the generated header
		if ((header = generateHeader(next)).getSubItemsCount() == 0) {
			new UpdateVaultTask<>(this, next).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else next.setSearched(true);
		return header;
	}

	protected void displayAccount(VaultHeader header) {
		if (adapter.contains(header)) adapter.updateDataSet(content, true);
		else {
			int index = -1;
			//noinspection unchecked
			String api = ((VaultHeader<AccountModel, AbstractFlexibleItem>) header).getData().getAPI();
			AccountModel next;

			//noinspection SuspiciousMethodCalls
			if ((next = getNextAvailable(api, api, items.indexOf(header.getData()))) != null)
				index = adapter.getGlobalPositionOf(new VaultHeader<>(next));

			if (index < 0 && (index = adapter.getGlobalPositionOf(load)) < 0) adapter.addItem(header);
			else adapter.addItem(index, header);
		}
		onUpdateEmptyView(0);

		adapter.onLoadMoreComplete(null, 200);
	}

	//check if there is more account to load or not
	protected boolean checkAvailability() {
		Set<String> pref = getPreference();
		Stream.of(items).filter(a -> !a.isSearched() && !containRemaining(a) && !pref.contains(a.getAPI()))
				.forEach(this::addRemaining);

		return !isRemainingEmpty();
	}

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

	/**
	 * called by swipe refresh layout
	 */
	protected void onRefresh() {
		cancelAllTask();
		refreshLayout.post(() -> {
			getSearchView().clearFocus();
			getSearchView().setIconified(true);
			getSearchView().setInputType(InputType.TYPE_NULL);
			getFAB().hide();
			refreshLayout.setRefreshing(true);
		});
		refreshedContent = new ArrayList<>();
		Set<String> pref = getPreference();
		Stream.of(items).filterNot(a -> pref.contains(a.getAPI()))
				.forEach(r -> new UpdateVaultTask<>(this, r, true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR));
	}

	protected boolean isAllRefreshed() {
		return refreshedContent.size() >= (items.size() - getPreference().size());
	}

	protected abstract VaultHeader generateHeader(AccountModel account);

	//check if given child is present in the adapter
	//if one of the child does, then the implied parent probably is expanded
	private boolean isExpanded(List<AbstractFlexibleItem> current, List<AbstractFlexibleItem> child) {
		return Stream.of(child).anyMatch(current::contains);
	}
}
