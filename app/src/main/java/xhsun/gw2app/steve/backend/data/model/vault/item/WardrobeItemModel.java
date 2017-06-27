package xhsun.gw2app.steve.backend.data.model.vault.item;

import xhsun.gw2app.steve.backend.data.model.SkinModel;
import xhsun.gw2app.steve.backend.data.model.vault.WardrobeModel;

/**
 * {@link VaultItemModel} for wardrobe items
 *
 * @author xhsun
 * @since 2017-05-18
 */

public class WardrobeItemModel extends VaultItemModel {
	private WardrobeModel.WardrobeType type;

	public WardrobeItemModel() {
		super("");
	}

	public WardrobeItemModel(String api, int skinID) {
		super("");
		this.api = api;
		skinModel = new SkinModel(skinID);
	}

	public String getCategoryName() {
		return type.name();
	}

	public WardrobeModel.WardrobeType getCategoryType() {
		return type;
	}

	public String getSubCategoryName() {
		return name;
	}

	public void setSkinData(SkinModel skinModel) {
		this.skinModel = skinModel;
		if (skinModel.getType() == null) return;
		switch (skinModel.getType()) {
			case Armor:
				type = WardrobeModel.WardrobeType.Armor;
				setName(skinModel.getSubType().name());
				break;
			case Weapon:
				type = WardrobeModel.WardrobeType.Weapon;
				setName(skinModel.getSubType().name());
				break;
			case Back:
				type = WardrobeModel.WardrobeType.Backpack;
				setName(type.name());
				break;
			default:
				type = WardrobeModel.WardrobeType.Misc;
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

		WardrobeItemModel that = (WardrobeItemModel) o;

		return api.equals(that.api) &&
				((skinModel != null) ? skinModel.equals(that.skinModel) : that.skinModel == null);
	}

	@Override
	public int hashCode() {
		int result = api.hashCode();
		result = 31 * result + (skinModel != null ? skinModel.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "WardrobeItemModel{" +
				"api='" + api + '\'' +
				", type=" + name +
				", skinModel=" + skinModel +
				'}';
	}
}
