package xhsun.gw2app.steve.backend.data.model.vault;

import java.util.List;

import xhsun.gw2app.steve.backend.data.model.AbstractModel;
import xhsun.gw2app.steve.backend.data.model.vault.item.BankItemModel;

/**
 * For formatting bank into sections
 *
 * @author xhsun
 * @since 2017-06-27
 */

public class BankSectionModel extends AbstractModel {
	private String api;
	private int count;
	private List<BankItemModel> items;

	public BankSectionModel(String api, int count) {
		super("Bank Tab");
		this.api = api;
		this.count = count;
	}

	public List<BankItemModel> getItems() {
		return items;
	}

	public void setItems(List<BankItemModel> items) {
		this.items = items;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		BankSectionModel that = (BankSectionModel) o;

		return count == that.count && (api != null ? api.equals(that.api) : that.api == null);

	}

	@Override
	public int hashCode() {
		int result = api != null ? api.hashCode() : 0;
		result = 31 * result + count;
		return result;
	}
}
