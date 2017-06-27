package xhsun.gw2app.steve.backend.data.model.vault.item;

import me.xhsun.guildwars2wrapper.model.v2.util.Inventory;
import me.xhsun.guildwars2wrapper.model.v2.util.Storage;
import xhsun.gw2app.steve.backend.data.model.ItemModel;
import xhsun.gw2app.steve.backend.data.model.SkinModel;

/**
 * {@link VaultItemModel} for inventory items
 *
 * @author xhsun
 * @since 2017-05-18
 */

public class InventoryItemModel extends VaultItemModel implements Countable {
	private long count = -1;

	public InventoryItemModel() {
		super("");
	}

	public InventoryItemModel(String api, String name, Inventory inventory) {
		super(name);
		this.api = api;
		itemModel = new ItemModel(inventory.getItemId());
		if (inventory.getSkin() != 0) skinModel = new SkinModel(inventory.getSkin());
		count = inventory.getCount() * ((inventory.getCharges() < 1) ? 1 : inventory.getCharges());
		binding = inventory.getBinding();
		boundTo = (inventory.getBoundTo() == null) ? "" : inventory.getBoundTo();
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setItemData(ItemModel itemModel) {
		this.itemModel = itemModel;
	}

	public void setSkinData(SkinModel skinModel) {
		this.skinModel = skinModel;
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

	public String getBoundTo() {
		return boundTo;
	}

	public void setBoundTo(String boundTo) {
		this.boundTo = boundTo;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		InventoryItemModel that = (InventoryItemModel) o;

		return api.equals(that.api) &&
				name.equals(that.name) &&
				((itemModel != null) ? itemModel.equals(that.itemModel) : that.itemModel == null) &&
				((skinModel != null) ? skinModel.equals(that.skinModel) : that.skinModel == null) &&
				binding == that.binding && boundTo.equals(that.boundTo);
	}

	@Override
	public int hashCode() {
		int result = (name != null ? name.hashCode() : 0);
		result = 31 * result + (api != null ? api.hashCode() : 0);
		result = 31 * result + (itemModel != null ? itemModel.hashCode() : 0);
		result = 31 * result + (skinModel != null ? skinModel.hashCode() : 0);
		result = 31 * result + (binding != null ? binding.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "InventoryItemModel{" +
				"name='" + name + '\'' +
				", itemModel=" + itemModel +
				", skinModel=" + skinModel +
				", binding=" + binding +
				", boundTo='" + boundTo + '\'' +
				'}';
	}
}
