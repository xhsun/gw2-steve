package xhsun.gw2app.steve.backend.util.items.checkbox;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.annimon.stream.Stream;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.flexibleadapter.items.IExpandable;
import eu.davidea.flexibleadapter.items.IHeader;
import eu.davidea.viewholders.ExpandableViewHolder;
import xhsun.gw2app.steve.R;
import xhsun.gw2app.steve.backend.data.model.dialog.AbstractSelectModel;

/**
 * {@link AbstractFlexibleItem} for checkbox<br/>
 * This can also be used as expandable header for checkbox group
 *
 * @author xhsun
 * @since 2017-05-16
 */

public class CheckBoxHeaderItem<I extends AbstractSelectModel> extends AbstractFlexibleItem<CheckBoxHeaderItem.ViewHolder>
		implements IExpandable<CheckBoxHeaderItem.ViewHolder, CheckBoxItem>,
		IHeader<CheckBoxHeaderItem.ViewHolder>, OnCheckboxClicked {
	private I item;
	private OnCheckBoxExpanded listener;
	private CheckBoxHeaderItem.ViewHolder temp;
	private List<CheckBoxItem> subItems;
	private boolean isExpanded = false, isHeader = false;

	/**
	 * use this constructor to use checkbox item without expandable feature
	 *
	 * @param item data
	 */
	public CheckBoxHeaderItem(I item) {
		this.item = item;
	}

	/**
	 * use this constructor to use checkbox item with expandable feature
	 *
	 * @param item     data
	 * @param listener {@link OnCheckBoxExpanded}
	 * @param subItems list of sub items
	 */
	public CheckBoxHeaderItem(I item, OnCheckBoxExpanded listener, List<CheckBoxItem> subItems) {
		this.item = item;
		this.listener = listener;
		isHeader = true;
		this.subItems = subItems;
	}

	@Override
	public int getLayoutRes() {
		return R.layout.item_header_checkbox;
	}

	@Override
	public CheckBoxHeaderItem.ViewHolder createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater, ViewGroup parent) {
		return new ViewHolder(inflater.inflate(getLayoutRes(), parent, false), adapter);
	}

	@Override
	public void bindViewHolder(FlexibleAdapter adapter, CheckBoxHeaderItem.ViewHolder holder, int position, List payloads) {
		temp = holder;
		String cappedName = item.getName().substring(0, 1).toUpperCase() + item.getName().substring(1);
		holder.checkBox.setText(cappedName);
		holder.checkBox.setChecked(item.isSelected());
		holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
			if (item.isSelected() == isChecked) return;
			item.setSelected(isChecked);
			if (subItems == null) return;
			Stream.of(subItems).forEach(i -> i.notifyClicked(item));
		});
		if (isHeader) holder.imageView.setVisibility(View.VISIBLE);

	}

	@Override
	public boolean isExpanded() {
		return isExpanded;
	}

	@Override
	public void setExpanded(boolean expanded) {
		isExpanded = expanded;
		if (listener != null) listener.notifyExpanded(expanded);
		if (temp == null || !temp.checkBox.getText().toString().equalsIgnoreCase(item.getName()))
			return;
		if (isHeader) {
			if (isExpanded) {
				temp.imageView.post(() -> temp.imageView.setImageResource(R.drawable.ic_arrow_up));
			} else {
				temp.imageView.post(() -> temp.imageView.setImageResource(R.drawable.ic_arrow_down));
			}
		}
	}

	@Override
	public int getExpansionLevel() {
		return 0;
	}

	@Override
	public List<CheckBoxItem> getSubItems() {
		return subItems;
	}

	@Override
	public void notifyClicked(AbstractSelectModel holder) {
		if (holder.isSelected() == item.isSelected()) return;
		if (!holder.isSelected()) {
			item.setSelected(false);
		} else {
			if (Stream.of(subItems).anyMatch(i -> !i.getItem().isSelected())) return;
			item.setSelected(true);
		}
		if (temp != null && temp.checkBox.getText().toString().equalsIgnoreCase(item.getName()))
			temp.checkBox.setChecked(item.isSelected());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		CheckBoxHeaderItem<?> that = (CheckBoxHeaderItem<?>) o;

		return item.equals(that.item);

	}

	@Override
	public int hashCode() {
		return item.hashCode();
	}

	class ViewHolder extends ExpandableViewHolder {
		CheckBox checkBox;
		ImageView imageView;

		ViewHolder(View view, FlexibleAdapter adapter) {
			super(view, adapter);
			this.checkBox = (CheckBox) view.findViewById(R.id.dialog_select_header);
			this.imageView = (ImageView) view.findViewById(R.id.dialog_select_arrow);
		}

		@Override
		protected boolean isViewExpandableOnClick() {
			return isHeader;
		}

		protected boolean isViewCollapsibleOnLongClick() {
			return isHeader;
		}
	}
}
