package xhsun.gw2app.steve.view.fragment.storage;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Set;

import timber.log.Timber;
import xhsun.gw2app.steve.R;
import xhsun.gw2app.steve.backend.data.AbstractData;
import xhsun.gw2app.steve.backend.data.AccountData;
import xhsun.gw2app.steve.backend.util.storage.StorageTabFragment;
import xhsun.gw2app.steve.backend.util.vault.VaultHeader;
import xhsun.gw2app.steve.backend.util.vault.VaultType;

/**
 * A simple {@link Fragment} subclass.
 * TODO layout, onCreateView, and fill all overrides
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
	public void updateData(AbstractData data) {

	}

	@Override
	public void refreshData(AbstractData data) {

	}

	@Override
	public void processChange(Set<AccountData> preference) {

	}

	@Override
	public boolean shouldLoad() {
		return false;
	}

	@Override
	protected void onRefresh() {

	}

	@Override
	protected VaultHeader generateContent() {
		return null;
	}

	@Override
	protected void displayAccount(VaultHeader header) {

	}

	@Override
	protected boolean checkAvailability() {
		return false;
	}

	@Override
	protected boolean isAllRefreshed() {
		return false;
	}

	@Override
	public void onUpdateEmptyView(int size) {

	}
}
