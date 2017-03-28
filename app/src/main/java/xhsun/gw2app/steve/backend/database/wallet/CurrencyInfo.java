package xhsun.gw2app.steve.backend.database.wallet;

import java.util.ArrayList;
import java.util.List;

/**
 * Currency data type
 *
 * @author xhsun
 * @since 2017-03-27
 */

public class CurrencyInfo {
	private List<WalletInfo> total;
	private long id;
	private String name;
	private String icon;

	public CurrencyInfo() {
		total = new ArrayList<>();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
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

	public List<WalletInfo> getTotal() {
		return total;
	}

	public void setTotal(List<WalletInfo> total) {
		this.total = total;
	}

	public long getTotalValue() {
		long value = 0;
		for (WalletInfo w : total) value += w.getValue();
		return value;
	}

	@Override
	public int hashCode() {
		return (int) id;
	}

	@Override
	public boolean equals(Object obj) {
		return this == obj || obj != null && getClass() == obj.getClass() && ((CurrencyInfo) obj).getId() == id;
	}
}
