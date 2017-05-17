package xhsun.gw2app.steve.view.fragment;


import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import timber.log.Timber;
import xhsun.gw2app.steve.R;
import xhsun.gw2app.steve.backend.data.AbstractData;
import xhsun.gw2app.steve.backend.data.AccountInfo;
import xhsun.gw2app.steve.backend.data.CharacterInfo;
import xhsun.gw2app.steve.backend.data.StorageInfo;
import xhsun.gw2app.steve.backend.util.AddAccountListener;
import xhsun.gw2app.steve.backend.util.dialog.select.selectCharacter.SelectCharAccountHolder;
import xhsun.gw2app.steve.backend.util.inventory.RefreshAccountsTask;
import xhsun.gw2app.steve.backend.util.inventory.RetrieveAccountsTask;
import xhsun.gw2app.steve.backend.util.inventory.RetrieveInventoryTask;
import xhsun.gw2app.steve.backend.util.items.BasicItem;
import xhsun.gw2app.steve.backend.util.items.QueryTextListener;
import xhsun.gw2app.steve.backend.util.vault.AbstractContentFragment;
import xhsun.gw2app.steve.backend.util.vault.OnPreferenceChangeListener;
import xhsun.gw2app.steve.backend.util.vault.VaultHeader;
import xhsun.gw2app.steve.backend.util.vault.VaultSubHeader;
import xhsun.gw2app.steve.backend.util.vault.VaultType;
import xhsun.gw2app.steve.view.dialog.DialogManager;

import static android.content.Context.MODE_PRIVATE;

/**
 * InventoryFragment is a subclass of {@link Fragment}<br/>
 * TODO sometimes list aren't updating when add item from checkbox
 * @author xhsun
 * @since 2017-03-28
 */
