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
import android.widget.Toast;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.roughike.bottombar.BottomBar;

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
 * TODO nested tab layout
 *
 * @author xhsun
 * @since 2017-06-17
 */
public class WardrobeFragment extends StorageTabFragment {
	private AccountModel current;
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

		bottomBar.setOnTabSelectListener(tabId -> {
			switch (tabId) {
				case R.id.tab_armor:
					Timber.i("Armor");
					Toast.makeText(getContext(), "Armor", Toast.LENGTH_LONG).show();
					break;
				case R.id.tab_backpack:
					Timber.i("back");
					Toast.makeText(getContext(), "back", Toast.LENGTH_LONG).show();
					break;
				case R.id.tab_mini:
					Timber.i("mini");
					Toast.makeText(getContext(), "mini", Toast.LENGTH_LONG).show();
					break;
				case R.id.tab_misc:
					Toast.makeText(getContext(), "misc", Toast.LENGTH_LONG).show();
					break;
				case R.id.tab_outfit:
					Toast.makeText(getContext(), "outfit", Toast.LENGTH_LONG).show();
					break;
				case R.id.tab_weapon:
					Toast.makeText(getContext(), "weapon", Toast.LENGTH_LONG).show();
					break;
			}
		});

		bottomBar.setTabSelectionInterceptor((oldTabId, newTabId) -> getProgressBar().getVisibility() == View.VISIBLE);

		hide();
		onDataUpdate();
		Timber.i("Initialization complete");
		return view;
	}

	@Override
	public boolean shouldLoad() {
		List<AbstractFlexibleItem> current = adapter.getCurrentItems();
		Set<String> prefer = getPreference();

		Stream.of(items)
				.filter(a -> !prefer.contains(a.getAPI()) &&
						(!current.contains(new VaultHeader<>(a)) || !a.getSearched().contains(tabToSelectableType())))
				.forEach(r -> r.setSearched(false));
		return Stream.of(items).anyMatch(a -> !a.isSearched());
	}

	protected VaultHeader generateContent() {
		VaultHeader header;

		if (current == null || current.getSearched().contains(tabToSelectableType())) {
			//get next account to load
			current = getRemaining();
			if (current == null) {
				if (checkAvailability()) current = getRemaining();
				else return null;
			}
		}

//		check the generated header
		if ((header = generateHeader(current)).getSubItemsCount() == 0) {
			new UpdateVaultTask<WardrobeItemModel>(this, tabToSelectableType(), current).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else {
			current.getSearched().add(tabToSelectableType());
			if (current.getSearched().size() == WardrobeWrapper.SelectableType.values().length)
				current.setSearched(true);
		}
		return header;
	}

	protected boolean checkAvailability() {
		Set<String> pref = getPreference();
		Stream.of(items).filter(a -> !a.isSearched() &&
				!a.getSearched().contains(tabToSelectableType()) &&
				!containRemaining(a) && !pref.contains(a.getAPI()))
				.forEach(this::addRemaining);

		return !isRemainingEmpty();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected VaultHeader generateHeader(AccountModel account) {
		WardrobeModel.WardrobeType type = tabToWardrobeType();
		List<VaultSubHeader<WardrobeSubModel>> storage = new ArrayList<>();
		VaultHeader<AccountModel, VaultSubHeader<WardrobeSubModel>> result = new VaultHeader<>(account);
		List<WardrobeModel> temp = Stream.of(account.getWardrobe()).filter(w -> w.getType() == type).collect(Collectors.toList());
		if (account.getWardrobe().size() == 0 || temp.size() == 0) return result;

		if (content.contains(result)) result = (VaultHeader) content.get(content.indexOf(result));
		else addToContent(result);

		if (type == WardrobeModel.WardrobeType.Backpack ||
				type == WardrobeModel.WardrobeType.Mini ||
				type == WardrobeModel.WardrobeType.Outfit)
			result.setSubItems(parseSingle(account.getAPI(), temp.get(0)));
		else result.setSubItems(parseMulti(temp.get(0)));

		return result;
	}

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

	private List<VaultSubHeader<WardrobeSubModel>> parseMulti(WardrobeModel wardrobe) {
		List<VaultSubHeader<WardrobeSubModel>> result = new ArrayList<>();
		for (WardrobeSubModel m : wardrobe.getData()) {
			if (m.getItems().size() == 0) continue;//nothing to show

			VaultSubHeader<WardrobeSubModel> item = new VaultSubHeader<>(m);
			result.add(item);

			//add all items that is not in the list
			item.setSubItems(Stream.of(m.getItems()).map(i -> new VaultItem(i, this)).collect(Collectors.toList()));
		}

		if (tabToWardrobeType() == WardrobeModel.WardrobeType.Armor)
			Collections.sort(result, (o1, o2) -> Double.compare(o1.getData().getOrder(), o2.getData().getOrder()));
		else //noinspection unchecked
			Collections.sort(result);
		return result;
	}

	private WardrobeWrapper.SelectableType tabToSelectableType() {
		switch (bottomBar.getCurrentTabId()) {
			case R.id.tab_armor:
			case R.id.tab_weapon:
			case R.id.tab_backpack:
				return WardrobeWrapper.SelectableType.SKIN;
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
		switch (bottomBar.getCurrentTabId()) {
			case R.id.tab_armor:
				return WardrobeModel.WardrobeType.Armor;
			case R.id.tab_weapon:
				return WardrobeModel.WardrobeType.Weapon;
			case R.id.tab_backpack:
				return WardrobeModel.WardrobeType.Backpack;
			case R.id.tab_mini:
				return WardrobeModel.WardrobeType.Mini;
			case R.id.tab_misc:
				return WardrobeModel.WardrobeType.Misc;
			case R.id.tab_outfit:
				return WardrobeModel.WardrobeType.Outfit;
			default:
				return WardrobeModel.WardrobeType.Misc;
		}
	}
}
