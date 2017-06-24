package xhsun.gw2app.steve.backend.data;


import me.xhsun.guildwars2wrapper.model.v2.Item;

/**
 * item data type
 *
 * @author xhsun
 * @since 2017-03-30
 */

public class ItemData {
	private int id;
	private String name;
	private String chatLink;
	private String icon;
	private Item.Rarity rarity;
	private int level;
	private String description;

	public ItemData(int id) {
		this.id = id;
	}

	public ItemData(Item item) {
		id = item.getId();
		name = item.getName();
		chatLink = item.getChatLink();
		icon = item.getIcon();
		rarity = item.getRarity();
		level = item.getLevel();
		description = (item.getDescription() == null) ? "" : item.getDescription();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ItemData itemData = (ItemData) o;

		return id == itemData.id;
	}

	@Override
	public int hashCode() {
		return id;
	}

	@Override
	public String toString() {
		return "ItemData{" +
				"id=" + id +
				", name='" + name + '\'' +
				'}';
	}
}
