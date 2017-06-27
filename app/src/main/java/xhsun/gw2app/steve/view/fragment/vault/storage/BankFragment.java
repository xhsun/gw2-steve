package xhsun.gw2app.steve.view.fragment.vault.storage;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.util.ArrayList;
import java.util.List;

import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import timber.log.Timber;
import xhsun.gw2app.steve.R;
import xhsun.gw2app.steve.backend.data.model.AccountModel;
import xhsun.gw2app.steve.backend.util.items.vault.VaultHeader;
import xhsun.gw2app.steve.backend.util.items.vault.VaultItem;
import xhsun.gw2app.steve.backend.util.support.vault.VaultType;
import xhsun.gw2app.steve.backend.util.support.vault.storage.StorageTabFragment;

/**
 * A simple {@link Fragment} subclass.
 *
 * @author xhsun
 * @since 2017-05-03
 */
public class BankFragment extends StorageTabFragment {

	public BankFragment() {
		super(VaultType.BANK);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.item_recyclerview, container, false);
		setRetainInstance(true);

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
	public void onUpdateEmptyView(int size) {
		if (adapter == null || content == null) return;
//		adapter.expandAll();
		List<AbstractFlexibleItem> current = adapter.getCurrentItems();
		//noinspection unchecked
		Stream.of(content).filter(current::contains)
				.forEach(r -> expandIfPossible(current, r, new ArrayList<>(((VaultHeader) r).getSubItems())));
	}

	@Override
	@SuppressWarnings("unchecked")
	protected VaultHeader<AccountModel, VaultItem> generateHeader(AccountModel account) {
		VaultHeader<AccountModel, VaultItem> result = new VaultHeader<>(account);
		if (account.getBank().size() == 0) return result;

		if (content.contains(result)) result = (VaultHeader) content.get(content.indexOf(result));
		else addToContent(result);

		result.setSubItems(Stream.of(account.getBank()).map(r -> new VaultItem(r, this)).collect(Collectors.toList()));

		return result;
	}
}
