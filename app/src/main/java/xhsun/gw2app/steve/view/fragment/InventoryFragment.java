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
import xhsun.gw2app.steve.backend.util.inventory.GetCharacterTask;
import xhsun.gw2app.steve.backend.util.inventory.WrapperProvider;
import xhsun.gw2app.steve.backend.util.storage.StorageTask;

import static android.content.Context.MODE_PRIVATE;

/**
 * InventoryFragment is a subclass of {@link Fragment}<br/>
 *
 * @author xhsun
 * @since 2017-03-28
 */
public class InventoryFragment extends Fragment implements AddAccountListener, WrapperProvider {
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

		adapter = new AccountListAdapter(this, new ArrayList<AccountInfo>());

		accountList.setLayoutManager(new LinearLayoutManager(view.getContext()));
		accountList.addItemDecoration(new DividerItemDecoration(accountList.getContext(), LinearLayoutManager.VERTICAL));
		accountList.setAdapter(adapter);

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

	public void onLoad(AccountInfo account) {
		Timber.d("Load character from %s", account);
		if (account == null || account.isSearched()) return;
		GetCharacterTask task = new GetCharacterTask(this, account);
		updates.add(task);
		task.execute();
	}

	private void onListRefresh() {
//		retrieveTask = new RetrieveCharacterInfo(this, true);
//		retrieveTask.execute();
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
				adapter.setData(result);
				target.onLoad(result.get(0));
			}
			updates.remove(this);
		}
	}
}
