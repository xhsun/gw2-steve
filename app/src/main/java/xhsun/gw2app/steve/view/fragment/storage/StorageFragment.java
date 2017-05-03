package xhsun.gw2app.steve.view.fragment.storage;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;
import xhsun.gw2app.steve.R;
import xhsun.gw2app.steve.backend.util.storage.StoragePagerAdapter;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 *
 * @author xhsun
 * @since 2017-05-01
 */
public class StorageFragment extends Fragment {
	private static final String PREFERENCE_NAME = "storageDisplay";
	private SharedPreferences preferences;

	@BindView(R.id.storage_tab)
	TabLayout tabLayout;
	@BindView(R.id.storage_viewpager)
	ViewPager viewPager;
	@BindView(R.id.storage_fab)
	FloatingActionButton fab;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_storage, container, false);
		ButterKnife.bind(this, view);

		Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
		toolbar.setTitle("Storage");
		setHasOptionsMenu(true);

		//load shared preference
		preferences = getActivity().getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);

		//init tabs
		viewPager.setAdapter(new StoragePagerAdapter(getFragmentManager()));
		tabLayout.setupWithViewPager(viewPager);

		//TODO more init

		Timber.i("Initialization complete");
		return view;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();//prevent constantly adding stuff to toolbar
		inflater.inflate(R.menu.fragment_search_toolbar, menu);
		setupSearchView(menu);//set up search box
		super.onCreateOptionsMenu(menu, inflater);
		Timber.i("Toolbar setup finished");
	}

	//setup search with with search hint and listener
	private void setupSearchView(Menu menu) {
		SearchView search = (SearchView) menu.findItem(R.id.toolbar_search).getActionView();
		search.setQueryHint("Search Storage");
//		search.setOnQueryTextListener(new QueryTextListener(this));
		search.setIconified(true);
		Timber.i("SearchView setup finished");
	}
}
