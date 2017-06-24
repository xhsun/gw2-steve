package xhsun.gw2app.steve.backend.data.model.vault;

import java.util.ArrayList;
import java.util.List;

import xhsun.gw2app.steve.backend.data.model.AbstractModel;
import xhsun.gw2app.steve.backend.data.model.vault.item.WardrobeItemModel;

/**
 * {@link AbstractModel} for wardrobe sub categories
 *
 * @author xhsun
 * @since 2017-05-18
 */

public class WardrobeSubModel extends AbstractModel {
	private List<WardrobeItemModel> items;

	public WardrobeSubModel(String subType) {
		super(subType);
		items = new ArrayList<>();
	}

	public String getSubTypeName() {
		return name;
	}

	public List<WardrobeItemModel> getItems() {
		return items;
	}

	public void setItems(List<WardrobeItemModel> items) {
		this.items = items;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		WardrobeSubModel that = (WardrobeSubModel) o;

		return name.equals(that.name);
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public String toString() {
		return "WardrobeSubModel{" +
				"SubCategory='" + name + '\'' +
				", itemSize=" + ((items == null) ? 0 : items.size()) +
				'}';
	}
}
