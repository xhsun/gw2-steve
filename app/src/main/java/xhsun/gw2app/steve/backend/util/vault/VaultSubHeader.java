package xhsun.gw2app.steve.backend.util.vault;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractExpandableItem;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.flexibleadapter.items.IExpandable;
import eu.davidea.viewholders.ExpandableViewHolder;
import xhsun.gw2app.steve.R;
import xhsun.gw2app.steve.backend.data.AbstractData;
import xhsun.gw2app.steve.backend.util.items.BasicItem;

/**
 * {@link AbstractExpandableItem} for vault sub header, which usually for character name/material name, etc.
 *
 * @author xhsun
 * @since 2017-05-09
 */

public class VaultSubHeader<T extends AbstractData>
		extends AbstractFlexibleItem<VaultSubHeader.SubHeaderViewHolder>
		implements IExpandable<VaultSubHeader.SubHeaderViewHolder, BasicItem>, Comparable {
	private T data;
	private List<BasicItem> items;
	private boolean expanded = false;

	public VaultSubHeader(T data) {
		this.data = data;
	}

	@Override
	public int getLayoutRes() {
		return R.layout.item_subheader_vault;
	}

	@Override
	public boolean isExpanded() {
		return expanded;
	}

	@Override
	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
	}

	@Override
	public int getExpansionLevel() {
		return 1;
	}

	@Override
	public List<BasicItem> getSubItems() {
		return (items == null) ? new ArrayList<BasicItem>() : items;
	}

	public BasicItem getSubItem(int position) {
		if (items != null && position >= 0 && position < items.size()) {
			return items.get(position);
		}
		return null;
	}

	public boolean containsSubItem(BasicItem item) {
		return items != null && item != null && items.contains(item);
	}

	public int getSubItemsCount() {
		return (items == null) ? 0 : items.size();
	}

//	public final boolean hasSubItems() {
//		return items!= null && items.size() > 0;
//	}
//
//	public boolean removeSubItem(BasicItem item) {
//		return item != null && items.remove(item);
//	}
//
//	public boolean removeSubItem(int position) {
//		if (items != null && position >= 0 && position < items.size()) {
//			items.remove(position);
//			return true;
//		}
//		return false;
//	}


	public void setSubItems(List<BasicItem> items) {
		this.items = new ArrayList<>(items);
	}

	public void addSubItem(BasicItem item) {
		if (items == null)
			items = new ArrayList<>();
		if (items.contains(item))
			items.get(items.indexOf(item)).setData(item.getData());
		else items.add(item);
	}

	@Override
	public SubHeaderViewHolder createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater, ViewGroup parent) {
		return new SubHeaderViewHolder(inflater.inflate(getLayoutRes(), parent, false), adapter);
	}

	@Override
	public void bindViewHolder(FlexibleAdapter adapter, VaultSubHeader.SubHeaderViewHolder holder, int position, List payloads) {
		String cappedName = data.getName().substring(0, 1).toUpperCase() + data.getName().substring(1);
		holder.name.setText(cappedName);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		VaultSubHeader that = (VaultSubHeader) o;
		return data.equals(that.data);
	}

	@Override
	public int hashCode() {
		return data.hashCode();
	}

	@Override
	public String toString() {
		return "VaultSubHeader{" +
				"data=" + data +
				", expanded=" + expanded +
				'}';
	}

	@Override
	public int compareTo(@NonNull Object o) {
		if (this == o) return 0;
		if (getClass() != o.getClass()) return 1;

		VaultSubHeader that = (VaultSubHeader) o;
		return data.getName().compareTo(that.data.getName());
	}


	class SubHeaderViewHolder extends ExpandableViewHolder {
		TextView name;

		SubHeaderViewHolder(View view, FlexibleAdapter adapter) {
			super(view, adapter);
			this.name = (TextView) view.findViewById(R.id.vault_subheader_title);
		}

		//TODO disable expandable until I know how to deal with the messy behaviors
		@Override
		protected boolean isViewExpandableOnClick() {
			return false;
		}
	}
}
