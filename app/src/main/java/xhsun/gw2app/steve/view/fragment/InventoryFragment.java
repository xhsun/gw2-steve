package xhsun.gw2app.steve.view.fragment;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import xhsun.gw2app.steve.MainApplication;
import xhsun.gw2app.steve.R;
import xhsun.gw2app.steve.backend.database.account.AccountWrapper;
import xhsun.gw2app.steve.backend.database.character.CharacterWrapper;
import xhsun.gw2app.steve.backend.database.character.StorageWrapper;

/**
 * InventoryFragment is a subclass of {@link Fragment}<br/>
 *
 * @author xhsun
 * @since 2017-03-28
 */
public class InventoryFragment extends Fragment {
	@Inject
	StorageWrapper storageWrapper;
	@Inject
	CharacterWrapper characterWrapper;
	@Inject
	AccountWrapper accountWrapper;
	@BindView(R.id.inventory_account_list)
	RecyclerView list;
	@BindView(R.id.inventory_refresh)
	SwipeRefreshLayout refreshLayout;
	@BindView(R.id.inventory_fab)
	FloatingActionButton fab;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		((MainApplication) getActivity().getApplication()).getServiceComponent().inject(this);//injection
		View view = inflater.inflate(R.layout.fragment_inventory, container, false);
		ButterKnife.bind(this, view);

		Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
		toolbar.setTitle("Inventory");


		refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				//TODO refresh
			}
		});
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//TODO pull up dialog to select character to show
			}
		});
		return view;
	}

}
