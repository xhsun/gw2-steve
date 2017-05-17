package xhsun.gw2app.steve.backend.util.dialog.select;

/**
 * Holder template for selecting items from checkbox
 *
 * @author xhsun
 * @since 2017-05-16
 */

public abstract class Holder {
	protected String name;
	private boolean isSelected = false;

	public Holder(String name, boolean isSelected) {
		this.name = name;
		this.isSelected = isSelected;
	}

	public String getName() {
		return name;
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

		Holder holder = (Holder) o;

		return name.equals(holder.name);

	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}
}
