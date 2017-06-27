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

import timber.log.Timber;
import xhsun.gw2app.steve.R;
import xhsun.gw2app.steve.backend.data.model.AccountModel;
import xhsun.gw2app.steve.backend.data.model.vault.BankSectionModel;
import xhsun.gw2app.steve.backend.data.model.vault.item.BankItemModel;
import xhsun.gw2app.steve.backend.util.items.vault.VaultHeader;
import xhsun.gw2app.steve.backend.util.items.vault.VaultItem;
import xhsun.gw2app.steve.backend.util.items.vault.VaultSubHeader;
import xhsun.gw2app.steve.backend.util.support.vault.VaultType;
import xhsun.gw2app.steve.backend.util.support.vault.storage.StorageTabFragment;

/**
 * A simple {@link Fragment} subclass.
 *
 * @author xhsun
 * @since 2017-05-03
 */
public class BankFragment extends StorageTabFragment {
	private static final int SIZE = 30;

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
	@SuppressWarnings("unchecked")
	protected VaultHeader<AccountModel, VaultSubHeader<BankSectionModel>> generateHeader(AccountModel account) {
		VaultHeader<AccountModel, VaultSubHeader<BankSectionModel>> result = new VaultHeader<>(account);
		if (account.getBank().size() == 0) return result;

		if (content.contains(result)) result = (VaultHeader) content.get(content.indexOf(result));
		else addToContent(result);

		List<BankItemModel> bank = account.getBank();
		List<VaultSubHeader<BankSectionModel>> sections = new ArrayList<>();
		int idealColumn = SIZE / columns, size = idealColumn * columns, count = bank.size() / size;
		for (int i = 0; i <= count; i++) {
			BankSectionModel section = new BankSectionModel(account.getAPI(), i);
			VaultSubHeader<BankSectionModel> header = new VaultSubHeader<>(section);
			List<BankItemModel> partition = bank.subList(i * size, (i == count) ? bank.size() : (i + 1) * size);

			header.setSubItems(Stream.of(partition).map(b -> new VaultItem(b, this)).collect(Collectors.toList()));
			section.setItems(partition);
			sections.add(header);
		}

		result.setSubItems(sections);
		return result;
	}
}
