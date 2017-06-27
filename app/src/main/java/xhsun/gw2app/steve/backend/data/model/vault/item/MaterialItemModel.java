package xhsun.gw2app.steve.backend.data.model.vault.item;

import me.xhsun.guildwars2wrapper.model.v2.account.MaterialStorage;
import me.xhsun.guildwars2wrapper.model.v2.util.Storage;
import xhsun.gw2app.steve.backend.data.model.ItemModel;

/**
 * {@link VaultItemModel} for material storage items
 *
 * @author xhsun
 * @since 2017-05-18
 */

public class MaterialItemModel extends VaultItemModel implements Countable {
	private int categoryID = -1;
	private long count = -1;

	public MaterialItemModel() {
		super("");
	}

	public MaterialItemModel(String api, String category, MaterialStorage material) {
		super("");
		init(api, material);
		setCategoryName(category);
	}

	private void init(String api, MaterialStorage material) {
		this.api = api;
		categoryID = material.getCategory();
		itemModel = new ItemModel(material.getItemId());
		count = material.getCount();
		binding = material.getBinding();
	}

	public int getCategoryID() {
		return categoryID;
	}

	public void setCategoryID(int categoryID) {
		this.categoryID = categoryID;
	}

	public String getCategoryName() {
		return name;
	}

	public void setCategoryName(String categoryName) {
		name = categoryName;
	}

	public void setItemData(ItemModel itemModel) {
		this.itemModel = itemModel;
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

	public Storage.Binding getBinding() {
		return binding;
	}

	public void setBinding(Storage.Binding binding) {
		this.binding = binding;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		MaterialItemModel that = (MaterialItemModel) o;

		return api.equals(that.api) &&
				categoryID == that.categoryID &&
				((itemModel != null) ? itemModel.equals(that.itemModel) : that.itemModel == null) &&
				binding == that.binding;
	}

	@Override
	public int hashCode() {
		int result = api.hashCode();
		result = 31 * result + (itemModel != null ? itemModel.hashCode() : 0);
		result = 31 * result + (binding != null ? binding.hashCode() : 0);
		result = 31 * result + categoryID;
		return result;
	}

	@Override
	public String toString() {
		return "MaterialItemModel{" +
				"api='" + api + '\'' +
				", category='" + name + '\'' +
				", itemModel=" + itemModel +
				", binding=" + binding +
				'}';
	}
}
