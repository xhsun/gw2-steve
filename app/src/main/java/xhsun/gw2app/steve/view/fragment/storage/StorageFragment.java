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
import android.text.InputType;
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
import xhsun.gw2app.steve.backend.data.AccountInfo;
import xhsun.gw2app.steve.backend.database.account.AccountWrapper;
import xhsun.gw2app.steve.backend.database.storage.BankWrapper;
import xhsun.gw2app.steve.backend.database.storage.MaterialWrapper;
import xhsun.gw2app.steve.backend.database.storage.WardrobeWrapper;
import xhsun.gw2app.steve.backend.util.AddAccountListener;
import xhsun.gw2app.steve.backend.util.CancellableAsyncTask;
import xhsun.gw2app.steve.backend.util.dialog.DialogManager;
import xhsun.gw2app.steve.backend.util.dialog.selectAccount.AccountHolder;
import xhsun.gw2app.steve.backend.util.items.QueryTextListener;
import xhsun.gw2app.steve.backend.util.storage.StoragePagerAdapter;
import xhsun.gw2app.steve.backend.util.storage.StorageTabFragment;
import xhsun.gw2app.steve.backend.util.storage.StorageTabHelper;
import xhsun.gw2app.steve.backend.util.vault.AbstractContentFragment;
import xhsun.gw2app.steve.backend.util.vault.OnPreferenceChangeListener;
import xhsun.gw2app.steve.backend.util.vault.VaultType;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 *
 * @author xhsun
 * @since 2017-05-01
 */
public class StorageFragment extends Fragment implements OnPreferenceChangeListener<AccountHolder>,
		AddAccountListener, StorageTabHelper {
	private static final String PREFERENCE_NAME = "storageDisplay";
	private SharedPreferences preferences;
	private List<AccountInfo> accounts;
	private List<StorageTabFragment> tabs;
	private InitializeAccounts task;

	@Inject
	AccountWrapper accountWrapper;
	@Inject
	BankWrapper bankWrapper;
	@Inject
	MaterialWrapper materialWrapper;
	@Inject
	WardrobeWrapper wardrobeWrapper;

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
		//TODO set on tab change and clear search when search

		//TODO fab

		task = new InitializeAccounts();
		task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
		task = new InitializeAccounts();
		task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	@Override
	public void notifyPreferenceChange(VaultType type, Set<AccountHolder> result) {
		Set<String> pref = new HashSet<>();
		Set<AccountInfo> preference = new HashSet<>();
		for (AccountHolder r : result) {
			AccountInfo temp = new AccountInfo(r.getApi());
			preference.add(temp);
			pref.add(temp.getAPI());
		}
		setPreference(type.name(), pref);
		for (StorageTabFragment fragment : tabs) {
			if (fragment.getType() == type)
				fragment.processChange(preference);
		}
	}

	//update preference in file
	private boolean setPreference(String type, Set<String> value) {
		Timber.i("Set preference for %s to %s", type, value);
		Set<String> temp = preferences.getStringSet(type, null);
		if (temp == null) temp = new HashSet<>();
		if (temp.equals(value)) return false;
		SharedPreferences.Editor editor;
		editor = preferences.edit();
		editor.putStringSet(type, value);
		editor.apply();
		return true;
	}

	@Override
	public Set<String> getPreference(VaultType type) {
		Set<String> result = preferences.getStringSet(type.name(), null);
		return (result == null) ? new HashSet<String>() : result;
	}

	@Override
	public List<AccountInfo> getData() {
		return accounts;
	}

	@Override
	public FloatingActionButton getFAB() {
		return fab;
	}

	@Override
	public ViewPager getViewPager() {
		return viewPager;
	}

	@Override
	public ProgressBar getProgressBar() {
		return progressBar;
	}

	@Override
	public SearchView getSearchView() {
		return search;
	}

	//show content
	private void showContent() {
		progressBar.setVisibility(View.GONE);
		fab.setVisibility(View.VISIBLE);
		viewPager.setVisibility(View.VISIBLE);
	}

	//hide content
	private void hideContent() {
		fab.setVisibility(View.GONE);
		viewPager.setVisibility(View.GONE);
		progressBar.setVisibility(View.VISIBLE);
	}

	private void setupTabFragments() {
		StorageTabFragment fragment;
		tabs = new ArrayList<>();
		fragment = new BankFragment();
		fragment.setHelper(this);
		tabs.add(fragment);
		//TODO other fragments
//		fragment = new BankFragment();
//		fragment.setHelper(this);
//		tabs.add(fragment);
//		fragment = new BankFragment();
//		fragment.setHelper(this);
//		tabs.add(fragment);
	}

	//setup search with with search hint and listener
	private void setupSearchView(Menu menu) {
		search = (SearchView) menu.findItem(R.id.toolbar_search).getActionView();
		search.setInputType(InputType.TYPE_TEXT_VARIATION_FILTER);
		search.setIconified(true);
		search.setQueryHint("Search Storage");
		//TODO might need custom text listener
		Set<AbstractContentFragment> fragments = new HashSet<>();
		for (StorageTabFragment f : tabs) fragments.add(f);
		search.setOnQueryTextListener(new QueryTextListener(fragments));
		Timber.i("SearchView setup finished");
	}

	//cancel task
	private void cancelTask() {
		if (task != null && task.getStatus() != AsyncTask.Status.FINISHED)
			task.cancel(true);
	}

	//get all account info
	private class InitializeAccounts extends CancellableAsyncTask<Void, Void, List<AccountInfo>> {

		@Override
		protected void onCancelled() {
			Timber.i("Initialize account info cancelled");
			accountWrapper.setCancelled(true);
			bankWrapper.setCancelled(true);
			materialWrapper.setCancelled(true);
			wardrobeWrapper.setCancelled(true);
			showContent();
		}

		@Override
		protected void onPreExecute() {
			accountWrapper.setCancelled(false);
			bankWrapper.setCancelled(false);
			materialWrapper.setCancelled(false);
			wardrobeWrapper.setCancelled(false);
			hideContent();
		}

		@Override
		protected List<AccountInfo> doInBackground(Void... params) {
			Timber.i("Start initialize account infos");
			List<AccountInfo> info = accountWrapper.getAll(true);
			List<AccountInfo> banks = bankWrapper.getAll();
			List<AccountInfo> materials = materialWrapper.getAll();
			List<AccountInfo> wardrobes = wardrobeWrapper.getAll();
			Set<String> preferBank = getPreference(VaultType.BANK);
			Set<String> preferMaterial = getPreference(VaultType.MATERIAL);
			Set<String> preferWardrobe = getPreference(VaultType.WARDROBE);
			for (AccountInfo a : info) {
				String api = a.getAPI();
				if (banks.contains(a) && !preferBank.contains(api))
					a.setBank(banks.get(banks.indexOf(a)).getBank());

				if (materials.contains(a) && !preferMaterial.contains(api))
					a.setMaterial(materials.get(materials.indexOf(a)).getMaterial());

				if (wardrobes.contains(a) && !preferWardrobe.contains(api))
					a.setWardrobe(wardrobes.get(wardrobes.indexOf(a)).getWardrobe());
			}
			return info;
		}

		@Override
		protected void onPostExecute(List<AccountInfo> result) {
			Timber.i("initialized all account info");
			if (isCancelled() || isCancelled) return;
			if (result.size() == 0) {
				new DialogManager((StorageFragment.this.getFragmentManager())).promptAdd(StorageFragment.this);
				return;
			}

			accounts = result;
			showContent();
			//notify current tab
			tabs.get(tabLayout.getSelectedTabPosition()).onDataUpdate();
		}
	}
}
