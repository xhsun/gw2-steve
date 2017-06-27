package xhsun.gw2app.steve.backend.data.model.vault;

import java.util.ArrayList;
import java.util.List;

import xhsun.gw2app.steve.backend.data.model.AbstractModel;

/**
 * {@link AbstractModel} for wardrobe
 *
 * @author xhsun
 * @since 2017-05-18
 */

public class WardrobeModel extends AbstractModel {
	private WardrobeType type;
	private List<WardrobeSubModel> data;

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

	public enum WardrobeType {
		Armor, Weapon, Backpack, Misc
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		WardrobeModel that = (WardrobeModel) o;

		return type == that.type;

	}

	@Override
	public int hashCode() {
		return type.hashCode();
	}

	@Override
	public String toString() {
		return "WardrobeModel{" +
				"type=" + type +
				", data=" + data +
				'}';
	}
}
