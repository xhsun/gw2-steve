package xhsun.gw2app.steve.backend.data;

import java.util.Arrays;

import xhsun.gw2api.guildwars2.model.Item;
import xhsun.gw2api.guildwars2.model.Skin;
import xhsun.gw2api.guildwars2.model.util.itemDetail.ItemDetail;

/**
 * skin data type
 *
 * @author xhsun
 * @since 2017-05-04
 */

public class SkinInfo {
	private long id;
	private String name;
	private Item.Type type;
	private ItemDetail.Type subType;
	private boolean isOverride;
	private Item.Restriction[] restriction;
	private String icon;
	private Item.Rarity rarity;
	private String description;

	public SkinInfo(long id) {
		this.id = id;
	}

	public SkinInfo(Skin skin) {
		id = skin.getId();
		name = skin.getName();
		type = skin.getType();
		if (skin.getDetails() != null) subType = skin.getDetails().getType();
		restriction = (skin.getRestrictions() == null) ? new Item.Restriction[0] : skin.getRestrictions();
		icon = skin.getIcon();
		rarity = skin.getRarity();
		description = (skin.getDescription() == null) ? "" : skin.getDescription();
		isOverride = containOverride(skin.getFlags());
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Item.Type getType() {
		return type;
	}

	public void setType(Item.Type type) {
		this.type = type;
	}

	public ItemDetail.Type getSubType() {
		return subType;
	}

	public void setSubType(ItemDetail.Type subType) {
		this.subType = subType;
	}

	public boolean isOverride() {
		return isOverride;
	}

	public void setOverride(boolean override) {
		isOverride = override;
	}

	public Item.Restriction[] getRestriction() {
		return restriction;
	}

	public void setRestriction(Item.Restriction[] restriction) {
		this.restriction = restriction;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}


	public static boolean containOverride(Skin.Flag[] flags) {
		for (Skin.Flag f : flags) {
			if (f == Skin.Flag.OverrideRarity) return true;
		}
		return false;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		SkinInfo skinInfo = (SkinInfo) o;

		return id == skinInfo.id;

	}

	@Override
	public int hashCode() {
		int result = (int) (id ^ (id >>> 32));
		result = 31 * result + name.hashCode();
		result = 31 * result + type.hashCode();
		result = 31 * result + (isOverride ? 1 : 0);
		result = 31 * result + Arrays.hashCode(restriction);
		result = 31 * result + icon.hashCode();
		result = 31 * result + rarity.hashCode();
		result = 31 * result + description.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return "SkinInfo{" +
				"id=" + id +
				", name='" + name + '\'' +
				'}';
	}
}
