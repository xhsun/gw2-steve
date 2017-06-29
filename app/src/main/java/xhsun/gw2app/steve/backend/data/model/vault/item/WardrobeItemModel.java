package xhsun.gw2app.steve.backend.data.model.vault.item;

import me.xhsun.guildwars2wrapper.model.v2.util.comm.Type;
import me.xhsun.guildwars2wrapper.model.v2.util.itemDetail.ItemDetail;
import xhsun.gw2app.steve.backend.data.model.MiscItemModel;
import xhsun.gw2app.steve.backend.data.model.SkinModel;
import xhsun.gw2app.steve.backend.data.model.vault.WardrobeModel;

/**
 * {@link VaultItemModel} for wardrobe items
 *
 * @author xhsun
 * @since 2017-05-18
 */

public class WardrobeItemModel extends VaultItemModel implements Countable {
	private WardrobeModel.WardrobeType type;
	private int order = 0;
	private long count = -1;

	public WardrobeItemModel() {
		super("");
	}

	public WardrobeItemModel(String api, int skinID) {
		super("");
		this.api = api;
		skinModel = new SkinModel(skinID);
	}

	public WardrobeItemModel(String api, MiscItemModel.MiscItemType type, int miscID) {
		super("");
		this.api = api;
		miscItem = new MiscItemModel(type, miscID);
	}

	public WardrobeModel.WardrobeType getCategoryType() {
		return type;
	}

	public String getSubCategoryName() {
		return name;
	}

	public void setSkin(SkinModel skinModel) {
		if (skinModel == null) return;
		this.skinModel = skinModel;
		if (skinModel.getType() == null) return;
		switch (skinModel.getType()) {
			case Armor:
				type = WardrobeModel.WardrobeType.Armor;
				setName(parseArmor(skinModel.getWeightClass(), skinModel.getSubType()));
				order = orderArmor(skinModel.getWeightClass(), skinModel.getSubType());
				break;
			case Weapon:
				type = WardrobeModel.WardrobeType.Weapon;
				setName(skinModel.getSubType().name());
				break;
			case Back:
				type = WardrobeModel.WardrobeType.Backpack;
				setName(" ");
				break;
			default:
				type = WardrobeModel.WardrobeType.Misc;
				setName(type.name());
				break;
		}
	}

	public void setMiscItem(MiscItemModel miscItem) {
		if (miscItem == null) return;
		this.miscItem = miscItem;
		switch (miscItem.getType()) {
			case GLIDER:
				type = WardrobeModel.WardrobeType.Misc;
				setName("Gliders");
				break;
			case MAILCARRIER:
				type = WardrobeModel.WardrobeType.Misc;
				setName("Mail Carriers");
				break;
			case MINI:
				type = WardrobeModel.WardrobeType.Mini;
				setName("Minis");
				break;
			case OUTFIT:
				type = WardrobeModel.WardrobeType.Outfit;
				setName("Outfits");
				break;
			case FINISHER:
				type = WardrobeModel.WardrobeType.Misc;
				setName("Finishers");
				break;
			default:
				type = WardrobeModel.WardrobeType.Misc;
				setName("Misc");
				break;
		}
	}

	@Override
	public long getCount() {
		return count;
	}

	@Override
	public void setCount(long count) {
		this.count = count;
	}

	public int getOrder() {
		return order;
	}

	private String parseArmor(ItemDetail.Weight weight, Type type) {
		if (type != Type.HelmAquatic) return weight.name() + " " + type.name();
		else return weight.name() + " Breathers";
	}

	private void setName(String name) {
		this.name = name;
	}

	private int orderArmor(ItemDetail.Weight weight, Type type) {
		//TODO wait for next version of gw2wrapper
//		List<ItemDetail.Weight> weightOrder= Arrays.asList(ItemDetail.Weight.values());
//		List<Type> typeOrder=Arrays.asList(Type.values());
//		return weightOrder.indexOf(weight) + typeOrder.indexOf(type)+1;
		int order;
		switch (weight) {
			case Light:
				order = 0;
				break;
			case Medium:
				order = 8;
				break;
			case Heavy:
				order = 16;
				break;
			default:
				order = 24;
				break;
		}

		switch (type) {
			case HelmAquatic:
				order += 1;
				break;
			case Helm:
				order += 2;
				break;
			case Shoulders:
				order += 3;
				break;
			case Coat:
				order += 4;
				break;
			case Gloves:
				order += 5;
				break;
			case Leggings:
				order += 6;
				break;
			default:
				order += 7;
				break;
		}
		return order;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		WardrobeItemModel that = (WardrobeItemModel) o;

		return (api != null ? api.equals(that.api) : that.api == null) &&
				(skinModel != null ? skinModel.equals(that.skinModel) : that.skinModel == null) &&
				(miscItem != null ? miscItem.equals(that.miscItem) : that.miscItem == null);
	}

	@Override
	public int hashCode() {
		int result = api != null ? api.hashCode() : 0;
		result = 31 * result + (skinModel != null ? skinModel.hashCode() : 0);
		result = 31 * result + (miscItem != null ? miscItem.hashCode() : 0);
		return result;
	}
}
