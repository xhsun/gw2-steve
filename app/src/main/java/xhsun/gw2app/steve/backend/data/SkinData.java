package xhsun.gw2app.steve.backend.data;

import java.util.ArrayList;
import java.util.List;

import me.xhsun.guildwars2wrapper.model.v2.Item;
import me.xhsun.guildwars2wrapper.model.v2.Skin;
import me.xhsun.guildwars2wrapper.model.v2.util.comm.Type;


/**
 * skin data type
 *
 * @author xhsun
 * @since 2017-05-04
 */

public class SkinData {
	private int id;
	private String name;
	private Item.Type type;
	private Type subType;
	private boolean isOverride;
	private List<Item.Restriction> restriction;
	private String icon;
	private Item.Rarity rarity;
	private String description;

	public SkinData(int id) {
		this.id = id;
	}

	public SkinData(Skin skin) {
		id = skin.getId();
		name = skin.getName();
		type = skin.getType();
		if (skin.getDetails() != null) subType = skin.getDetails().getType();
		restriction = (skin.getRestrictions() == null) ? new ArrayList<>() : skin.getRestrictions();
		icon = skin.getIcon();
		rarity = skin.getRarity();
		description = (skin.getDescription() == null) ? "" : skin.getDescription();
		isOverride = containOverride(skin.getFlags());
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

	public Item.Type getType() {
		return type;
	}

	public void setType(Item.Type type) {
		this.type = type;
	}

	public Type getSubType() {
		return subType;
	}

	public void setSubType(Type subType) {
		this.subType = subType;
	}

	public boolean isOverride() {
		return isOverride;
	}

	public void setOverride(boolean override) {
		isOverride = override;
	}

	public List<Item.Restriction> getRestriction() {
		return restriction;
	}

	public void setRestriction(List<Item.Restriction> restriction) {
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


	public static boolean containOverride(List<Skin.Flag> flags) {
		return flags.contains(Skin.Flag.OverrideRarity);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		SkinData skinData = (SkinData) o;

		return id == skinData.id;
	}

	@Override
	public int hashCode() {
		return id;
	}

	@Override
	public String toString() {
		return "SkinData{" +
				"id=" + id +
				", name='" + name + '\'' +
				'}';
	}
}
