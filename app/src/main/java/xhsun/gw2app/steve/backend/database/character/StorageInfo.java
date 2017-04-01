package xhsun.gw2app.steve.backend.database.character;

import xhsun.gw2api.guildwars2.model.util.Inventory;
import xhsun.gw2api.guildwars2.model.util.Storage;
import xhsun.gw2app.steve.backend.database.common.ItemInfo;

/**
 * Storage data type
 *
 * @author xhsun
 * @since 2017-03-29
 */

public class StorageInfo {
	private long id = -1;
	private ItemInfo itemInfo;
	private String characterName = "";
	private String api = "";
	private long count;
	private String categoryName = "";
	private Storage.Binding binding;//null if no binding
	private String boundTo = "";

	StorageInfo() {
	}

	StorageInfo(Storage storage, String api, String value, boolean isBank) {
		itemInfo = new ItemInfo(storage.getItemId());
		count = storage.getCount();
		this.api = api;
		if (!isBank) {
			characterName = value;
			binding = ((Inventory) storage).getBinding();
			boundTo = (((Inventory) storage).getBound_to() == null) ? "" : ((Inventory) storage).getBound_to();
		} else {
			categoryName = value;
			//TODO other things for bank item
		}
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
	public boolean equals(Object obj) {
		return this == obj || obj != null && getClass() == obj.getClass()
				&& ((StorageInfo) obj).itemInfo.equals(itemInfo)
				&& ((StorageInfo) obj).boundTo.equals(boundTo)
				&& ((StorageInfo) obj).characterName.equals(characterName)
				&& ((StorageInfo) obj).api.equals(api)
				&& ((StorageInfo) obj).categoryName.equals(categoryName);
	}
}
