package xhsun.gw2app.steve.backend.util.dialog.select.selectAccount;

import xhsun.gw2app.steve.backend.util.dialog.select.Holder;

/**
 * data holder for selecting account
 *
 * @author xhsun
 * @since 2017-05-03
 */

public class SelectAccAccountHolder extends Holder {
	private String api;

	public SelectAccAccountHolder(String name, String api, boolean isSelected) {
		super(name, isSelected);
		this.api = api;
	}

	public String getApi() {
		return api;
	}

	public void setApi(String api) {
		this.api = api;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof SelectAccAccountHolder)) return false;

		SelectAccAccountHolder that = (SelectAccAccountHolder) o;

		return api.equals(that.api) && name.equals(that.name);
	}

	@Override
	public int hashCode() {
		int result = api.hashCode();
		result = 31 * result + name.hashCode();
		return result;
	}
}
