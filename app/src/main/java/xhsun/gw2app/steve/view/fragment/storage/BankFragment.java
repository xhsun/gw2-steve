package xhsun.gw2app.steve.view.fragment.storage;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Set;

import xhsun.gw2app.steve.R;
import xhsun.gw2app.steve.backend.database.account.AccountInfo;
import xhsun.gw2app.steve.backend.util.items.StorageContentFragment;
import xhsun.gw2app.steve.backend.util.items.StorageType;

/**
 * A simple {@link Fragment} subclass.
 *
 * @author xhsun
 * @since 2017-05-03
 */
public class BankFragment extends StorageContentFragment {

	public BankFragment() {
		super.setType(StorageType.BANK);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_bank, container, false);
	}

	@Override
	public void filter(String query) {

	}

	@Override
	public void restore() {

	}

	@Override
	public void processPreferenceChange(Set<AccountInfo> preference) {

	}
}
