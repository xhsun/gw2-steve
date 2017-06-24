package xhsun.gw2app.steve.backend.data.vault;

import java.util.ArrayList;
import java.util.List;

import xhsun.gw2app.steve.backend.data.AbstractData;

/**
 * {@link AbstractData} for wardrobe
 *
 * @author xhsun
 * @since 2017-05-18
 */

public class WardrobeData extends AbstractData {
	private WardrobeType type;
	private List<WardrobeSubData> data;

	public WardrobeData(WardrobeType type) {
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

	public List<WardrobeSubData> getData() {
		return data;
	}

	public void setData(List<WardrobeSubData> data) {
		this.data = data;
	}

	public enum WardrobeType {
		Armor, Weapon, Backpack, Misc
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		WardrobeData that = (WardrobeData) o;

		return type == that.type;

	}

	@Override
	public int hashCode() {
		return type.hashCode();
	}

	@Override
	public String toString() {
		return "WardrobeData{" +
				"type=" + type +
				", data=" + data +
				'}';
	}
}
