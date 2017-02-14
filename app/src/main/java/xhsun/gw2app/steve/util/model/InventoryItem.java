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
}
