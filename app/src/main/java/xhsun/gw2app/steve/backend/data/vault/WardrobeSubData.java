package xhsun.gw2app.steve.backend.data.vault;

import java.util.ArrayList;
import java.util.List;

import xhsun.gw2app.steve.backend.data.AbstractData;
import xhsun.gw2app.steve.backend.data.vault.item.WardrobeItemData;

/**
 * {@link AbstractData} for wardrobe sub categories
 *
 * @author xhsun
 * @since 2017-05-18
 */

public class WardrobeSubData extends AbstractData {
	private List<WardrobeItemData> items;

	public WardrobeSubData(String subType) {
		super(subType);
		items = new ArrayList<>();
	}

	public String getSubTypeName() {
		return name;
	}

	public List<WardrobeItemData> getItems() {
		return items;
	}

	public void setItems(List<WardrobeItemData> items) {
		this.items = items;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		WardrobeSubData that = (WardrobeSubData) o;

		return name.equals(that.name);
	}

	@Override
	public int hashCode() {
		int result = name.hashCode();
		result = 31 * result + (items != null ? items.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "WardrobeSubData{" +
				"SubCategory='" + name + '\'' +
				", itemSize=" + ((items == null) ? 0 : items.size()) +
				'}';
	}
}
