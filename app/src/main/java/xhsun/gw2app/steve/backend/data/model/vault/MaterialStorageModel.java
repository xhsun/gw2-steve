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
	private String api = "";
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

	public void setApi(String api) {
		this.api = api;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		MaterialStorageModel that = (MaterialStorageModel) o;

		return id == that.id &&
				(api != null ? api.equals(that.api) : that.api == null);

	}

	@Override
	public int hashCode() {
		int result = id;
		result = 31 * result + (api != null ? api.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "MaterialStorageModel{" +
				"category='" + name + '\'' +
				", itemSize=" + ((items == null) ? 0 : items.size()) +
				'}';
	}
}
