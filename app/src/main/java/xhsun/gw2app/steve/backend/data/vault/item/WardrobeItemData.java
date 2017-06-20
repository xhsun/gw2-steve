package xhsun.gw2app.steve.backend.data.vault.item;

import xhsun.gw2app.steve.backend.data.SkinData;
import xhsun.gw2app.steve.backend.data.vault.WardrobeData;

/**
 * {@link VaultItemData} for wardrobe items
 *
 * @author xhsun
 * @since 2017-05-18
 */

public class WardrobeItemData extends VaultItemData {
	private WardrobeData.WardrobeType type;

	public WardrobeItemData() {
		super("");
	}

	public WardrobeItemData(String api, int skinID) {
		super("");
		this.api = api;
		skinData = new SkinData(skinID);
	}

	public String getCategoryName() {
		return type.name();
	}

	public WardrobeData.WardrobeType getCategoryType() {
		return type;
	}

	public String getSubCategoryName() {
		return name;
	}

	public void setSkinData(SkinData skinData) {
		this.skinData = skinData;
		if (skinData.getType() == null) return;
		switch (skinData.getType()) {
			case Armor:
				type = WardrobeData.WardrobeType.Armor;
				setName(skinData.getSubType().name());
				break;
			case Weapon:
				type = WardrobeData.WardrobeType.Weapon;
				setName(skinData.getSubType().name());
				break;
			case Back:
				type = WardrobeData.WardrobeType.Backpack;
				setName(type.name());
				break;
			default:
				type = WardrobeData.WardrobeType.Misc;
				setName(type.name());
				break;
		}
	}

	private void setName(String name) {
		this.name = name.substring(0, 1) + name.substring(1).toLowerCase();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		WardrobeItemData that = (WardrobeItemData) o;

		return api.equals(that.api) &&
				((skinData != null) ? skinData.equals(that.skinData) : that.skinData == null);
	}

	@Override
	public int hashCode() {
		int result = api.hashCode();
		result = 31 * result + (skinData != null ? skinData.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "WardrobeItemData{" +
				"api='" + api + '\'' +
				", type=" + name +
				", skinData=" + skinData +
				'}';
	}
}
