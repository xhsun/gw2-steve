package xhsun.gw2app.steve.view.fragment.storage;


import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;
import xhsun.gw2app.steve.MainApplication;
import xhsun.gw2app.steve.R;
import xhsun.gw2app.steve.backend.database.account.AccountInfo;
import xhsun.gw2app.steve.backend.database.account.AccountWrapper;
import xhsun.gw2app.steve.backend.util.AddAccountListener;
import xhsun.gw2app.steve.backend.util.CancellableAsyncTask;
import xhsun.gw2app.steve.backend.util.dialog.DialogManager;
import xhsun.gw2app.steve.backend.util.dialog.selectAccount.AccountHolder;
import xhsun.gw2app.steve.backend.util.items.QueryTextListener;
import xhsun.gw2app.steve.backend.util.items.StorageContentFragment;
import xhsun.gw2app.steve.backend.util.items.StorageType;
import xhsun.gw2app.steve.backend.util.storage.PreferenceModifySupport;
import xhsun.gw2app.steve.backend.util.storage.StoragePagerAdapter;
import xhsun.gw2app.steve.backend.util.storage.StorageTabContentSupport;
import xhsun.gw2app.steve.backend.util.storage.StorageTabFragment;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 *
 * @author xhsun
 * @since 2017-05-01
 */
public class StorageFragment extends Fragment implements PreferenceModifySupport, AddAccountListener,
		StorageTabContentSupport {
	private static final String PREFERENCE_NAME = "storageDisplay";
	private SharedPreferences preferences;
	private List<AccountInfo> accounts;
	private List<StorageTabFragment> tabs;
	private InitAccounts task;

	@Inject
	AccountWrapper accountWrapper;

	private SearchView search;
	@BindView(R.id.storage_tab)
	TabLayout tabLayout;
	@BindView(R.id.storage_viewpager)
	ViewPager viewPager;
	@BindView(R.id.storage_fab)
	FloatingActionButton fab;
	@BindView(R.id.storage_progress)
	ProgressBar progressBar;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		((MainApplication) getActivity().getApplication()).getServiceComponent().inject(this);//injection
		View view = inflater.inflate(R.layout.fragment_storage, container, false);
		ButterKnife.bind(this, view);

		Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
		toolbar.setTitle("Storage");
		setHasOptionsMenu(true);

		accounts = new ArrayList<>();

		//load shared preference
		preferences = getActivity().getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);

		//init tabs
		setupTabFragments();
		viewPager.setAdapter(new StoragePagerAdapter(getFragmentManager(), tabs));
		tabLayout.setupWithViewPager(viewPager);

		//TODO fab

		task = new InitAccounts();
		task.execute();
		Timber.i("Initialization complete");
		return view;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();//prevent constantly adding stuff to toolbar
		inflater.inflate(R.menu.fragment_search_toolbar, menu);
		setupSearchView(menu);//set up search box
		super.onCreateOptionsMenu(menu, inflater);
		Timber.i("Toolbar setup finished");
	}

	@Override
	public void onPause() {
		super.onPause();
		cancelTask();
	}

	@Override
	public void addAccountCallback(AccountInfo account) {
		cancelTask();
		task = new InitAccounts();
		task.execute();
	}

	//show content
	public synchronized void showContent() {
		fab.setVisibility(View.VISIBLE);
		viewPager.setVisibility(View.VISIBLE);
		progressBar.setVisibility(View.GONE);
	}

	//hide content
	public synchronized void hideContent() {
		fab.setVisibility(View.GONE);
		viewPager.setVisibility(View.GONE);
		progressBar.setVisibility(View.VISIBLE);
	}

	@Override
	public Set<String> getPreferences(StorageType type) {
		Set<String> result = preferences.getStringSet(type.name(), null);
		if (result == null) {//there is no preference for this account yet, create default
			result = new HashSet<>();
			for (AccountInfo a : accounts) result.add(a.getAPI());
			__setPreference(type.name(), result);
		}
		return result;
	}

	@Override
	public void setPreference(StorageType type, Set<AccountHolder> accounts) {
		Set<AccountInfo> prefer = new HashSet<>();
		Set<String> preferAPI = new HashSet<>();
		for (AccountHolder a : accounts) {
			if (a.isSelected()) {
				prefer.add(new AccountInfo(a.getApi()));
				preferAPI.add(a.getApi());
			}
		}

		if (__setPreference(type.name(), preferAPI)) {
			for (StorageContentFragment f : tabs)
				if (f.getType() == type) f.processPreferenceChange(prefer);
		}
		//clear focus of search no matter what
		search.clearFocus();
	}

	@Override
	public synchronized List<AccountInfo> getAccounts() {
		return accounts;
	}


	//update preference in file
	private boolean __setPreference(String type, Set<String> result) {
		Timber.i("Set preference for %s to %s", type, result);
		Set<String> process = preferences.getStringSet(type, null);
		if (process != null && process.equals(result)) return false;
		SharedPreferences.Editor editor;
		editor = preferences.edit();
		editor.putStringSet(type, result);
		editor.apply();
		return true;
	}

	private void setupTabFragments() {
		StorageTabFragment fragment;
		tabs = new ArrayList<>();
		fragment = new BankFragment();
		fragment.setProvider(this);
		tabs.add(fragment);
		//TODO other fragments
	}

	//setup search with with search hint and listener
	private void setupSearchView(Menu menu) {
		search = (SearchView) menu.findItem(R.id.toolbar_search).getActionView();
		search.setIconified(true);
		search.setQueryHint("Search Storage");
		Set<StorageContentFragment> fragments = new HashSet<>();
		for (StorageContentFragment f : tabs) fragments.add(f);
		search.setOnQueryTextListener(new QueryTextListener(fragments));
		Timber.i("SearchView setup finished");
	}

	//cancel task
	private void cancelTask() {
		if (task != null && task.getStatus() != AsyncTask.Status.FINISHED)
			task.cancel(true);
	}

	//notify current tab
	private void accountUpdated() {
		tabs.get(tabLayout.getSelectedTabPosition()).notifyAccountUpdate();
	}

	//get all account info
	private class InitAccounts extends CancellableAsyncTask<Void, Void, List<AccountInfo>> {

		@Override
		protected void onCancelled() {
			Timber.i("init account info cancelled");
			accountWrapper.setCancelled(true);
			showContent();
		}

		@Override
		protected void onPreExecute() {
			accountWrapper.setCancelled(false);
			hideContent();
		}

		@Override
		protected List<AccountInfo> doInBackground(Void... params) {
			return accountWrapper.getAll(true);
		}

		@Override
		protected void onPostExecute(List<AccountInfo> result) {
			if (isCancelled() || isCancelled) return;
			if (result.size() == 0) {
				new DialogManager((StorageFragment.this.getFragmentManager())).promptAdd(StorageFragment.this);
			} else {
				accounts = result;
				accountUpdated();
			}
			showContent();
		}
	}
}
