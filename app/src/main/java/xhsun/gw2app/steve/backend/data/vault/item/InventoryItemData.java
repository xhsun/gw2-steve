package xhsun.gw2app.steve.backend.data.vault.item;

import me.xhsun.guildwars2wrapper.model.v2.util.Inventory;
import me.xhsun.guildwars2wrapper.model.v2.util.Storage;
import xhsun.gw2app.steve.backend.data.ItemData;
import xhsun.gw2app.steve.backend.data.SkinData;

/**
 * {@link VaultItemData} for inventory items
 *
 * @author xhsun
 * @since 2017-05-18
 */

public class InventoryItemData extends VaultItemData implements Countable {
	private long count = -1;

	public InventoryItemData() {
		super("");
	}

	public InventoryItemData(String api, String name, Inventory inventory) {
		super(name);
		this.api = api;
		itemData = new ItemData(inventory.getItemId());
		if (inventory.getSkin() != 0) skinData = new SkinData(inventory.getSkin());
		count = inventory.getCount() * ((inventory.getCharges() < 1) ? 1 : inventory.getCharges());
		binding = inventory.getBinding();
		boundTo = (inventory.getBoundTo() == null) ? "" : inventory.getBoundTo();
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setItemData(ItemData itemData) {
		this.itemData = itemData;
	}

	public void setSkinData(SkinData skinData) {
		this.skinData = skinData;
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

		InventoryItemData that = (InventoryItemData) o;

		return api.equals(that.api) &&
				name.equals(that.name) &&
				((itemData != null) ? itemData.equals(that.itemData) : that.itemData == null) &&
				((skinData != null) ? skinData.equals(that.skinData) : that.skinData == null) &&
				binding == that.binding && boundTo.equals(that.boundTo);
	}

	@Override
	public int hashCode() {
		int result = (name != null ? name.hashCode() : 0);
		result = 31 * result + (api != null ? api.hashCode() : 0);
		result = 31 * result + (itemData != null ? itemData.hashCode() : 0);
		result = 31 * result + (skinData != null ? skinData.hashCode() : 0);
		result = 31 * result + (binding != null ? binding.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "InventoryItemData{" +
				"name='" + name + '\'' +
				", itemData=" + itemData +
				", skinData=" + skinData +
				", binding=" + binding +
				", boundTo='" + boundTo + '\'' +
				'}';
	}
}
