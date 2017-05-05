package xhsun.gw2app.steve.backend.database.storage;

import xhsun.gw2api.guildwars2.model.account.Bank;
import xhsun.gw2api.guildwars2.model.account.Material;
import xhsun.gw2api.guildwars2.model.util.Inventory;
import xhsun.gw2api.guildwars2.model.util.Storage;
import xhsun.gw2app.steve.backend.database.common.ItemInfo;
import xhsun.gw2app.steve.backend.database.common.SkinInfo;

/**
 * Storage data type
 *
 * @author xhsun
 * @since 2017-03-29
 */

public class StorageInfo {
	private long id = -1;
	private ItemInfo itemInfo;
	private SkinInfo skinInfo;
	private String characterName = "";
	private String api = "";
	private long count = -1;
	private long categoryID = -1;
	private String categoryName = "";
	private Storage.Binding binding;//null if no binding
	private String boundTo = "";

	StorageInfo() {
	}

	StorageInfo(long skinID, String api) {
		skinInfo = new SkinInfo(skinID);
		this.api = api;
	}

	StorageInfo(Material material, String api) {
		itemInfo = new ItemInfo(material.getItemId());
		count = material.getCount();
		this.api = api;
		binding = material.getBinding();
		categoryID = material.getCategory();
	}

	StorageInfo(Bank bank, String api) {
		itemInfo = new ItemInfo(bank.getItemId());
		skinInfo = new SkinInfo(bank.getSkinId());
		count = bank.getCount() * ((bank.getCharges() < 1) ? 1 : bank.getCharges());
		this.api = api;
		binding = bank.getBinding();
		boundTo = (bank.getBound_to() == null) ? "" : bank.getBound_to();
	}

	StorageInfo(Inventory storage, String api, String name) {
		itemInfo = new ItemInfo(storage.getItemId());
		skinInfo = new SkinInfo(storage.getSkin());
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

	public ItemInfo getItemInfo() {
		return itemInfo;
	}

	public void setItemInfo(ItemInfo itemInfo) {
		this.itemInfo = itemInfo;
	}

	public SkinInfo getSkinInfo() {
		return skinInfo;
	}

	public void setSkinInfo(SkinInfo skinInfo) {
		this.skinInfo = skinInfo;
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

		StorageInfo that = (StorageInfo) o;

		return count == that.count
				&& categoryID == that.categoryID
				&& (itemInfo != null ? itemInfo.equals(that.itemInfo) : that.itemInfo == null
				&& (skinInfo != null ? skinInfo.equals(that.skinInfo) : that.skinInfo == null
				&& characterName.equals(that.characterName)
				&& api.equals(that.api)
				&& binding == that.binding
				&& boundTo.equals(that.boundTo)));
	}

	@Override
	public int hashCode() {
		int result = (int) (id ^ (id >>> 32));
		result = 31 * result + (itemInfo != null ? itemInfo.hashCode() : 0);
		result = 31 * result + (skinInfo != null ? skinInfo.hashCode() : 0);
		result = 31 * result + characterName.hashCode();
		result = 31 * result + api.hashCode();
		result = 31 * result + (int) (count ^ (count >>> 32));
		result = 31 * result + (int) (categoryID ^ (categoryID >>> 32));
		result = 31 * result + categoryName.hashCode();
		result = 31 * result + (binding != null ? binding.hashCode() : 0);
		result = 31 * result + boundTo.hashCode();
		return result;
	}
}
