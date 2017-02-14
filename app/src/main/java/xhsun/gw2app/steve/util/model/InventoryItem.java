package xhsun.gw2app.steve.util.model;

import xhsun.gw2api.guildwars2.model.util.Item;
import xhsun.gw2api.guildwars2.model.util.Storage;

/**
 * @author xhsun
 * @since 2017-02-14
 */

public class InventoryItem {
	private long id;
	private int count;
	private String chat_link;
	private String name;
	private String icon;
	private String description;
	private Item.Rarity rarity;
	private long vendor_value;
	private int level;
	private Storage.Binding binding;
	private String bound_to;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getChat_link() {
		return chat_link;
	}

	public void setChat_link(String chat_link) {
		this.chat_link = chat_link;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public long getVendor_value() {
		return vendor_value;
	}

	public void setVendor_value(long vendor_value) {
		this.vendor_value = vendor_value;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public Storage.Binding getBinding() {
		return binding;
	}

	public void setBinding(Storage.Binding binding) {
		this.binding = binding;
	}

	public String getBound_to() {
		return bound_to;
	}

	public void setBound_to(String bound_to) {
		this.bound_to = bound_to;
	}
}
