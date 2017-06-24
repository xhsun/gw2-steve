package xhsun.gw2app.steve.backend.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Currency data type
 *
 * @author xhsun
 * @since 2017-03-27
 */

public class CurrencyData {
	private List<WalletData> total;
	private int id;
	private String name;
	private String icon;

	public CurrencyData(int id) {
		this.id = id;
		total = new ArrayList<>();
	}

	public CurrencyData() {
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

	public void setTotal(List<WalletData> total) {
		this.total = total;
	}

	public long getTotalValue() {
		long value = 0;
		for (WalletData w : total) value += w.getValue();
		return value;
	}

	@Override
	public int hashCode() {
		return id;
	}

	@Override
	public boolean equals(Object obj) {
		return this == obj || obj != null && getClass() == obj.getClass() && ((CurrencyData) obj).getId() == id;
	}

	public List<WalletData> getChildList() {
		return total;
	}
}
