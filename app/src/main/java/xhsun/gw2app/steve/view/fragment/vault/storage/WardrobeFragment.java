package xhsun.gw2app.steve.view.fragment.vault.storage;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.roughike.bottombar.BottomBar;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import timber.log.Timber;
import xhsun.gw2app.steve.R;
import xhsun.gw2app.steve.backend.data.model.AccountModel;
import xhsun.gw2app.steve.backend.data.model.vault.WardrobeModel;
import xhsun.gw2app.steve.backend.data.model.vault.WardrobeSubModel;
import xhsun.gw2app.steve.backend.data.model.vault.item.WardrobeItemModel;
import xhsun.gw2app.steve.backend.data.wrapper.storage.WardrobeWrapper;
import xhsun.gw2app.steve.backend.util.items.vault.VaultHeader;
import xhsun.gw2app.steve.backend.util.items.vault.VaultItem;
import xhsun.gw2app.steve.backend.util.items.vault.VaultSubHeader;
import xhsun.gw2app.steve.backend.util.support.vault.VaultType;
import xhsun.gw2app.steve.backend.util.support.vault.storage.StorageTabFragment;
import xhsun.gw2app.steve.backend.util.task.vault.UpdateVaultTask;

/**
 * A simple {@link Fragment} subclass.
 *
 * @author xhsun
 * @since 2017-06-17
 */
public class WardrobeFragment extends StorageTabFragment {
	private AccountModel current;
	private WardrobeWrapper.SelectableType currentSelectableType;
	private WardrobeModel.WardrobeType currentType;
	@BindView(R.id.wardrobe_bottomBar)
	BottomBar bottomBar;

	public WardrobeFragment() {
		super(VaultType.WARDROBE);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_wardrobe, container, false);
		setRetainInstance(true);
		ButterKnife.bind(this, view);

		recyclerView = (RecyclerView) view.findViewById(R.id.wardrobe_recyclerview);
		setupRecyclerView(view);

		refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.wardrobe_refreshlayout);
		setupRefreshLayout();

		progressBar = (ProgressBar) view.findViewById(R.id.wardrobe_progress);

		bottomBar.setOnTabSelectListener(tabId -> {
			currentType = tabToWardrobeType(tabId);
			currentSelectableType = tabToSelectableType(tabId);
			if (isLoadingRequired()) return;
			displayLoaded(tabToWardrobeType(tabId));
		});

		bottomBar.setTabSelectionInterceptor((oldTabId, newTabId) -> progressBar.getVisibility() == View.VISIBLE ||
				refreshLayout.isRefreshing());

		Timber.i("Initialization complete");
		return view;
	}

	@Override
	public boolean shouldLoad() {
		return shouldLoad(tabToSelectableType());
	}

	@Override
	protected void startRefreshing() {
		Set<String> pref = getPreference();
		Stream.of(items).filterNot(a -> pref.contains(a.getAPI()))
				.forEach(r -> new UpdateVaultTask<>(this, tabToSelectableType(), r, true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR));
	}

	protected VaultHeader generateContent() {
		VaultHeader<AccountModel, VaultSubHeader<WardrobeSubModel>> header;
		WardrobeWrapper.SelectableType tempSelect = (currentSelectableType == null) ? tabToSelectableType() : currentSelectableType;
		WardrobeModel.WardrobeType tempType = (currentType == null) ? tabToWardrobeType() : currentType;

		if (current == null || current.getSearched().contains(tempSelect)) {
			current = getRemaining();//get next account to load
			if (current == null) {
				if (checkAvailability(tempSelect)) current = getRemaining();
				else return null;
			}
		}

		//check the generated header
		if ((header = generateHeader(current, tempType)).getSubItemsCount() == 0 && !current.getSearched().contains(tempSelect)) {
			new UpdateVaultTask<WardrobeItemModel>(this, tempSelect, current).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			header.setSubItems(new ArrayList<>());
		} else {
			current.getSearched().add(tempSelect);
			current.setSearched(true);
		}
		return header;//subItems: empty -> loading; empty -> nothing to load
	}

	protected boolean checkAvailability(WardrobeWrapper.SelectableType type) {
		Set<String> pref = getPreference();
		Stream.of(items).filter(a -> (!a.getSearched().contains(type) ||
				!content.contains(new VaultHeader<>(a))) &&
				!containRemaining(a) && !pref.contains(a.getAPI()))
				.forEach(this::addRemaining);

		return !isRemainingEmpty();
	}

	@Override
	protected VaultHeader<AccountModel, VaultSubHeader<WardrobeSubModel>> generateHeader(AccountModel account) {
		return generateHeader(account, tabToWardrobeType());
	}

	@Override
	protected void displayAccount(VaultHeader header) {
		adapter.updateDataSet(content, true);
		adapter.onLoadMoreComplete(null, 200);
	}

	private boolean shouldLoad(WardrobeWrapper.SelectableType type) {
		Set<String> prefer = getPreference();
		return Stream.of(items).anyMatch(a -> !prefer.contains(a.getAPI()) &&
				(!a.getSearched().contains(type) || !content.contains(new VaultHeader<>(a)))) ||
				!isRemainingEmpty();
	}

	private boolean isLoadingRequired() {
		hide();
		resetEndless();
		if (items.size() == 0 || isNotAllSearched(currentSelectableType)) {
			onDataUpdate();
			return true;
		}
		return false;
	}

	private void displayLoaded(WardrobeModel.WardrobeType type) {
		Set<String> pref = getPreference();
		List<AbstractFlexibleItem> temp = new ArrayList<>();
		for (AccountModel a : items) {
			if (pref.contains(a.getAPI())) continue;
			temp.add(generateHeader(a, type));
		}
		content = temp;
		adapter.updateDataSet(content, true);
		onUpdateEmptyView(0);
		show();
	}

	private void resetEndless() {
		current = null;
		remaining = new ArrayDeque<>();
		adapter.clear();
	}

	private boolean isNotAllSearched(WardrobeWrapper.SelectableType type) {
		Set<String> prefer = getPreference();
		return Stream.of(items).anyMatch(a -> !prefer.contains(a.getAPI()) &&
				!a.getSearched().contains(type));
	}

	@SuppressWarnings("unchecked")
	private VaultHeader<AccountModel, VaultSubHeader<WardrobeSubModel>> generateHeader(AccountModel account, WardrobeModel.WardrobeType type) {
		VaultHeader<AccountModel, VaultSubHeader<WardrobeSubModel>> result = new VaultHeader<>(account);
		List<WardrobeModel> temp = Stream.of(account.getWardrobe()).filter(w -> w.getType() == type).collect(Collectors.toList());

		if (content.contains(result)) result = (VaultHeader) content.get(content.indexOf(result));
		else addToContent(result);

		if (account.getWardrobe().size() == 0 || temp.size() == 0) {
			result.setSubItems(null);
			return result;
		}

		if (type == WardrobeModel.WardrobeType.Backpack ||
				type == WardrobeModel.WardrobeType.Mini ||
				type == WardrobeModel.WardrobeType.Outfit)
			result.setSubItems(parseSingle(account.getAPI(), temp.get(0)));
		else result.setSubItems(parseMulti(temp.get(0), type));

		return result;
	}

	//create sub header to make it easier to see
	private List<VaultSubHeader<WardrobeSubModel>> parseSingle(String api, WardrobeModel single) {
		List<VaultSubHeader<WardrobeSubModel>> sections = new ArrayList<>();
		List<WardrobeItemModel> items = Stream.of(single.getData()).flatMap(d -> Stream.of(d.getItems())).collect(Collectors.toList());

		int idealColumn = SIZE / columns, size = idealColumn * columns, count = items.size() / size;
		for (int i = 0; i <= count; i++) {
			WardrobeSubModel section = new WardrobeSubModel(api, " ", i);
			VaultSubHeader<WardrobeSubModel> header = new VaultSubHeader<>(section);
			List<WardrobeItemModel> partition = items.subList(i * size, (i == count) ? items.size() : (i + 1) * size);

			header.setSubItems(Stream.of(partition).map(b -> new VaultItem(b, this)).collect(Collectors.toList()));
			section.setItems(partition);
			sections.add(header);
		}
		return sections;
	}

	//create sub header for wardrobe that naturally have a section
	private List<VaultSubHeader<WardrobeSubModel>> parseMulti(WardrobeModel wardrobe, WardrobeModel.WardrobeType type) {
		List<VaultSubHeader<WardrobeSubModel>> result = new ArrayList<>();
		for (WardrobeSubModel m : wardrobe.getData()) {
			if (m.getItems().size() == 0) continue;//nothing to show

			VaultSubHeader<WardrobeSubModel> item = new VaultSubHeader<>(m);
			result.add(item);

			//add all items that is not in the list
			item.setSubItems(Stream.of(m.getItems()).map(i -> new VaultItem(i, this)).collect(Collectors.toList()));
		}

		if (type == WardrobeModel.WardrobeType.Armor)
			Collections.sort(result, (o1, o2) -> Double.compare(o1.getData().getOrder(), o2.getData().getOrder()));
		else //noinspection unchecked
			Collections.sort(result);
		return result;
	}

	private WardrobeWrapper.SelectableType tabToSelectableType() {
		return tabToSelectableType(bottomBar.getCurrentTabId());
	}

	private WardrobeWrapper.SelectableType tabToSelectableType(int id) {
		switch (id) {
			case R.id.tab_mini:
				return WardrobeWrapper.SelectableType.MINI;
			case R.id.tab_misc:
				return WardrobeWrapper.SelectableType.MISC;
			case R.id.tab_outfit:
				return WardrobeWrapper.SelectableType.OUTFIT;
			default:
				return WardrobeWrapper.SelectableType.SKIN;
		}
	}

	private WardrobeModel.WardrobeType tabToWardrobeType() {
		return tabToWardrobeType(bottomBar.getCurrentTabId());
	}

	private WardrobeModel.WardrobeType tabToWardrobeType(int id) {
		switch (id) {
			case R.id.tab_armor:
				return WardrobeModel.WardrobeType.Armor;
			case R.id.tab_weapon:
				return WardrobeModel.WardrobeType.Weapon;
			case R.id.tab_backpack:
				return WardrobeModel.WardrobeType.Backpack;
			case R.id.tab_mini:
				return WardrobeModel.WardrobeType.Mini;
			case R.id.tab_outfit:
				return WardrobeModel.WardrobeType.Outfit;
			default:
				return WardrobeModel.WardrobeType.Misc;
		}
	}
}
