package xhsun.gw2app.steve.backend.util.storage;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import timber.log.Timber;

/**
 * Created by hannah on 27/04/17.
 */

public class WrapContentLinearLayoutManager extends LinearLayoutManager {

	public WrapContentLinearLayoutManager(Context context) {
		super(context);
	}

	//... constructor
	@Override
	public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
		try {
			super.onLayoutChildren(recycler, state);
		} catch (IndexOutOfBoundsException e) {
			Timber.e(e, "IndexOutOfBoundsException in RecyclerView");
		}
	}
}
