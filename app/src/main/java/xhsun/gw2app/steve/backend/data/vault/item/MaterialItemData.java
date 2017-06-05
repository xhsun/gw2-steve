package xhsun.gw2app.steve.backend.data.vault.item;

import me.xhsun.guildwars2wrapper.model.account.Material;
import me.xhsun.guildwars2wrapper.model.util.Storage;
import xhsun.gw2app.steve.backend.data.ItemData;

/**
 * {@link VaultItemData} for material storage items
 *
 * @author xhsun
 * @since 2017-05-18
 */

public class MaterialItemData extends VaultItemData implements Countable {
	private long categoryID = -1;
	private long count = -1;

	public MaterialItemData() {
		super("");
	}

	public MaterialItemData(String api, Material material) {
		super("");
		this.api = api;
		categoryID = material.getCategory();
		itemData = new ItemData(material.getItemId());
		count = material.getCount();
		binding = material.getBinding();
	}

	public long getCategoryID() {
		return categoryID;
	}

	public void setCategoryID(long categoryID) {
		this.categoryID = categoryID;
	}

	public String getCategoryName() {
		return name;
	}

	public void setCategoryName(String categoryName) {
		name = categoryName;
	}

	public void setItemData(ItemData itemData) {
		this.itemData = itemData;
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

		MaterialItemData that = (MaterialItemData) o;

		return api.equals(that.api) &&
				categoryID == that.categoryID &&
				((itemData != null) ? itemData.equals(that.itemData) : that.itemData == null) &&
				binding == that.binding;
	}

	@Override
	public int hashCode() {
		int result = api.hashCode();
		result = 31 * result + (itemData != null ? itemData.hashCode() : 0);
		result = 31 * result + (binding != null ? binding.hashCode() : 0);
		result = 31 * result + (int) (categoryID ^ (categoryID >>> 32));
		return result;
	}

	@Override
	public String toString() {
		return "MaterialItemData{" +
				"api='" + api + '\'' +
				", category='" + name + '\'' +
				", itemData=" + itemData +
				", binding=" + binding +
				'}';
	}
}
