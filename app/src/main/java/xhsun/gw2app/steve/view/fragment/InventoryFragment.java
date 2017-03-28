package xhsun.gw2app.steve.view.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import xhsun.gw2app.steve.R;

/**
 * InventoryFragment is a subclass of {@link Fragment}<br/>
 *
 * @author xhsun
 * @since 2017-03-28
 */
public class InventoryFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_inventory, container, false);
	}

}
