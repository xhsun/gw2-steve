package xhsun.gw2app.steve.view.fragment;


import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;
import xhsun.gw2api.guildwars2.err.GuildWars2Exception;
import xhsun.gw2app.steve.MainApplication;
import xhsun.gw2app.steve.R;
import xhsun.gw2app.steve.backend.database.account.AccountInfo;
import xhsun.gw2app.steve.backend.database.account.AccountWrapper;
import xhsun.gw2app.steve.backend.database.character.CharacterWrapper;
import xhsun.gw2app.steve.backend.database.character.StorageWrapper;
import xhsun.gw2app.steve.backend.util.AddAccountListener;
import xhsun.gw2app.steve.backend.util.dialog.DialogManager;
import xhsun.gw2app.steve.backend.util.inventory.AccountListAdapter;
import xhsun.gw2app.steve.backend.util.inventory.GetInventoryTask;
import xhsun.gw2app.steve.backend.util.inventory.OnLoadMoreListener;
import xhsun.gw2app.steve.backend.util.storage.StorageTask;

import static android.content.Context.MODE_PRIVATE;

/**
 * InventoryFragment is a subclass of {@link Fragment}<br/>
 *
 * @author xhsun
 * @since 2017-03-28
 */
public class InventoryFragment extends Fragment implements AddAccountListener, OnLoadMoreListener {
	private static final String PREFERENCE_NAME = "inventoryDisplay";
	private AccountListAdapter adapter;
	private SharedPreferences preferences;
	private List<StorageTask> updates;
	private ArrayDeque<AccountInfo> accounts;

	private boolean isLoading = false, isMoreDataAvailable = true;

	@Inject
	StorageWrapper storageWrapper;
	@Inject
	CharacterWrapper characterWrapper;
	@Inject
	AccountWrapper accountWrapper;

	@BindView(R.id.inventory_account_list)
	RecyclerView accountList;
	@BindView(R.id.inventory_refresh)
	SwipeRefreshLayout refresh;
	@BindView(R.id.inventory_fab)
	FloatingActionButton fab;
	@BindView(R.id.inventory_progress)
	ProgressBar progress;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		((MainApplication) getActivity().getApplication()).getServiceComponent().inject(this);//injection
		View view = inflater.inflate(R.layout.fragment_inventory, container, false);
		ButterKnife.bind(this, view);

		Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
		toolbar.setTitle("Inventory");
		//load shared preference
		preferences = getActivity().getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
		updates = new ArrayList<>();

		adapter = new AccountListAdapter(this, new ArrayList<AccountInfo>());

		accountList.setLayoutManager(new LinearLayoutManager(view.getContext()));
		accountList.addItemDecoration(new DividerItemDecoration(accountList.getContext(), LinearLayoutManager.VERTICAL));
		accountList.setAdapter(adapter);

		refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				onListRefresh();
			}
		});
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//TODO pull up dialog to select character to show
			}
		});
		//for hide fab on scroll down and show on scroll up
		accountList.addOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				if (dy > 0 && fab.getVisibility() == View.VISIBLE) fab.hide();
				else if (dy < 0 && fab.getVisibility() != View.VISIBLE) fab.show();
			}
		});

		//getting all account info
		RetrieveAllAccountInfo task = new RetrieveAllAccountInfo(this);
		updates.add(task);
		task.execute();

		Timber.i("Initialization complete");
		return view;
	}


	@Override
	public void addAccountCallback(AccountInfo account) {
		onListRefresh();
	}

	@Override
	public void onPause() {
		Timber.i("Paused Fragment");
		super.onPause();
		for (StorageTask t : updates) {
			if (t.getStatus() != AsyncTask.Status.FINISHED) {
				t.cancel(true);
				t.setCancelled();
			}
		}
	}

	private void onListRefresh() {
//		retrieveTask = new RetrieveCharacterInfo(this, true);
//		retrieveTask.execute();
	}

	@Override
	public boolean isLoading() {
		return isLoading;
	}

	@Override
	public void setLoading(boolean loading) {
		isLoading = loading;
	}

	@Override
	public boolean isMoreDataAvailable() {
		return isMoreDataAvailable;
	}

	@Override
	public CharacterWrapper getCharacterWrapper() {
		return characterWrapper;
	}

	@Override
	public StorageWrapper getStorageWrapper() {
		return storageWrapper;
	}

	@Override
	public SharedPreferences getPreferences() {
		return preferences;
	}

	@Override
	public List<StorageTask> getUpdates() {
		return updates;
	}

	@Override
	public AccountListAdapter getAdapter() {
		return adapter;
	}

	@Override
	public void OnLoadMore(@NonNull AccountInfo account) {
		if (account.isSearched() || account.getCharacters().size() == account.getCharacterNames().size()) {//nothing to get from this account, go to the next one
			account.setSearched(true);
			loadNextAccount();
		} else {//load more character inventory info
			Timber.d("Load more character inventory info for %s", account);
			isLoading = true;//set loading to true
			accountList.post(new Runnable() {
				@Override
				public void run() {
					adapter.addData(null);//show loading for user
				}
			});
			GetInventoryTask task = new GetInventoryTask(this, account);
			updates.add(task);
			task.execute();
		}
	}

	//get the next account in the queue
	private void loadNextAccount() {
		accountList.post(new Runnable() {
			@Override
			public void run() {
				AccountInfo next = accounts.pollFirst();
				if (next == null) isMoreDataAvailable = false;
				else adapter.addData(next);
			}
		});
	}

	//start refresh by code
//	private void startRefresh() {
//		refreshLayout.post(new Runnable() {
//			@Override
//			public void run() {
//				refreshLayout.setRefreshing(true);
//				onListRefresh();
//			}
//		});
//	}

	private class RetrieveAllAccountInfo extends StorageTask<Void, Void, ArrayDeque<AccountInfo>> {
		private InventoryFragment target;

		private RetrieveAllAccountInfo(InventoryFragment target) {
			this.target = target;
			accountWrapper.setCancelled(false);
		}

		@Override
		public void onPreExecute() {
			//remove everything in the list
			if (adapter.getItemCount() > 0) adapter.removeAllData();
			fab.setVisibility(View.GONE);
			refresh.setVisibility(View.GONE);
			progress.setVisibility(View.VISIBLE);
		}

		@Override
		protected void onCancelled() {
			Timber.i("Retrieve character info cancelled");
			characterWrapper.setCancelled(true);
			showContent();
		}

		@Override
		protected ArrayDeque<AccountInfo> doInBackground(Void... params) {
			ArrayDeque<AccountInfo> accounts = new ArrayDeque<>(accountWrapper.getAll(true));
			for (AccountInfo account : accounts) {
				if (isCancelled() || isCancelled) break;
				try {
					account.setCharacterNames(characterWrapper.getAllNames(account.getAPI()));
				} catch (GuildWars2Exception e) {
					return null;//for error process
				}
			}
			return accounts;
		}

		@Override
		protected void onPostExecute(ArrayDeque<AccountInfo> result) {
			if (isCancelled() || isCancelled) return;
			if (result == null) {
				//TODO show error message
			} else if (result.size() == 0) {
				new DialogManager(getFragmentManager()).promptAdd(target);
			} else {
				accounts = result;//store all account info
				//get first account to load
				List<AccountInfo> list = new ArrayList<>();
				list.add(accounts.pollFirst());
				adapter.setData(list);
			}
			updates.remove(this);
			showContent();
		}

		//show list and hide progress
		private void showContent() {
			fab.setVisibility(View.VISIBLE);
			refresh.setVisibility(View.VISIBLE);
			progress.setVisibility(View.GONE);
			refresh.setRefreshing(false);
		}
	}
}
