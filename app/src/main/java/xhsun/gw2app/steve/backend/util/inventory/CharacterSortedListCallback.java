package xhsun.gw2app.steve.backend.util.inventory;

import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;

import xhsun.gw2app.steve.backend.database.character.CharacterInfo;

/**
 * sorted list callback for character info
 *
 * @author xhsun
 * @since 2017-05-01
 */

class CharacterSortedListCallback extends SortedList.Callback<CharacterInfo> {
	private RecyclerView.Adapter adapter;

	CharacterSortedListCallback(RecyclerView.Adapter adapter) {
		this.adapter = adapter;
	}

	@Override
	public int compare(CharacterInfo o1, CharacterInfo o2) {
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
	public boolean areContentsTheSame(CharacterInfo oldItem, CharacterInfo newItem) {
		if (oldItem == null && newItem == null) return true;
		else if (oldItem == null || newItem == null) return false;
		return oldItem.getInventory().size() == newItem.getInventory().size();
	}

	@Override
	public boolean areItemsTheSame(CharacterInfo item1, CharacterInfo item2) {
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
