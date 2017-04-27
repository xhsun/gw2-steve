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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;
import xhsun.gw2app.steve.R;
import xhsun.gw2app.steve.backend.database.account.AccountInfo;
import xhsun.gw2app.steve.backend.database.character.CharacterInfo;
import xhsun.gw2app.steve.backend.util.AddAccountListener;
import xhsun.gw2app.steve.backend.util.dialog.AccountHolder;
import xhsun.gw2app.steve.backend.util.dialog.DialogManager;
import xhsun.gw2app.steve.backend.util.inventory.AccountListAdapter;
import xhsun.gw2app.steve.backend.util.inventory.CharacterListAdapter;
import xhsun.gw2app.steve.backend.util.inventory.GetInventoryTask;
import xhsun.gw2app.steve.backend.util.inventory.OnLoadMoreListener;
import xhsun.gw2app.steve.backend.util.inventory.RetrieveAllAccountInfo;
import xhsun.gw2app.steve.backend.util.inventory.UpdateStorageTask;
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
	private Set<StorageTask> updates;
	private List<AccountInfo> accounts;
	private ArrayDeque<AccountInfo> remaining;

	private boolean isLoading = false, isMoreDataAvailable = true;

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
		View view = inflater.inflate(R.layout.fragment_inventory, container, false);
		ButterKnife.bind(this, view);

		Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
		toolbar.setTitle("Inventory");
		//load shared preference
		preferences = getActivity().getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
		updates = new HashSet<>();
		remaining = new ArrayDeque<>();
		accounts = new ArrayList<>();

		adapter = new AccountListAdapter(this, accounts);

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
				new DialogManager(getFragmentManager()).selectCharacterInventory(InventoryFragment.this, accounts);
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
		cancelAllTask();
	}

	@Override
	public Set<String> getPreferences(AccountInfo name) {
		Set<String> result = preferences.getStringSet(name.getAPI(), null);
		if (result == null) {//there is no preference for this account yet, create default
			__setPreference(name.getName(), name.getAllCharacterNames());
			result = new HashSet<>(name.getAllCharacterNames());
		}
		return result;
	}

	@Override
	//load first account
	public void loadFirstAccount() {
		remaining = new ArrayDeque<>(accounts);//transfer all to remaining
		//reset counters
		setLoading(false);
		isMoreDataAvailable = true;
		accountList.post(new Runnable() {
			@Override
			public void run() {
				if (adapter.getItemCount() > 0) adapter.removeAllData();
				//get first account to load
				List<AccountInfo> list = new ArrayList<>();
				list.add(remaining.pollFirst());
				adapter.setData(list);
			}
		});
	}

	@Override
	public void onLoadMore(@NonNull AccountInfo account) {
		if (account.isSearched() || account.getCharacters().size() == getPreferences(account).size()) {
			//nothing to get from this account, go to the next one
			account.setSearched(true);
			loadNextAccount();
		} else {//load more character inventory info
			Timber.d("Load more character inventory info for %s", account);
			setLoading(true);//set loading to true
			accountList.post(new Runnable() {
				@Override
				public void run() {
					adapter.addData(null);//show loading for user
				}
			});
			GetInventoryTask task = new GetInventoryTask(this, account);
			updates.add(task);
			task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}
	}

	@Override
	public void setPreference(List<AccountHolder> holders) {
		List<AccountInfo> changed = new ArrayList<>();
		AccountInfo temp;
		for (AccountHolder a : holders) {
			List<String> names = a.getSelectedCharacterNames();
			if (!__setPreference(a.getApi(), names)) continue;//nothing got changed
			temp = accounts.get(accounts.indexOf(new AccountInfo(a.getApi())));
			if (names.size() == 0) {//Nothing should be showing
				Timber.i("Remove %s from display due to preference change", a.getName());
				temp.setSearched(true);
				temp.setCharacters(new ArrayList<CharacterInfo>());
				remaining.remove(temp);
				int index = adapter.removeData(temp);
				if (index >= 0) adapter.notifyItemRemoved(index);
				continue;
			}
			changed.add(temp);
		}
		processPreferenceUpdate(changed);
	}

	@Override
	public synchronized boolean isLoading() {
		return isLoading;
	}

	@Override
	public synchronized void setLoading(boolean loading) {
		isLoading = loading;
	}

	@Override
	public synchronized boolean isMoreDataAvailable() {
		return isMoreDataAvailable;
	}

	@Override
	public Set<StorageTask> getUpdates() {
		return updates;
	}

	@Override
	public AccountListAdapter getAdapter() {
		return adapter;
	}

	@Override
	public List<AccountInfo> getAccounts() {
		return accounts;
	}

	@Override
	//show list and hide progress
	public void showContent() {
		accountList.setVisibility(View.VISIBLE);
		fab.setVisibility(View.VISIBLE);
		refresh.setVisibility(View.VISIBLE);
		progress.setVisibility(View.GONE);
		refresh.setRefreshing(false);
	}

	@Override
	//hide everything except progress
	public void hideContent() {
		//remove everything in the list
		if (adapter.getItemCount() > 0)
			accountList.post(new Runnable() {
				@Override
				public void run() {
					adapter.removeAllData();
				}
			});
		accounts = new ArrayList<>();
		remaining = new ArrayDeque<>();
		accountList.setVisibility(View.GONE);
		fab.setVisibility(View.GONE);
		refresh.setVisibility(View.GONE);
		refresh.setRefreshing(false);
		progress.setVisibility(View.VISIBLE);
	}

	//reload all inventory info
	private void onListRefresh() {
		cancelAllTask();//stop all other tasks
		RetrieveAllAccountInfo task = new RetrieveAllAccountInfo(this);
		task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	private void cancelAllTask() {
		for (StorageTask t : updates) {
			if (t.getStatus() != AsyncTask.Status.FINISHED) {
				t.cancel(true);
				t.setCancelled();
			}
		}
	}

	//false if nothing got changed
	private boolean __setPreference(String api, List<String> characters) {
		Timber.i("Set preference for %s to %s", api, characters);
		Set<String> names = new HashSet<>(characters);
		Set<String> result = preferences.getStringSet(api, null);
		if (result != null && result.equals(names)) return false;
		SharedPreferences.Editor editor;
		editor = preferences.edit();
		editor.putStringSet(api, names);
		editor.apply();
		return true;
	}

	//update what is currently displaying base on preference change
	private void processPreferenceUpdate(List<AccountInfo> changed) {
		//show loading for user, not that I ever see it; but in extreme cases, it might be useful
		accountList.post(new Runnable() {
			@Override
			public void run() {
				adapter.addData(null);
			}
		});
		for (AccountInfo a : changed) {
			a.setSearched(true);
			Set<String> currentDisplay = a.getCharacterNames();
			final Set<String> shouldDisplay = preferences.getStringSet(a.getAPI(), null);
			if (shouldDisplay == null) continue;//welp...
			Timber.i("Process preference update for %s with new preference: %s", a.getName(), shouldDisplay);
			if (currentDisplay.size() == 0) {//nothing is displaying for this account
				if (a.getAllCharacters().size() == 0) {//nothing is known, wait to load this account
					a.setSearched(false);//reset searched
					remaining.add(a);
					isMoreDataAvailable = true;
					if (!isLoading()) loadNextAccount();
				} else {
					final AccountInfo temp = a;
					accountList.post(new Runnable() {
						@Override
						public void run() {
							adapter.addData(accounts.indexOf(temp), temp, shouldDisplay);
						}
					});
				}
			} else {
				//find all all that should be removed from display
				Set<String> shouldRemove = new HashSet<>(currentDisplay);
				shouldRemove.removeAll(shouldDisplay);
				if (shouldRemove.size() > 0) removeWithoutLoad(a, shouldRemove);

				//find all that should be displaying
				Set<String> shouldAdd = new HashSet<>(shouldDisplay);
				shouldAdd.removeAll(currentDisplay);
				if (shouldAdd.size() > 0) displayWithoutLoad(a, shouldAdd);
			}
		}
		//remove loading
		accountList.post(new Runnable() {
			@Override
			public void run() {
				int index = adapter.removeData(null);
				if (index >= 0) adapter.notifyItemRemoved(index);
			}
		});
	}

	@Override
	//display all char in the list without disrupting anything
	public void displayWithoutLoad(final AccountInfo a, Set<String> shouldAdd) {
		Timber.i("Display %s for %s without disruption", shouldAdd, a.getName());
		for (final String name : shouldAdd) {
			CharacterInfo temp = new CharacterInfo(name);
			//inventory info for this char exist, display that
			if (a.getAllCharacters().contains(temp)) {
				final CharacterInfo info = a.getAllCharacters().get(a.getAllCharacters().indexOf(temp));
				a.getCharacters().add(info);
				a.getChild().post(new Runnable() {
					@Override
					public void run() {
						((CharacterListAdapter) a.getChild().getAdapter())
								.addDataWithoutLoad(a.getAllCharacterNames().indexOf(name), info);
					}
				});
			} else {//retrieve info from server
				UpdateStorageTask task = new UpdateStorageTask(this, a, temp, false);
				updates.add(task);
				task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			}
		}
	}

	//remove all char in the list without disrupting anything
	private void removeWithoutLoad(AccountInfo a, Set<String> shouldRemove) {
		Timber.i("Remove %s from display for %s without disruption", shouldRemove, a.getName());
		for (String name : shouldRemove) {
			final CharacterInfo info = a.getCharacters().get(
					a.getCharacters().indexOf(new CharacterInfo(name)));
			final AccountInfo temp = a;
			a.getChild().post(new Runnable() {
				@Override
				public void run() {
					//remove inventory from display
					int index = ((CharacterListAdapter) temp.getChild().getAdapter()).removeData(info);
					if (index >= 0) temp.getChild().getAdapter().notifyItemRemoved(index);
				}
			});
			//remove from list
			a.getCharacters().remove(info);
		}
	}

	//get the next account in the queue
	private void loadNextAccount() {
		accountList.post(new Runnable() {
			@Override
			public void run() {
				AccountInfo next = remaining.pollFirst();
				if (next == null) isMoreDataAvailable = false;
				else adapter.addData(next);
			}
		});
	}
}
