package xhsun.gw2app.steve.backend.data.model;

/**
 * wallet data type
 *
 * @author xhsun
 * @since 2017-03-27
 */

public class WalletModel {
	private int currencyID;
	private String api;
	private String account;
	private String icon;
	private long value;

	public WalletModel() {
	}

	public WalletModel(int id, String api) {
		currencyID = id;
		this.api = api;
	}

	public long getValue() {
		return value;
	}

	public void setValue(long value) {
		this.value = value;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public int getCurrencyID() {
		return currencyID;
	}

	public void setCurrencyID(int currencyID) {
		this.currencyID = currencyID;
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
		if (o == null || getClass() != o.getClass()) return false;

		WalletModel that = (WalletModel) o;

		return currencyID == that.currencyID && (api != null ? api.equals(that.api) : that.api == null);
	}

	@Override
	public int hashCode() {
		int result = currencyID;
		result = 31 * result + (api != null ? api.hashCode() : 0);
		return result;
	}
}
