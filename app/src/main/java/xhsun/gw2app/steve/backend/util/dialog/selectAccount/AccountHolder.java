package xhsun.gw2app.steve.backend.util.dialog.selectAccount;

/**
 * data holder for selecting account
 *
 * @author xhsun
 * @since 2017-05-03
 */

public class AccountHolder {
	private String api, name;
	private boolean isSelected = false;

	public String getApi() {
		return api;
	}

	public void setApi(String api) {
		this.api = api;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean selected) {
		isSelected = selected;
	}
}
