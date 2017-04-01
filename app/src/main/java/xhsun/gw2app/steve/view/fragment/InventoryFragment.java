package xhsun.gw2app.steve.view.fragment;


import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;
import xhsun.gw2api.guildwars2.err.GuildWars2Exception;
import xhsun.gw2app.steve.MainApplication;
import xhsun.gw2app.steve.R;
import xhsun.gw2app.steve.backend.database.account.AccountInfo;
import xhsun.gw2app.steve.backend.database.account.AccountWrapper;
import xhsun.gw2app.steve.backend.database.character.CharacterInfo;
import xhsun.gw2app.steve.backend.database.character.CharacterWrapper;
import xhsun.gw2app.steve.backend.database.character.StorageWrapper;
import xhsun.gw2app.steve.backend.util.AddAccountListener;
import xhsun.gw2app.steve.backend.util.dialog.DialogManager;
import xhsun.gw2app.steve.backend.util.inventory.AccountListAdapter;
import xhsun.gw2app.steve.backend.util.storage.EndlessRecyclerOnScrollListener;
import xhsun.gw2app.steve.backend.util.storage.StorageTask;

import static android.content.Context.MODE_PRIVATE;

/**
 * InventoryFragment is a subclass of {@link Fragment}<br/>
 *
 * @author xhsun
 * @since 2017-03-28
 */
public class InventoryFragment extends Fragment implements AddAccountListener {
	private static final String PREFERENCE_NAME = "inventoryDisplay";
	private AccountListAdapter adapter;
	private SharedPreferences preferences;
	private List<StorageTask> updates;
	@Inject
	StorageWrapper storageWrapper;
	@Inject
	CharacterWrapper characterWrapper;
	@Inject
	AccountWrapper accountWrapper;

	@BindView(R.id.inventory_account_list)
	RecyclerView accountList;
	@BindView(R.id.inventory_refresh)
	SwipeRefreshLayout refreshLayout;
	@BindView(R.id.inventory_fab)
	FloatingActionButton fab;

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

//		adapter = new AccountListAdapter(getContext(), new ArrayList<AccountInfo>());

		accountList.setLayoutManager(new LinearLayoutManager(view.getContext()));
		accountList.addItemDecoration(new DividerItemDecoration(accountList.getContext(), LinearLayoutManager.VERTICAL));
		accountList.setNestedScrollingEnabled(false);

		refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
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

//		retrieveTask = new RetrieveCharacterInfo(this, false);
//		retrieveTask.execute();
		RetrieveBasicInfo task = new RetrieveBasicInfo(this);
		updates.add(task);
		task.execute();

		Timber.i("Initialization complete");
		return view;
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

	@Override
	public void addAccountCallback(AccountInfo account) {
		onListRefresh();
	}

	//start refresh by code
	private void startRefresh() {
		refreshLayout.post(new Runnable() {
			@Override
			public void run() {
				refreshLayout.setRefreshing(true);
				onListRefresh();
			}
		});
	}

	private void onLoadMore(int page) {
		UpdateCharacter task = new UpdateCharacter(this, adapter.getParentList());
		updates.add(task);
		task.execute();
	}



	private void onListRefresh() {
//		retrieveTask = new RetrieveCharacterInfo(this, true);
//		retrieveTask.execute();
	}

	private class RetrieveBasicInfo extends StorageTask<Void, Void, List<AccountInfo>> {
		private InventoryFragment target;

		private RetrieveBasicInfo(InventoryFragment target) {
			this.target = target;
			accountWrapper.setCancelled(false);
		}

		@Override
		protected void onCancelled() {
			Timber.i("Retrieve character info cancelled");
			characterWrapper.setCancelled(true);
		}

		//TODO when app first open nothing is here, maybe pull something from database?
		@Override
		protected List<AccountInfo> doInBackground(Void... params) {
			List<AccountInfo> accounts = accountWrapper.getAll(true);
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
		protected void onPostExecute(List<AccountInfo> result) {
			if (isCancelled() || isCancelled) return;
			if (result == null) {
				//TODO show error message
			} else if (result.size() == 0) {
				new DialogManager(getFragmentManager()).promptAdd(target);
			} else {
				Timber.i("Resulting list: %s", result);
				adapter = new AccountListAdapter(getContext(), result);
				accountList.setAdapter(adapter);
				accountList.addOnScrollListener(new EndlessRecyclerOnScrollListener((LinearLayoutManager) accountList.getLayoutManager()) {
					@Override
					public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
						target.onLoadMore(page);
					}
				});
			}
			updates.remove(this);
		}
	}