public class InventoryFragment extends AbstractContentFragment<AccountInfo>
		implements AddAccountListener, OnPreferenceChangeListener<SelectCharAccountHolder> {
	private static final String PREFERENCE_NAME = "inventoryDisplay";
	private SharedPreferences preferences;
	private AccountInfo current;
	private List<AbstractFlexibleItem> refreshedContent;

	private SearchView search;
	@BindView(R.id.inventory_account_list)
	RecyclerView accountList;
	@BindView(R.id.inventory_refresh)
	SwipeRefreshLayout refresh;
	@BindView(R.id.inventory_fab)
	FloatingActionButton fab;
	@BindView(R.id.inventory_progress)
	ProgressBar progress;

	public InventoryFragment() {
		super(VaultType.INVENTORY);

		refreshedContent = null;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_inventory, container, false);
		ButterKnife.bind(this, view);

		Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
		toolbar.setTitle("Inventory");
		setHasOptionsMenu(true);

		//load shared preference
		preferences = getActivity().getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);

		accountList.setLayoutManager(createGridLayoutManager(view));
		accountList.setAdapter(adapter);
		accountList.setHasFixedSize(true);
		accountList.setItemAnimator(new DefaultItemAnimator());

		refresh.setDistanceToTriggerSync(390);
		refresh.setColorSchemeResources(R.color.colorAccent);
		refresh.setEnabled(false);
		refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				InventoryFragment.this.onRefresh();
			}
		});

		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new DialogManager(getFragmentManager())
						.selectCharacters(InventoryFragment.this, items, getAllPreferences());
			}
		});
		//for hide fab on scroll down and show on scroll up
		accountList.addOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				if (dy > 0 && fab.getVisibility() == View.VISIBLE) fab.hide();
				else if (dy < 0 && fab.getVisibility() != View.VISIBLE &&
						(adapter != null && !adapter.hasSearchText()))
					fab.show();
			}
		});

		//getting all account info
		RetrieveAccountsTask task = new RetrieveAccountsTask(this);
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
	public void startEndless() {
		try {//calculate rough estimate of row size
			rows = (int) Math.floor(accountList.getHeight() / (accountList.getWidth() / columns));
		} catch (ArithmeticException ignored) {
		}

		//init endless
		remaining = new ArrayDeque<>(items);
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
		Timber.d("No more to load");
		//check if should show content
		if (!isShowing() && (isRecyclerScrollable() || !shouldLoad())) show();
		//don't reset if there is search text
		if (adapter.hasSearchText()) return;
		//reset
		if (adapter.contains(load)) adapter.removeScrollableFooter(load);
	}

	@Override
	public void loadNextData() {
		VaultHeader<AccountInfo, VaultSubHeader> header = generateContent();
		if (header == null) adapter.onLoadMoreComplete(null, 200);
		else if (header.getSubItemsCount() > 0)
			displayNewAccount(header);
	}

	@Override
	public void updateData(AbstractData data) {
		VaultHeader<AccountInfo, VaultSubHeader> header;
		Set<String> prefer = getPreference(current.getAPI());

		if ((header = generateHeader((AccountInfo) data, prefer)) == null) {
			adapter.onLoadMoreComplete(null, 200);
			return;//welp... something is really wrong
		}

		if (!adapter.contains(header)) displayNewAccount(header);
		else displayNewCharacter(header);
	}

	@Override
	public boolean shouldLoad() {
		List<AbstractFlexibleItem> current = adapter.getCurrentItems();
		HashMap<AccountInfo, Set<String>> prefers = getAllPreferences();
		Set<AccountInfo> accounts = getAllValidAccount(prefers);

		for (AccountInfo a : accounts) {
			VaultHeader<AccountInfo, VaultSubHeader> temp = new VaultHeader<>(a);
			if (!current.contains(temp)) return true;
			//noinspection unchecked
			temp = (VaultHeader) current.get(current.indexOf(temp));
			if (temp.getSubItemsCount() < a.getAllCharacterNames().size() - prefers.get(a).size())
				return true;
		}
		return false;
	}

	@Override
	public void filter(String query) {
		if (adapter.hasNewSearchText(query)) {
			adapter.setSearchText(query);
			adapter.filterItems(new ArrayList<>(content), 200);
		}
		// Disable SwipeRefresh and FAB if search is active!!
		if (adapter.hasSearchText()) {
			refresh.setEnabled(false);
			fab.hide();
		} else {
			refresh.setEnabled(true);
			fab.show();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void refreshData(AbstractData data) {
		if (refreshedContent == null) return;
		CharacterInfo character = (CharacterInfo) data;
		int accountIndex = items.indexOf(new AccountInfo(character.getApi()));
		AccountInfo account = items.get(accountIndex);
		//get account
		VaultHeader<AccountInfo, VaultSubHeader> accHeader = new VaultHeader<>(account);
		if (!refreshedContent.contains(accHeader)) {
			if (accountIndex < refreshedContent.size()) refreshedContent.add(accountIndex, accHeader);
			else refreshedContent.add(accHeader);
		} else accHeader = (VaultHeader) refreshedContent.get(refreshedContent.indexOf(accHeader));

		VaultSubHeader<CharacterInfo> charHeader = generateSubHeader(character);
		accHeader.addSubItem(account.getAllCharacterNames().indexOf(character.getName()), charHeader);

		if (isAllRefreshed()) {
			content = refreshedContent;
			refreshedContent = null;
			for (AbstractFlexibleItem h : content)
				Collections.sort(((VaultHeader) h).getSubItems());
			adapter.updateDataSet(content, true);
			refresh.post(new Runnable() {
				@Override
				public void run() {
					search.setInputType(InputType.TYPE_TEXT_VARIATION_FILTER);
					refresh.setRefreshing(false);
					fab.show();
				}
			});
		}
	}

	@Override
	public void addAccountCallback(AccountInfo account) {
		super.cancelAllTask();//stop all other tasks
		//start getting basic info for new account
		new RetrieveAccountsTask(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	@Override
	public void notifyPreferenceChange(VaultType type, Set<SelectCharAccountHolder> result) {
		Set<AccountInfo> preference = new HashSet<>();
		for (SelectCharAccountHolder r : result) {
			List<String> names = r.getShouldHideCharacters();
			AccountInfo temp = new AccountInfo(r.getApi());
			temp.setAllCharacterNames(names);
			preference.add(temp);
		}
		processChange(preference);
	}

	@Override
	public void processChange(Set<AccountInfo> preference) {
		super.cancelAllTask();
		boolean shouldLoad = false;
		for (AccountInfo a : preference) {
			int index, size;
			if ((index = items.indexOf(a)) < 0) continue;
			AccountInfo info = items.get(index);
			//update preference first
			setPreference(a.getAPI(), a.getAllCharacterNames());
			//then update view
			size = info.getAllCharacterNames().size() - a.getAllCharacterNames().size();
			if ((index = adapter.getGlobalPositionOf(new VaultHeader<AccountInfo, VaultSubHeader>(info))) < 0) {
				if (size > 0) {
					info.setSearched(false);
					shouldLoad = true;
				}
				continue;//nothing needed to be updated
			}
			//noinspection unchecked
			VaultHeader<AccountInfo, VaultSubHeader> header = (VaultHeader) adapter.getItem(index);
			if (size <= 0) {
				content.remove(content.indexOf(header));
				adapter.removeItem(index);
				continue;
			}
			for (CharacterInfo c : info.getAllCharacters()) {
				VaultSubHeader<CharacterInfo> temp = new VaultSubHeader<>(c);
				if (a.getAllCharacterNames().contains(c.getName())) {
					header.removeSubItem(temp);
					if ((index = adapter.getGlobalPositionOf(temp)) >= 0)
						adapter.removeItem(index);
				} else if (!header.containsSubItem(temp)) {
					info.setSearched(false);
					shouldLoad = true;
				}
			}
		}
		if (shouldLoad) loadNextData();
	}

	@Override
	public Set<String> getPreference(String key) {
		Set<String> result = preferences.getStringSet(key, null);
		return (result == null) ? new HashSet<String>() : result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onUpdateEmptyView(int size) {
		if (adapter == null || content == null) return;

		List<AbstractFlexibleItem> current = adapter.getCurrentItems();

		for (AbstractFlexibleItem h : content) {
			if (!current.contains(h)) continue;
			VaultHeader<AccountInfo, VaultSubHeader> header = (VaultHeader) h;
			expandIfPossible(current, h, new ArrayList<AbstractFlexibleItem>(header.getSubItems()));

			for (VaultSubHeader<CharacterInfo> s : header.getSubItems())
				expandIfPossible(current, s, new ArrayList<AbstractFlexibleItem>(s.getSubItems()));
		}
	}

	@Override
	//show list and hide progress
	public void show() {
		accountList.setVisibility(View.VISIBLE);
		fab.setVisibility(View.VISIBLE);
		refresh.setVisibility(View.VISIBLE);
		progress.setVisibility(View.GONE);
		refresh.setRefreshing(false);
		refresh.setEnabled(true);
	}

	@Override
	//hide everything except progress
	public void hide() {
		accountList.setVisibility(View.INVISIBLE);
		fab.setVisibility(View.GONE);
		refresh.setVisibility(View.INVISIBLE);
		refresh.setRefreshing(false);
		progress.setVisibility(View.VISIBLE);
	}

	@Override
	public void stopRefresh() {
		refresh.post(new Runnable() {
			@Override
			public void run() {
				refresh.setRefreshing(false);
			}
		});
	}

	@Override
	protected boolean isShowing() {
		return accountList.getVisibility() == View.VISIBLE && refresh.getVisibility() == View.VISIBLE;
	}

	//generate inventory content to display
	private VaultHeader<AccountInfo, VaultSubHeader> generateContent() {
		Set<String> prefer;
		VaultHeader<AccountInfo, VaultSubHeader> header;

		//check if need to load new current account
		if (current == null || current.isSearched()) {
			//get next account to load
			current = remaining.pollFirst();
			if (current == null) {
				if (checkAvailability()) current = remaining.pollFirst();
				else return null;
			}
		}

		//check if we need to load from server
		prefer = getPreference(current.getAPI());
		//check the generated header
		if ((header = generateHeader(current, prefer)) == null) {
			current.setSearched(true);
			loadNextData();//directly load next account
		} else if (header.getSubItemsCount() < getActualCharSize(header.getData(), prefer)) {
			//more to load, start loading more
			new RetrieveInventoryTask(this, current).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else {//no more to load for this account
			current.setSearched(true);
			return header;
		}
		return new VaultHeader<>(current);
	}

	//generate header for the given account, that header will be added to content list here
	@SuppressWarnings("unchecked")
	private VaultHeader<AccountInfo, VaultSubHeader> generateHeader(AccountInfo account, Set<String> prefer) {
		if (getActualCharSize(account, prefer) <= 0) return null;//nothing to populate

		List<CharacterInfo> chars = account.getAllCharacters();
		VaultHeader<AccountInfo, VaultSubHeader> result = new VaultHeader<>(account);
		if (content.contains(result)) result = (VaultHeader) content.get(content.indexOf(result));
		else content.add(result);

		for (String c : account.getAllCharacterNames()) {
			if (prefer.contains(c)) continue;//shouldn't show

			//get old inventory info for this char if possible
			CharacterInfo character = new CharacterInfo(account.getAPI(), c);
			if (chars.contains(character)) character = chars.get(chars.indexOf(character));
			else account.getAllCharacters().add(character);
			if (character.getInventory().size() == 0) continue;//nothing to show

			//get old subHeader for this char if possible
			VaultSubHeader<CharacterInfo> item = new VaultSubHeader<>(character);
			if (result.containsSubItem(item))
				item = result.getSubItems().get(result.getSubItems().indexOf(item));
			else result.addSubItem(item);

			//add all items that is not in the list
			for (StorageInfo s : character.getInventory()) {
				BasicItem i = new BasicItem(s, this);
				if (!item.containsSubItem(i)) item.addSubItem(i);
			}
		}
		Collections.sort(result.getSubItems());
		return result;
	}

	//generate sub header for the given character info
	//this sub header will not be added to content list
	private VaultSubHeader<CharacterInfo> generateSubHeader(CharacterInfo character) {
		VaultSubHeader<CharacterInfo> charHeader = new VaultSubHeader<>(character);
		for (StorageInfo s : character.getInventory()) charHeader.addSubItem(new BasicItem(s, this));
		return charHeader;
	}

	//display account that haven't shown before
	private void displayNewAccount(VaultHeader<AccountInfo, VaultSubHeader> header) {
		if (adapter.contains(header)) adapter.updateDataSet(content, true);
		else adapter.addItem(adapter.getGlobalPositionOf(load), header);
		onUpdateEmptyView(0);

		adapter.onLoadMoreComplete(null, 200);
	}

	//display newly added character for given account
	@SuppressWarnings("unchecked")
	private void displayNewCharacter(VaultHeader<AccountInfo, VaultSubHeader> header) {
		adapter.updateDataSet(content, true);
		onUpdateEmptyView(0);
		//set this item as searched, if enough is loaded
		if (header.getSubItemsCount() >= getActualCharSize(header.getData()))
			items.get(items.indexOf(header.getData())).setSearched(true);
		//finishing up
		adapter.onLoadMoreComplete(null, 200);
	}

	//check if there is more account to load or not
	private boolean checkAvailability() {
		for (AccountInfo a : items) {
			if (!a.isSearched()
					&& !remaining.contains(a)
					&& getActualCharSize(a, getPreference(a.getAPI())) > 0)
				remaining.add(a);
		}

		return remaining.size() != 0;
	}

	//return all account that should be showing
	private Set<AccountInfo> getAllValidAccount(HashMap<AccountInfo, Set<String>> prefers) {
		Set<AccountInfo> result = new HashSet<>();
		for (AccountInfo a : items) {
			if ((!prefers.containsKey(a) && a.getAllCharacterNames().size() > 0) ||
					(getActualCharSize(a, prefers.get(a)) > 0))
				result.add(a);
		}
		return result;
	}

	//get number of chars this account should be showing
	private int getActualCharSize(AccountInfo account) {
		return getActualCharSize(account, getPreference(account.getAPI()));
	}

	//get number of chars this account should be showing
	private int getActualCharSize(AccountInfo account, Set<String> prefer) {
		return account.getAllCharacterNames().size() - prefer.size();
	}

	//false if nothing got changed
	private boolean setPreference(String api, List<String> characters) {
		Timber.i("Set preference for %s to %s", api, characters);
		Set<String> names = new HashSet<>(characters);
		Set<String> result = preferences.getStringSet(api, null);
		if (result == null) result = new HashSet<>();
		if (result.equals(names)) return false;
		SharedPreferences.Editor editor;
		editor = preferences.edit();
		editor.putStringSet(api, names);
		editor.apply();
		return true;
	}

	//get all preference that is currently available
	private HashMap<AccountInfo, Set<String>> getAllPreferences() {
		HashMap<AccountInfo, Set<String>> result = new HashMap<>();
		for (AccountInfo a : items)
			result.put(a, getPreference(a.getAPI()));
		return result;
	}

	//expand given item if current list in adapter contain this item, but doesn't contain it's sub items
	private void expandIfPossible(List<AbstractFlexibleItem> current,
	                              AbstractFlexibleItem item, List<AbstractFlexibleItem> child) {
		if (current.contains(item) && !isExpanded(current, child)) adapter.expand(item, true);
	}

	//reload all inventory info
	private void onRefresh() {
		super.cancelAllTask();
		refresh.post(new Runnable() {
			@Override
			public void run() {
				search.clearFocus();
				search.setIconified(true);
				search.setInputType(InputType.TYPE_NULL);
				fab.hide();
				refresh.setRefreshing(true);
			}
		});
		refreshedContent = new ArrayList<>();
		new RefreshAccountsTask(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	//check if everything were refreshed
	@SuppressWarnings("unchecked")
	private boolean isAllRefreshed() {
		for (AccountInfo a : items) {
			VaultHeader<AccountInfo, VaultSubHeader> newHeader = new VaultHeader<>(a);
			if (!refreshedContent.contains(newHeader)) return false;
			newHeader = (VaultHeader) refreshedContent.get(refreshedContent.indexOf(newHeader));

			if (newHeader.getSubItemsCount() != getActualCharSize(a)) return false;
		}
		return true;
	}

	//check if given child is present in the adapter
	//if one of the child does, then the implied parent probably is expanded
	private boolean isExpanded(List<AbstractFlexibleItem> current, List<AbstractFlexibleItem> child) {
		for (AbstractFlexibleItem c : child) {
			boolean contains = current.contains(c);
			if (contains) return true;
		}
		return false;
	}

	//setup search with with search hint and listener
	private void setupSearchView(Menu menu) {
		search = (SearchView) menu.findItem(R.id.toolbar_search).getActionView();
		search.setInputType(InputType.TYPE_TEXT_VARIATION_FILTER);
		search.setQueryHint("Search Inventory");
		search.setOnQueryTextListener(new QueryTextListener(this));
		search.setIconified(true);
		Timber.i("SearchView setup finished");
	}
}
