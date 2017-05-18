package xhsun.gw2app.steve.backend.data;

import xhsun.gw2api.guildwars2.model.account.Bank;
import xhsun.gw2api.guildwars2.model.account.Material;
import xhsun.gw2api.guildwars2.model.util.Inventory;
import xhsun.gw2api.guildwars2.model.util.Storage;

/**
 * Storage data type
 *
 * @author xhsun
 * @since 2017-03-29
 */

public class StorageData {
	private long id = -1;
	private ItemData itemData;
	private SkinData skinData;
	private String characterName = "";
	private String api = "";
	private long count = -1;
	private long categoryID = -1;
	private String categoryName = "";
	private Storage.Binding binding;//null if no binding
	private String boundTo = "";

	public StorageData() {
	}

	public StorageData(long skinID, String api) {
		skinData = new SkinData(skinID);
		this.api = api;
	}

	public StorageData(Material material, String api) {
		itemData = new ItemData(material.getItemId());
		count = material.getCount();
		this.api = api;
		binding = material.getBinding();
		categoryID = material.getCategory();
	}

	public StorageData(Bank bank, String api) {
		itemData = new ItemData(bank.getItemId());
		if (bank.getSkinId() != 0) skinData = new SkinData(bank.getSkinId());
		count = bank.getCount() * ((bank.getCharges() < 1) ? 1 : bank.getCharges());
		this.api = api;
		binding = bank.getBinding();
		boundTo = (bank.getBound_to() == null) ? "" : bank.getBound_to();
	}

	public StorageData(Inventory storage, String api, String name) {
		itemData = new ItemData(storage.getItemId());
		if (storage.getSkin() != 0) skinData = new SkinData(storage.getSkin());
		count = storage.getCount() * ((storage.getCharges() < 1) ? 1 : storage.getCharges());
		this.api = api;
		binding = storage.getBinding();
		boundTo = (storage.getBound_to() == null) ? "" : storage.getBound_to();
		characterName = name;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public ItemData getItemData() {
		return itemData;
	}

	public void setItemData(ItemData itemData) {
		this.itemData = itemData;
	}

	public SkinData getSkinData() {
		return skinData;
	}

	public void setSkinData(SkinData skinData) {
		this.skinData = skinData;
	}

	public String getCharacterName() {
		return characterName;
	}

	public void setCharacterName(String characterName) {
		this.characterName = characterName;
	}

	public String getApi() {
		return api;
	}

	public void setApi(String api) {
		this.api = api;
	}

	public long getCategoryID() {
		return categoryID;
	}

	public void setCategoryID(long categoryID) {
		this.categoryID = categoryID;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
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

		StorageData that = (StorageData) o;

		return categoryID == that.categoryID
				&& ((itemData != null) ? itemData.equals(that.itemData) : that.itemData == null)
				&& ((skinData != null) ? skinData.equals(that.skinData) : that.skinData == null)
				&& characterName.equals(that.characterName)
				&& api.equals(that.api)
				&& binding == that.binding
				&& boundTo.equals(that.boundTo);
	}

	@Override
	public int hashCode() {
		int result = (int) (id ^ (id >>> 32));
		result = 31 * result + (itemData != null ? itemData.hashCode() : 0);
		result = 31 * result + (skinData != null ? skinData.hashCode() : 0);
		result = 31 * result + characterName.hashCode();
		result = 31 * result + api.hashCode();
		result = 31 * result + (int) (count ^ (count >>> 32));
		result = 31 * result + (int) (categoryID ^ (categoryID >>> 32));
		result = 31 * result + categoryName.hashCode();
		result = 31 * result + (binding != null ? binding.hashCode() : 0);
		result = 31 * result + boundTo.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return "StorageData{" +
				"id=" + id +
				", itemData=" + itemData +
				", skinData=" + skinData +
				'}';
	}
}
