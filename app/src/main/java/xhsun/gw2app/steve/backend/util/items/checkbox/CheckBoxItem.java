package xhsun.gw2app.steve.backend.util.items.checkbox;

import android.view.View;
import android.widget.CheckBox;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.flexibleadapter.items.IHeader;
import eu.davidea.flexibleadapter.items.ISectionable;
import eu.davidea.viewholders.FlexibleViewHolder;
import xhsun.gw2app.steve.R;
import xhsun.gw2app.steve.backend.data.model.dialog.AbstractSelectModel;

/**
 * {@link AbstractFlexibleItem} for sub checkboxes in checkbox group
 *
 * @author xhsun
 * @since 2017-05-16
 */

public class CheckBoxItem<I extends AbstractSelectModel> extends AbstractFlexibleItem<CheckBoxItem.ViewHolder>
		implements ISectionable<CheckBoxItem.ViewHolder, IHeader>, OnCheckboxClicked {
	private IHeader header;
	private I item;
	private CheckBoxItem.ViewHolder temp;
	private OnCheckboxClicked listener;

	public CheckBoxItem(IHeader header, OnCheckboxClicked listener, I item) {
		this.header = header;
		this.listener = listener;
		this.item = item;
	}

	@Override
	public IHeader getHeader() {
		return header;
	}

	@Override
	public void setHeader(IHeader header) {
		this.header = header;
	}

	public I getItem() {
		return item;
	}

	@Override
	public int getLayoutRes() {
		return R.layout.item_checkbox;
	}

	@Override
	public ViewHolder createViewHolder(View view, FlexibleAdapter flexibleAdapter) {
		return new ViewHolder(view, flexibleAdapter);
	}

	@Override
	public void bindViewHolder(FlexibleAdapter adapter, CheckBoxItem.ViewHolder holder, int position, List payloads) {
		temp = holder;
		holder.checkBox.setText(item.getName());
		holder.checkBox.setChecked(item.isSelected());
		holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
			if (item.isSelected() == isChecked) return;
			item.setSelected(isChecked);
			listener.notifyClicked(item);
		});
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		CheckBoxItem<?> that = (CheckBoxItem<?>) o;

		return item.equals(that.item);
	}

	@Override
	public int hashCode() {
		return item.hashCode();
	}

	@Override
	public void notifyClicked(AbstractSelectModel holder) {
		if (item.isSelected() == holder.isSelected()) return;
		item.setSelected(holder.isSelected());
		if (temp != null && temp.checkBox.getText().toString().equals(item.getName()))
			temp.checkBox.setChecked(item.isSelected());
	}

	class ViewHolder extends FlexibleViewHolder {
		CheckBox checkBox;

		ViewHolder(View view, FlexibleAdapter adapter) {
			super(view, adapter);
			this.checkBox = (CheckBox) view.findViewById(R.id.dialog_select_child);
		}
	}
}
