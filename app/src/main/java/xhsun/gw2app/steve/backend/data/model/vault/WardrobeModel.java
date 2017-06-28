package xhsun.gw2app.steve.backend.data.model.vault;

import java.util.ArrayList;
import java.util.List;

import xhsun.gw2app.steve.backend.data.model.AbstractModel;
import xhsun.gw2app.steve.backend.data.wrapper.storage.WardrobeWrapper;

/**
 * {@link AbstractModel} for wardrobe
 *
 * @author xhsun
 * @since 2017-05-18
 */

public class WardrobeModel extends AbstractModel {
	public enum WardrobeType {
		Armor, Backpack, Mini, Outfit, Weapon, Misc;

		public static WardrobeWrapper.SelectableType convert(WardrobeType type) {
			switch (type) {
				case Misc:
					return WardrobeWrapper.SelectableType.MISC;
				case Mini:
					return WardrobeWrapper.SelectableType.MINI;
				case Outfit:
					return WardrobeWrapper.SelectableType.OUTFIT;
				default:
					return WardrobeWrapper.SelectableType.SKIN;
			}
		}
	}
	private WardrobeType type;
	private List<WardrobeSubModel> data;
	private String api;

	public WardrobeModel(WardrobeType type) {
		super(type.name());
		this.type = type;
		data = new ArrayList<>();
	}

	public WardrobeType getType() {
		return type;
	}

	public String getTypeName() {
		return name;
	}

	public List<WardrobeSubModel> getData() {
		return data;
	}

	public void setData(List<WardrobeSubModel> data) {
		this.data = data;
	}

	public void setApi(String api) {
		this.api = api;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		WardrobeModel that = (WardrobeModel) o;

		return type == that.type && (api != null ? api.equals(that.api) : that.api == null);
	}

	@Override
	public int hashCode() {
		int result = type != null ? type.hashCode() : 0;
		result = 31 * result + (api != null ? api.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "WardrobeModel{" +
				"type=" + type +
				", data=" + data +
				'}';
	}
}
