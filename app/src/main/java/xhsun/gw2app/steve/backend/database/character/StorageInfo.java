package xhsun.gw2app.steve.backend.database.character;

import xhsun.gw2api.guildwars2.model.Item;
import xhsun.gw2api.guildwars2.model.ItemStats;
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
	private String characterName;
	private String api;
	private long count;
	private long skinID = -1;
	private String skinName;
	private long statsID = -1;
	private String statsName;
	private Storage.Binding binding;//null if no binding
	private String boundTo = "";
	private String chatLink;
	private String icon;
	private String description;
	private Item.Rarity rarity;
	private int level;

	public StorageInfo() {
	}

	StorageInfo(String name, long itemID, long skinID, ItemStats itemStats, String boundTo) {
		characterName = name;
		this.itemID = itemID;
		this.skinID = skinID;
		if (itemStats != null) statsID = itemStats.getId();
		if (boundTo != null) this.boundTo = boundTo;
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

	public String getSkinName() {
		return skinName;
	}

	public void setSkinName(String skinName) {
		this.skinName = skinName;
	}

	public String getStatsName() {
		return statsName;
	}

	public void setStatsName(String statsName) {
		this.statsName = statsName;
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
		return this == obj || obj != null && getClass() == obj.getClass() && ((StorageInfo) obj).itemID == itemID
				&& ((StorageInfo) obj).boundTo.equals(boundTo) && ((StorageInfo) obj).skinID == skinID
				&& ((StorageInfo) obj).statsID == statsID && ((StorageInfo) obj).characterName.equals(characterName);
	}
}
