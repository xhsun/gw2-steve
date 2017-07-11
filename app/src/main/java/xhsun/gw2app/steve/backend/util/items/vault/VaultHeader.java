package xhsun.gw2app.steve.backend.util.items.vault;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractExpandableHeaderItem;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.flexibleadapter.items.IExpandable;
import eu.davidea.flexibleadapter.items.IHeader;
import eu.davidea.viewholders.ExpandableViewHolder;
import xhsun.gw2app.steve.R;
import xhsun.gw2app.steve.backend.data.model.AbstractModel;

/**
 * {@link AbstractExpandableHeaderItem} for vault header, which mainly for displaying account name
 *
 * @author xhsun
 * @since 2017-05-09
 */

public class VaultHeader<T extends AbstractModel, S extends AbstractFlexibleItem>
		extends AbstractFlexibleItem<VaultHeader.HeaderViewHolder>
		implements IExpandable<VaultHeader.HeaderViewHolder, S>, IHeader<VaultHeader.HeaderViewHolder> {
	private T data;
	private boolean expanded = false;
	private List<S> items;

	public VaultHeader(T data) {
		this.data = data;
	}

//	public VaultHeader(VaultHeader<T, S> header) {
//		this.data = header.data;
//		this.expanded = header.expanded;
//		items = new ArrayList<>(header.items);
//	}

	public T getData() {
		return data;
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
		return 0;
	}

	@Override
	public List<S> getSubItems() {
		return (items == null) ? new ArrayList<>() : items;
	}

	@Nullable
	public List<S> getRawSubItems() {
		return items;
	}

	public int getSubItemsCount() {
		return (items == null) ? 0 : items.size();
	}

	public void setSubItems(List<S> items) {
		this.items = items;
	}

	public boolean containsSubItem(S item) {
		return items != null && item != null && items.contains(item);
	}

//	public final boolean hasSubItems() {
//		return items != null && items.size() > 0;
//	}

	public boolean removeSubItem(S item) {
		return items != null && item != null && items.remove(item);
	}

//	public boolean removeSubItem(int position) {
//		if (items != null && position >= 0 && position < items.size()) {
//			items.remove(position);
//			return true;
//		}
//		return false;
//	}

	public void addSubItem(int position, S item) {
		if (item == null) return;
		if (items == null) items = new ArrayList<>();

		if (position < items.size()) items.add(position, item);
		else items.add(item);
	}

	public void addSubItem(S item) {
		if (items == null)
			items = new ArrayList<>();
		items.add(item);
	}

	@Override
	public int getLayoutRes() {
		return R.layout.item_header_vault;
	}

	@Override
	public HeaderViewHolder createViewHolder(View view, FlexibleAdapter flexibleAdapter) {
		return new HeaderViewHolder(view, flexibleAdapter);
	}

	@Override
	public void bindViewHolder(FlexibleAdapter adapter, VaultHeader.HeaderViewHolder holder, int position, List payloads) {
		if (data.getName().equals("")) return;
		String cappedName = data.getName().substring(0, 1).toUpperCase() + data.getName().substring(1);
		holder.name.setText(cappedName);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		VaultHeader that = (VaultHeader) o;
		return data.equals(that.data);
	}

	@Override
	public int hashCode() {
		return data.hashCode();
	}

	class HeaderViewHolder extends ExpandableViewHolder {
		TextView name;

		HeaderViewHolder(View view, FlexibleAdapter adapter) {
			super(view, adapter);
			this.name = (TextView) view.findViewById(R.id.vault_header_title);
		}

		@Override
		protected boolean isViewExpandableOnClick() {
			return false;
		}
	}
}
