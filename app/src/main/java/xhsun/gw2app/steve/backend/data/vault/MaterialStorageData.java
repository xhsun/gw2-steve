package xhsun.gw2app.steve.backend.data.vault;

import java.util.ArrayList;
import java.util.List;

import xhsun.gw2app.steve.backend.data.AbstractData;
import xhsun.gw2app.steve.backend.data.vault.item.MaterialItemData;

/**
 * {@link AbstractData} for categorize material storage items
 *
 * @author xhsun
 * @since 2017-05-18
 */

public class MaterialStorageData extends AbstractData {
	private long id;
	private List<MaterialItemData> items;

	public MaterialStorageData(long categoryID, String category) {
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

	public List<MaterialItemData> getItems() {
		return items;
	}

	public void setItems(List<MaterialItemData> items) {
		this.items = items;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		MaterialStorageData that = (MaterialStorageData) o;

		return id == that.id;
	}

	@Override
	public int hashCode() {
		int result = (int) (id ^ (id >>> 32));
		result = 31 * result + name.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return "MaterialStorageData{" +
				"category='" + name + '\'' +
				", itemSize=" + ((items == null) ? 0 : items.size()) +
				'}';
	}
}
