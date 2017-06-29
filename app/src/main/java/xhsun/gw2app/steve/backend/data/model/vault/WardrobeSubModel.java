package xhsun.gw2app.steve.backend.data.model.vault;

import java.util.ArrayList;
import java.util.List;

import xhsun.gw2app.steve.backend.data.model.AbstractModel;
import xhsun.gw2app.steve.backend.data.model.vault.item.WardrobeItemModel;

/**
 * {@link AbstractModel} for wardrobe sub categories
 *
 * @author xhsun
 * @since 2017-05-18
 */

public class WardrobeSubModel extends AbstractModel {
	private String api;
	private int count, order;
	private List<WardrobeItemModel> items;

	public WardrobeSubModel(String subType) {
		super(subType);
		items = new ArrayList<>();
	}

	public WardrobeSubModel(String subType, int order) {
		super(subType);
		this.order = order;
		items = new ArrayList<>();
	}

	public WardrobeSubModel(String api, String subType, int count) {
		super(subType);
		this.api = api;
		this.count = count;
		items = new ArrayList<>();
	}

	public String getSubTypeName() {
		return name;
	}

	public List<WardrobeItemModel> getItems() {
		return items;
	}

	public void setItems(List<WardrobeItemModel> items) {
		this.items = items;
	}

	public void setApi(String api) {
		this.api = api;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getOrder() {
		return order;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		WardrobeSubModel that = (WardrobeSubModel) o;

		return count == that.count &&
				(api != null ? api.equals(that.api) : that.api == null) &&
				(name != null ? name.equals(that.name) : that.name == null);
	}

	@Override
	public int hashCode() {
		int result = api != null ? api.hashCode() : 0;
		result = 31 * result + (name != null ? name.hashCode() : 0);
		result = 31 * result + count;
		return result;
	}

	@Override
	public String toString() {
		return "WardrobeSubModel{" +
				"SubCategory='" + name + '\'' +
				", itemSize=" + ((items == null) ? 0 : items.size()) +
				'}';
	}
}
