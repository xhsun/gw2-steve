package xhsun.gw2app.steve.backend.data.model.vault;

import java.util.ArrayList;
import java.util.List;

import xhsun.gw2app.steve.backend.data.model.AbstractModel;
import xhsun.gw2app.steve.backend.data.model.vault.item.MaterialItemModel;

/**
 * {@link AbstractModel} for categorize material storage items
 *
 * @author xhsun
 * @since 2017-05-18
 */

public class MaterialStorageModel extends AbstractModel {
	private int id;
	private List<MaterialItemModel> items;

	public MaterialStorageModel(int categoryID, String category) {
		super(category);
		id = categoryID;
		items = new ArrayList<>();
	}

	public long getCategory() {
		return id;
	}

	public String getCategoryName() {
		return name;
	}

	public List<MaterialItemModel> getItems() {
		return items;
	}

	public void setItems(List<MaterialItemModel> items) {
		this.items = items;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		MaterialStorageModel that = (MaterialStorageModel) o;

		return id == that.id;
	}

	@Override
	public int hashCode() {
		return id;
	}

	@Override
	public String toString() {
		return "MaterialStorageModel{" +
				"category='" + name + '\'' +
				", itemSize=" + ((items == null) ? 0 : items.size()) +
				'}';
	}
}
