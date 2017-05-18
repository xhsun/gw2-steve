package xhsun.gw2app.steve.view.fragment.storage;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import timber.log.Timber;
import xhsun.gw2app.steve.R;
import xhsun.gw2app.steve.backend.data.AbstractData;
import xhsun.gw2app.steve.backend.data.AccountInfo;
import xhsun.gw2app.steve.backend.util.items.BasicItem;
import xhsun.gw2app.steve.backend.util.storage.StorageTabFragment;
import xhsun.gw2app.steve.backend.util.vault.UpdateVaultTask;
import xhsun.gw2app.steve.backend.util.vault.VaultHeader;
import xhsun.gw2app.steve.backend.util.vault.VaultSubHeader;
import xhsun.gw2app.steve.backend.util.vault.VaultType;

/**
 * A simple {@link Fragment} subclass.
 *
 * @author xhsun
 * @since 2017-05-03
 */
public class BankFragment extends StorageTabFragment {

	public BankFragment() {
		super(VaultType.BANK);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_bank, container, false);
		setRetainInstance(true);

		recyclerView = (RecyclerView) view.findViewById(R.id.bank_account_list);
		setupRecyclerView(view);

		refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.bank_refresh);
		setupRefreshLayout();

		hide();
		onDataUpdate();
		Timber.i("Initialization complete");
		return view;
	}

	@Override
	public void updateData(AbstractData data) {
		VaultHeader<AccountInfo, BasicItem> header;
		AccountInfo account = (AccountInfo) data;
		account.setSearched(true);

		if ((header = generateHeader(account)).getSubItemsCount() == 0) {
			adapter.onLoadMoreComplete(null, 200);
			return;//welp... something is really wrong
		}

		displayAccount(header);
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
	public void refreshData(AbstractData data) {
		if (refreshedContent == null) return;
		AccountInfo account = (AccountInfo) data;
		int index = items.indexOf(account);
		//get account
		VaultHeader<AccountInfo, BasicItem> header = generateHeader((AccountInfo) data);
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
		}
	}

	@Override
	public void processChange(Set<AccountInfo> preference) {
		cancelAllTask();
		Stream.of(preference)
				.filter(a -> adapter.contains(new VaultHeader<AccountInfo, VaultSubHeader>(a)))
				.forEach(r -> {
					VaultHeader temp = new VaultHeader<AccountInfo, VaultSubHeader>(r);
					adapter.removeItem(adapter.getGlobalPositionOf(temp));
					content.remove(temp);
				});

		if (shouldLoad()) loadNextData();
	}

	@Override
	public void onUpdateEmptyView(int size) {
		if (adapter == null || content == null) return;
//		adapter.expandAll();
		List<AbstractFlexibleItem> current = adapter.getCurrentItems();
		//noinspection unchecked
		Stream.of(content).filter(current::contains)
				.forEach(r -> expandIfPossible(current, r, new ArrayList<>(((VaultHeader) r).getSubItems())));
	}

	@Override
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
				.forEach(r -> new UpdateVaultTask(this, r, true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR));
	}

	@Override
	protected VaultHeader generateContent() {
		VaultHeader<AccountInfo, BasicItem> header;

		AccountInfo next = getRemaining();
		if (next == null) {
			if (checkAvailability()) next = getRemaining();
			else return null;
		}

		//check the generated header
		if ((header = generateHeader(next)).getSubItemsCount() == 0) {
			new UpdateVaultTask(this, next).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else next.setSearched(true);
		return header;
	}

	@Override
	protected void displayAccount(VaultHeader header) {
		if (adapter.contains(header)) adapter.updateDataSet(content, true);
		else {
			int index = -1;
			//noinspection unchecked
			String api = ((VaultHeader<AccountInfo, BasicItem>) header).getData().getAPI();
			AccountInfo next;

			//noinspection SuspiciousMethodCalls
			if ((next = getNextAvailable(api, api, items.indexOf(header.getData()))) != null)
				index = adapter.getGlobalPositionOf(new VaultHeader<>(next));

			if (index < 0 && (index = adapter.getGlobalPositionOf(load)) < 0) adapter.addItem(header);
			else adapter.addItem(index, header);
		}
		onUpdateEmptyView(0);

		adapter.onLoadMoreComplete(null, 200);
	}

	@Override
	protected synchronized boolean checkAvailability() {
		Set<String> pref = getPreference();
		Stream.of(items).filter(a -> !a.isSearched() && !containRemaining(a) && !pref.contains(a.getAPI()))
				.forEach(this::addRemaining);

		return !isRemainingEmpty();
	}

	@Override
	protected boolean isAllRefreshed() {
		return refreshedContent.size() >= (items.size() - getPreference().size());
	}

	@SuppressWarnings("unchecked")
	private VaultHeader<AccountInfo, BasicItem> generateHeader(AccountInfo account) {
		VaultHeader<AccountInfo, BasicItem> result = new VaultHeader<>(account);
		if (account.getBank().size() == 0) return result;

		if (content.contains(result)) result = (VaultHeader) content.get(content.indexOf(result));
		else addToContent(result);

		VaultHeader<AccountInfo, BasicItem> temp = result;
		result.setSubItems(Stream.of(account.getBank()).filterNot(i -> temp.containsSubItem(new BasicItem(i, this)))
				.map(r -> new BasicItem(r, this)).collect(Collectors.toList()));

		return result;
	}
}
