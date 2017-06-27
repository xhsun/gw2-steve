package xhsun.gw2app.steve.backend.data.model.vault.item;

import me.xhsun.guildwars2wrapper.model.v2.util.Inventory;
import me.xhsun.guildwars2wrapper.model.v2.util.Storage;
import xhsun.gw2app.steve.backend.data.model.ItemModel;
import xhsun.gw2app.steve.backend.data.model.SkinModel;

/**
 * {@link VaultItemModel} for bank items
 *
 * @author xhsun
 * @since 2017-05-18
 */

public class BankItemModel extends VaultItemModel implements Countable {
	private long count = -1;

	public BankItemModel() {
		super("");
	}

	public BankItemModel(String api, Inventory bank) {
		super("");
		this.api = api;
		itemModel = new ItemModel(bank.getItemId());
		if (bank.getSkin() != 0) skinModel = new SkinModel(bank.getSkin());
		count = bank.getCount() * ((bank.getCharges() < 1) ? 1 : bank.getCharges());
		binding = bank.getBinding();
		boundTo = (bank.getBoundTo() == null) ? "" : bank.getBoundTo();
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

		VaultItemModel that = (VaultItemModel) o;

		return api.equals(that.api) &&
				((itemModel != null) ? itemModel.equals(that.itemModel) : that.itemModel == null) &&
				((skinModel != null) ? skinModel.equals(that.skinModel) : that.skinModel == null) &&
				binding == that.binding &&
				boundTo.equals(that.boundTo);
	}

	@Override
	public int hashCode() {
		int result = api.hashCode();
		result = 31 * result + (itemModel != null ? itemModel.hashCode() : 0);
		result = 31 * result + (skinModel != null ? skinModel.hashCode() : 0);
		result = 31 * result + (binding != null ? binding.hashCode() : 0);
		result = 31 * result + boundTo.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return "BankItemModel{" +
				"api='" + api + '\'' +
				", itemModel=" + itemModel +
				", skinModel=" + skinModel +
				", binding=" + binding +
				", boundTo='" + boundTo + '\'' +
				'}';
	}
}
