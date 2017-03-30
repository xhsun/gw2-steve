package xhsun.gw2app.steve.backend.database.character;

import xhsun.gw2api.guildwars2.model.Item;
import xhsun.gw2api.guildwars2.model.util.Storage;

/**
 * Storage data type
 *
 * @author xhsun
 * @since 2017-03-29
 */

public class StorageInfo {
	private long id;
	private long itemID;
	private String itemName;
	private String characterName = "";
	private String api = "";
	private long count;
	private String categoryName = "";
	private Storage.Binding binding;//null if no binding
	private String boundTo = "";
	private String chatLink;
	private String icon;
	private String description;
	private Item.Rarity rarity;
	private int level;

	public StorageInfo() {
	}

	StorageInfo(String api, long itemID, String boundTo, String value, boolean isBank) {
		this.api = api;
		this.itemID = itemID;
		if (boundTo != null) this.boundTo = boundTo;
		if (isBank) this.categoryName = value;
		else this.characterName = value;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getItemID() {
		return itemID;
	}

	public void setItemID(long itemID) {
		this.itemID = itemID;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
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

	public String getChatLink() {
		return chatLink;
	}

	public void setChatLink(String chatLink) {
		this.chatLink = chatLink;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Item.Rarity getRarity() {
		return rarity;
	}

	public void setRarity(Item.Rarity rarity) {
		this.rarity = rarity;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	@Override
	public boolean equals(Object obj) {
		return this == obj || obj != null && getClass() == obj.getClass()
				&& ((StorageInfo) obj).itemID == itemID
				&& ((StorageInfo) obj).boundTo.equals(boundTo)
				&& ((StorageInfo) obj).characterName.equals(characterName)
				&& ((StorageInfo) obj).api.equals(api)
				&& ((StorageInfo) obj).categoryName.equals(categoryName);
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
}
