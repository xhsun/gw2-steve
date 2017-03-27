package xhsun.gw2app.steve.backend.util.wallet;

/**
 * Representing individual's amount
 *
 * @author xhsun
 * @since 2017-03-26
 */

public class IndividualWallet {
	private long id;
	private String account;
	private String icon;
	private long value;

	public IndividualWallet(long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public long getValue() {
		return value;
	}

	public void setValue(long value) {
		this.value = value;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}
}
