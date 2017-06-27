package xhsun.gw2app.steve.view.fragment.vault.storage;

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
import xhsun.gw2app.steve.backend.data.model.AbstractModel;
import xhsun.gw2app.steve.backend.data.model.AccountModel;
import xhsun.gw2app.steve.backend.util.items.vault.VaultHeader;
import xhsun.gw2app.steve.backend.util.support.vault.VaultType;
import xhsun.gw2app.steve.backend.util.support.vault.storage.StorageTabFragment;

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
	public void updateData(AbstractModel data) {

	}

	@Override
	public void refreshData(AbstractModel data) {

	}

	@Override
	public void processChange(Set<AccountModel> preference) {

	}

	@Override
	public boolean shouldLoad() {
		return false;
	}

	@Override
	protected void onRefresh() {

	}

	@Override
	protected boolean isAllRefreshed() {
		return false;
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
	public void onUpdateEmptyView(int size) {

	}
}