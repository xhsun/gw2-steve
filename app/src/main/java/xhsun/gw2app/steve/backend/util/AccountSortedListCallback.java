package xhsun.gw2app.steve.backend.util;

import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;

import xhsun.gw2app.steve.backend.database.account.AccountInfo;

/**
 * sorted list callback for account info
 *
 * @author xhsun
 * @since 2017-05-01
 */

public class AccountSortedListCallback extends SortedList.Callback<AccountInfo> {
	private RecyclerView.Adapter adapter;

	public AccountSortedListCallback(RecyclerView.Adapter adapter) {
		this.adapter = adapter;
	}

	@Override
	public int compare(AccountInfo o1, AccountInfo o2) {
		if (o1 == null && o2 == null) return 0;
		else if (o1 == null) return 1;
		else if (o2 == null) return -1;
		return o1.getName().compareTo(o2.getName());
	}

	@Override
	public void onChanged(int position, int count) {
		adapter.notifyItemRangeChanged(position, count);
	}

	@Override
	public boolean areContentsTheSame(AccountInfo oldItem, AccountInfo newItem) {
		if (oldItem == null && newItem == null) return true;
		else if (oldItem == null || newItem == null) return false;
		return oldItem.equals(newItem);
	}

	@Override
	public boolean areItemsTheSame(AccountInfo item1, AccountInfo item2) {
		if (item1 == null && item2 == null) return true;
		else if (item1 == null || item2 == null) return false;
		return item1.equals(item2);
	}

	@Override
	public void onInserted(int position, int count) {
		adapter.notifyItemRangeInserted(position, count);
	}

	@Override
	public void onRemoved(int position, int count) {
		adapter.notifyItemRangeRemoved(position, count);
	}

	@Override
	public void onMoved(int fromPosition, int toPosition) {
		adapter.notifyItemMoved(fromPosition, toPosition);
	}
}
