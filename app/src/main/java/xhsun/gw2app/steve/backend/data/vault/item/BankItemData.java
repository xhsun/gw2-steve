package xhsun.gw2app.steve.backend.data.vault.item;

import me.xhsun.guildwars2wrapper.model.account.Bank;
import me.xhsun.guildwars2wrapper.model.util.Storage;
import xhsun.gw2app.steve.backend.data.ItemData;
import xhsun.gw2app.steve.backend.data.SkinData;

/**
 * {@link VaultItemData} for bank items
 *
 * @author xhsun
 * @since 2017-05-18
 */

public class BankItemData extends VaultItemData implements Countable {
	private long count = -1;

	public BankItemData() {
		super("");
	}

	public BankItemData(String api, Bank bank) {
		super("");
		this.api = api;
		itemData = new ItemData(bank.getItemId());
		if (bank.getSkinId() != 0) skinData = new SkinData(bank.getSkinId());
		count = bank.getCount() * ((bank.getCharges() < 1) ? 1 : bank.getCharges());
		binding = bank.getBinding();
		boundTo = (bank.getBound_to() == null) ? "" : bank.getBound_to();
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

		VaultItemData that = (VaultItemData) o;

		return api.equals(that.api) &&
				((itemData != null) ? itemData.equals(that.itemData) : that.itemData == null) &&
				((skinData != null) ? skinData.equals(that.skinData) : that.skinData == null) &&
				binding == that.binding &&
				boundTo.equals(that.boundTo);
	}

	@Override
	public int hashCode() {
		int result = api.hashCode();
		result = 31 * result + (itemData != null ? itemData.hashCode() : 0);
		result = 31 * result + (skinData != null ? skinData.hashCode() : 0);
		result = 31 * result + (binding != null ? binding.hashCode() : 0);
		result = 31 * result + boundTo.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return "BankItemData{" +
				"api='" + api + '\'' +
				", itemData=" + itemData +
				", skinData=" + skinData +
				", binding=" + binding +
				", boundTo='" + boundTo + '\'' +
				'}';
	}
}
