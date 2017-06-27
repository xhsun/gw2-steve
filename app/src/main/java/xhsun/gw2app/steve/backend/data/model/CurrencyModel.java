package xhsun.gw2app.steve.backend.data.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Currency data type
 *
 * @author xhsun
 * @since 2017-03-27
 */

public class CurrencyModel {
	private List<WalletModel> total;
	private int id;
	private String name;
	private String icon;

	public CurrencyModel(int id) {
		this.id = id;
		total = new ArrayList<>();
	}

	public CurrencyModel() {
		total = new ArrayList<>();
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

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public void setTotal(List<WalletModel> total) {
		this.total = total;
	}

	public long getTotalValue() {
		long value = 0;
		for (WalletModel w : total) value += w.getValue();
		return value;
	}

	@Override
	public int hashCode() {
		return id;
	}

	@Override
	public boolean equals(Object obj) {
		return this == obj || obj != null && getClass() == obj.getClass() && ((CurrencyModel) obj).getId() == id;
	}

	public List<WalletModel> getChildList() {
		return total;
	}
}
