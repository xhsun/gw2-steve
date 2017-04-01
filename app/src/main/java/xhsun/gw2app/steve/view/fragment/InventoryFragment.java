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
import java.util.HashSet;
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
import xhsun.gw2app.steve.backend.database.character.StorageInfo;
import xhsun.gw2app.steve.backend.database.character.StorageWrapper;
import xhsun.gw2app.steve.backend.util.AddAccountListener;
import xhsun.gw2app.steve.backend.util.dialog.DialogManager;
import xhsun.gw2app.steve.backend.util.inventory.AccountListAdapter;

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
	private RetrieveCharacterInfo retrieveTask = null;
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

		adapter = new AccountListAdapter(getContext(), new ArrayList<AccountInfo>());

		accountList.setLayoutManager(new LinearLayoutManager(view.getContext()));
		accountList.addItemDecoration(new DividerItemDecoration(accountList.getContext(), LinearLayoutManager.VERTICAL));
		accountList.setNestedScrollingEnabled(false);
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

		retrieveTask = new RetrieveCharacterInfo(this, false);
		retrieveTask.execute();
		Timber.i("Initialization complete");
		return view;
	}

	@Override
	public void onPause() {
		super.onPause();
		if (retrieveTask != null && retrieveTask.getStatus() != AsyncTask.Status.FINISHED)
			retrieveTask.cancel(true);
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

	private void onListRefresh() {
		retrieveTask = new RetrieveCharacterInfo(this, true);
		retrieveTask.execute();
	}

	private class RetrieveCharacterInfo extends AsyncTask<Void, Void, List<AccountInfo>> {
		private InventoryFragment target;
		private boolean isRefresh;

		private RetrieveCharacterInfo(InventoryFragment target, boolean isRefresh) {
			this.target = target;
			this.isRefresh = isRefresh;
		}

		@Override
		protected void onCancelled() {
			Timber.i("Retrieve character info cancelled");
			if (isRefresh) refreshLayout.setRefreshing(false);
		}

		@Override
		protected List<AccountInfo> doInBackground(Void... params) {
			Timber.i("Start retrieve character info with isRefresh set to %s", isRefresh);
			List<AccountInfo> accounts = accountWrapper.getAll(true);
			for (AccountInfo info : accounts) {
				List<CharacterInfo> characters;
				if (isRefresh) {
					try {
						characters = characterWrapper.update(info);
					} catch (GuildWars2Exception e) {
						Timber.e(e, "Error when trying to update character information");
						return null;
					}
				} else characters = characterWrapper.getAll(info.getAPI());
				if (!updateCharacter(info, characters, isRefresh)) return null;
			}
			return accounts;
		}

		@Override
		protected void onPostExecute(List<AccountInfo> result) {
			if (result == null) {
				//TODO display error message
			} else if (result.size() == 0) {
				Timber.i("No accounts in record, prompt add account");
				new DialogManager(getFragmentManager()).promptAdd(target);
				retrieveTask = null;
				if (isRefresh) refreshLayout.setRefreshing(false);
				return;
			} else {
				Timber.i("update account list");
				adapter.setParentList(result, true);
				adapter.notifyParentRangeChanged(0, result.size());
			}

			retrieveTask = null;
			if (isRefresh) refreshLayout.setRefreshing(false);
			else startRefresh();
		}

		//true on success, false on server issue
		private boolean updateCharacter(AccountInfo info, List<CharacterInfo> characters, boolean updateStorage) {
			Set<String> prefer = preferences.getStringSet(info.getName(), null);//get preference
			if (updateStorage) return __updateCharacterWithNew(prefer, info, characters);
			__updateCharacterWithOld(prefer, info, characters);
			return true;
		}

		private void __updateCharacterWithOld(Set<String> prefer, AccountInfo info, List<CharacterInfo> characters) {
			if (prefer != null && prefer.size() > 0) {//update preference with new list of character
				for (CharacterInfo c : characters) {
					if (!prefer.contains(c.getName())) c.setEnabled(false);
					else getStorage(c);
				}
			} else {
				for (CharacterInfo c : characters) getStorage(c);
			}

			info.setCharacters(characters);//add character list to account
			setPreference(info.getName(), characters);//update preference
		}

		private void getStorage(CharacterInfo character) {
			List<StorageInfo> inventories = storageWrapper.getAll(character.getName(), false);
			character.setInventory(inventories);
		}

		private boolean __updateCharacterWithNew(Set<String> prefer, AccountInfo info, List<CharacterInfo> characters) {
			if (prefer != null && prefer.size() > 0) {//update preference with new list of character
				for (CharacterInfo c : characters) {
					if (!prefer.contains(c.getName())) c.setEnabled(false);
					else if (!updateStorage(c)) return false;
				}
			} else {
				for (CharacterInfo c : characters) if (!updateStorage(c)) return false;
			}

			info.setCharacters(characters);//add character list to account
			setPreference(info.getName(), characters);//update preference
			return true;
		}

		private boolean updateStorage(CharacterInfo character) {
			try {
				character.setInventory(storageWrapper.updateInventoryInfo(character));
			} catch (GuildWars2Exception e) {
				Timber.e(e, "Error when trying to update character inventory for %s", character.getName());
				return false;
			}
			return true;
		}

		private void setPreference(String name, List<CharacterInfo> characters) {
			SharedPreferences.Editor editor;
			Set<String> names = new HashSet<>();
			for (CharacterInfo c : characters) if (c.isEnabled()) names.add(c.getName());
			editor = preferences.edit();
			editor.putStringSet(name, names);
			editor.apply();
		}
	}
}
