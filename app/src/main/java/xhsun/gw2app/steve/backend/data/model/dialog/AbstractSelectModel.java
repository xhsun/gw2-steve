package xhsun.gw2app.steve.backend.data.model.dialog;

import xhsun.gw2app.steve.backend.data.model.AbstractModel;

/**
 * AbstractSelectModel template for selecting items from checkbox
 *
 * @author xhsun
 * @since 2017-05-16
 */

public abstract class AbstractSelectModel extends AbstractModel {
	private boolean isSelected = false;

	AbstractSelectModel(String name, boolean isSelected) {
		super(name);
		this.isSelected = isSelected;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean selected) {
		isSelected = selected;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		AbstractSelectModel holder = (AbstractSelectModel) o;

		return name.equals(holder.name);
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}
}
