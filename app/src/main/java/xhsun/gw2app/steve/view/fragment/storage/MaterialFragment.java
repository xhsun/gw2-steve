package xhsun.gw2app.steve.view.fragment.storage;

import android.support.v4.app.Fragment;

import java.util.Set;

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
