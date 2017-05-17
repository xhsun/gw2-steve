package xhsun.gw2app.steve.backend.data;

/**
 * wallet data type
 *
 * @author xhsun
 * @since 2017-03-27
 */

public class WalletInfo {
	private long currencyID;
	private String api;
	private String account;
	private String icon;
	private long value;

	public WalletInfo() {
	}

	public WalletInfo(long id, String api) {
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

	public long getCurrencyID() {
		return currencyID;
	}

	public void setCurrencyID(long currencyID) {
		this.currencyID = currencyID;
	}

	public String getApi() {
		return api;
	}

	public void setApi(String api) {
		this.api = api;
	}

	@Override
	public int hashCode() {
		return (currencyID + api).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return this == obj || obj != null && getClass() == obj.getClass() && ((WalletInfo) obj).getCurrencyID() == currencyID && ((WalletInfo) obj).getApi().equals(api);
	}
}
