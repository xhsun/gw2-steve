package xhsun.gw2app.steve.backend.util.items;

import android.support.v7.util.SortedList;

import xhsun.gw2app.steve.backend.database.storage.StorageInfo;

/**
 * Callback for storage info sorted list
 *
 * @author xhsun
 * @since 2017-04-17
 */

class SortedListCallback extends SortedList.Callback<StorageInfo> {
	private StorageGridAdapter adapter;

	SortedListCallback(StorageGridAdapter adapter) {
		this.adapter = adapter;
	}

	@Override
	public int compare(StorageInfo o1, StorageInfo o2) {
		return ((Long) o1.getId()).compareTo(o2.getId());
	}

	@Override
	public void onChanged(int position, int count) {
		adapter.notifyItemRangeChanged(position, count);
	}

	@Override
	public boolean areContentsTheSame(StorageInfo oldItem, StorageInfo newItem) {
		return oldItem.equals(newItem);
	}

	@Override
	public boolean areItemsTheSame(StorageInfo item1, StorageInfo item2) {
		return item1.getId() == item2.getId();
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
