package xhsun.gw2app.steve.view.storage.bank;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import xhsun.gw2app.steve.R;
import xhsun.gw2app.steve.util.listener.EndlessRecyclerViewScrollListener;
import xhsun.gw2app.steve.util.model.InventoryItem;
import xhsun.gw2app.steve.view.storage.StorageGridAdapter;

/**
 * @author xhsun
 * @since 2017-02-13
 */
public class BankFragment extends Fragment {
	private static final int SIZE = 64;
	private List<InventoryItem> storages;

	public BankFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_storage, container, false);

		storages = new ArrayList<>();//TODO empty for now

		Context context = view.getContext();
		RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.storage_list);
		GridLayoutManager manager = new GridLayoutManager(context, calculateColumns());
		recyclerView.setLayoutManager(manager);
		recyclerView.setAdapter(new StorageGridAdapter(storages, getContext()));
		recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(manager) {
			@Override
			public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
				//TODO getIteminfo(int[page] of ids) and construct inventoryItem class
				//need a way to get thing from list that we haven't got before
				//pop as we get x number of item?
			}
		});
		return view;
	}

	private int calculateColumns() {
		DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
		float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
		return (int) (dpWidth / SIZE);
	}
}
