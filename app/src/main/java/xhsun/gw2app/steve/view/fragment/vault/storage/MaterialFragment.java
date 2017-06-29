package xhsun.gw2app.steve.view.fragment.vault.storage;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import timber.log.Timber;
import xhsun.gw2app.steve.R;
import xhsun.gw2app.steve.backend.data.model.AccountModel;
import xhsun.gw2app.steve.backend.data.model.vault.MaterialStorageModel;
import xhsun.gw2app.steve.backend.util.items.vault.VaultHeader;
import xhsun.gw2app.steve.backend.util.items.vault.VaultItem;
import xhsun.gw2app.steve.backend.util.items.vault.VaultSubHeader;
import xhsun.gw2app.steve.backend.util.support.vault.VaultType;
import xhsun.gw2app.steve.backend.util.support.vault.storage.StorageTabFragment;

/**
 * A simple {@link Fragment} subclass.
 *
 * @author xhsun
 * @since 2017-05-16
 */

public class MaterialFragment extends StorageTabFragment {

	public MaterialFragment() {
		super(VaultType.MATERIAL);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.item_recyclerview, container, false);
		setRetainInstance(true);

		progressBar = (ProgressBar) view.findViewById(R.id.item_progress);

		recyclerView = (RecyclerView) view.findViewById(R.id.item_recyclerview);
		setupRecyclerView(view);

		refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.item_refreshlayout);
		setupRefreshLayout();

		hide();
		onDataUpdate();
		Timber.i("Initialization complete");
		return view;
	}

	@Override
	@SuppressWarnings("unchecked")
	protected VaultHeader<AccountModel, VaultSubHeader<MaterialStorageModel>> generateHeader(AccountModel account) {
		List<VaultSubHeader<MaterialStorageModel>> storage = new ArrayList<>();
		VaultHeader<AccountModel, VaultSubHeader<MaterialStorageModel>> result = new VaultHeader<>(account);

		if (content.contains(result)) result = (VaultHeader) content.get(content.indexOf(result));
		else addToContent(result);

		if (account.getMaterial().size() == 0) {
			result.setSubItems(null);
			return result;
		}

		for (MaterialStorageModel m : account.getMaterial()) {
			if (m.getItems().size() == 0) continue;//nothing to show
			m.setApi(account.getAPI());//give unique identifier to each category, so that equals don't freak out

			VaultSubHeader<MaterialStorageModel> item = new VaultSubHeader<>(m);
			storage.add(item);

			//add all items that is not in the list
			item.setSubItems(Stream.of(m.getItems()).map(i -> new VaultItem(i, this)).collect(Collectors.toList()));
		}
		Collections.sort(storage);
		result.setSubItems(storage);
		return result;
	}
}