	private class UpdateCharacter extends StorageTask<Void, Void, CharacterInfo> {
		private InventoryFragment target;
		private List<AccountInfo> accounts;

		private UpdateCharacter(InventoryFragment target, List<AccountInfo> accounts) {
			this.target = target;
			this.accounts = accounts;
			characterWrapper.setCancelled(false);
		}

		@Override
		protected void onCancelled() {
			Timber.i("Retrieve character info cancelled");
			characterWrapper.setCancelled(true);
		}

		@Override
		protected CharacterInfo doInBackground(Void... params) {
			CharacterInfo character = getCurrentName();
			if (character == null) return null;
			character.setInventory(storageWrapper.getAll(character.getName(), false));
			return character;
		}

		@Override
		protected void onPostExecute(CharacterInfo result) {
			if (isCancelled() || isCancelled) return;
			if (result == null) {
				//TODO show error
			} else {
				if (result.getInventory().size() > 0)
					adapter.notifyChildInserted(result.getParentPosition(), result.getSelfPosition());
				//start updating storage information for this character
				UpdatedStorageInfo task = new UpdatedStorageInfo(target);
				updates.add(task);
				task.execute(result);
			}
			updates.remove(this);
		}

		private CharacterInfo getCurrentName() {
			for (AccountInfo account : accounts) {
				if (isCancelled() || isCancelled) return null;
				if (account.isSearched()) continue;
				String name = "";
				Set<String> prefer = preferences.getStringSet(account.getName(), null);
				Timber.i("Preference for %s is %s", account.getName(), prefer);
				for (String c : account.getCharacterNames()) {
					if (isCancelled() || isCancelled) return null;
					if (account.getChildList().contains(new CharacterInfo(c))) continue;
					if (!(prefer != null && prefer.size() > 0 && !prefer.contains(c))) {
						name = c;
						break;
					}
				}
				Timber.i("Name for this session is %s", name);
				if (!name.equals("")) {
					try {
						CharacterInfo info = characterWrapper.update(account.getAPI(), name);
						if (account.getChildList().contains(info)) {
							CharacterInfo old = account.getChildList().get(account.getChildList().indexOf(info));
							old.update(info);
							info = old;
						} else {
							info.setParentPosition(account.getSelfPosition());
							account.getChildList().add(info);
							info.setSelfPosition(account.getChildList().indexOf(info));
						}
						return info;
					} catch (GuildWars2Exception e) {
						//TODO maybe show error?
						return null;
					}
				}
			}
			return null;
		}
	}

	private class UpdatedStorageInfo extends StorageTask<CharacterInfo, Void, CharacterInfo> {
		private InventoryFragment target;
		private boolean isShowing = false;

		private UpdatedStorageInfo(InventoryFragment target) {
			this.target = target;
			storageWrapper.setCancelled(false);
		}

		@Override
		protected void onCancelled() {
			Timber.i("Retrieve character info cancelled");
			storageWrapper.setCancelled(true);
		}

		@Override
		protected CharacterInfo doInBackground(CharacterInfo... params) {
			CharacterInfo info = params[0];
			if (info == null) return null;
			try {
				if (info.getInventory().size() > 0) isShowing = true;
				info.setInventory(storageWrapper.updateInventoryInfo(info));
				return info;
			} catch (GuildWars2Exception e) {
				return null;
			}
		}

		@Override
		protected void onPostExecute(CharacterInfo result) {
			if (isCancelled() || isCancelled) return;
			if (result == null) {
				//TODO show error
			} else if (result.getInventory().size() > 0) {
				int parent = result.getParentPosition();
				int child = result.getSelfPosition();
				if (parent == -1 || child == -1) {
					return;
				}
				if (isShowing) adapter.notifyChildChanged(parent, child);
				else adapter.notifyChildInserted(parent, child);
			}
			updates.remove(this);
		}
	}
}
