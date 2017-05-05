package xhsun.gw2app.steve.view.fragment.storage;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import xhsun.gw2app.steve.R;
import xhsun.gw2app.steve.backend.database.account.AccountInfo;
import xhsun.gw2app.steve.backend.util.CancellableAsyncTask;
import xhsun.gw2app.steve.backend.util.items.StorageType;
import xhsun.gw2app.steve.backend.util.storage.BankListAdapter;
import xhsun.gw2app.steve.backend.util.storage.StorageTabFragment;

/**
 * A simple {@link Fragment} subclass.
 *
 * @author xhsun
 * @since 2017-05-03
 */
public class BankFragment extends StorageTabFragment<BankListAdapter> {
	private BankListAdapter adapter;
	private Set<CancellableAsyncTask> updates;
	private ArrayDeque<AccountInfo> remaining;

	@BindView(R.id.bank_refresh)
	SwipeRefreshLayout refreshLayout;
	@BindView(R.id.bank_account_list)
	RecyclerView recyclerView;

	public BankFragment() {
		super.setType(StorageType.BANK);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_bank, container, false);
		ButterKnife.bind(this, view);

		updates = new HashSet<>();

		adapter = new BankListAdapter(this, provider.getAccounts());

		recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
		recyclerView.setAdapter(adapter);
		recyclerView.setNestedScrollingEnabled(false);

		refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				isRefresh = true;
				onListRefresh();
			}
		});

		if (provider.getAccounts().size() > 0)
			loadFirstAccount();
		return view;
	}

	@Override
	public void filter(String query) {
		//TODO
	}

	@Override
	public void restore() {
		//TODO
	}

	@Override
	public void processPreferenceChange(Set<AccountInfo> preference) {
		//TODO
	}

	@Override
	public void notifyAccountUpdate() {
		//TODO
	}

	@Override
	protected void onListRefresh() {
		//TODO
	}

	@Override
	public void loadFirstAccount() {
		hideContent();
		//TODO
	}

	@Override
	public void onLoadMore(AccountInfo account) {
		//TODO
	}

	@Override
	public void displayWithoutLoad(AccountInfo a, Set<String> shouldAdd) {
		//TODO
	}

	@Override
	public boolean isLoading() {
		return isLoading;
	}

	@Override
	public synchronized void setLoading(boolean loading) {
		isLoading = loading;
	}

	@Override
	public boolean isMoreDataAvailable() {
		return isMoreDataAvailable;
	}

	@Override
	public boolean isRefresh() {
		return isRefresh;
	}

	@Override
	public Set<String> getPreferences(StorageType value) {
		return provider.getPreferences(value);
	}

	@Override
	public String getQuery() {
		return query;
	}

	@Override
	public Set<CancellableAsyncTask> getUpdates() {
		return updates;
	}

	@Override
	public BankListAdapter getAdapter() {
		return adapter;
	}

	@Override
	public List<AccountInfo> getAccounts() {
		return provider.getAccounts();
	}

	@Override
	public RecyclerView provideParentView() {
		return recyclerView;
	}

	@Override
	public void showContent() {
		provider.showContent();
	}

	@Override
	public void hideContent() {
		provider.hideContent();
	}
}
