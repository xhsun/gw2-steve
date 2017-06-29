package xhsun.gw2app.steve.backend.data.model.dialog;

/**
 * data holder for selecting account
 *
 * @author xhsun
 * @since 2017-05-03
 */

public class SelectAccountModel extends AbstractSelectModel {
	private String api;

	public SelectAccountModel(String name, String api, boolean isSelected) {
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
		if (!(o instanceof SelectAccountModel)) return false;

		SelectAccountModel that = (SelectAccountModel) o;

		return api.equals(that.api) && name.equals(that.name);
	}

	@Override
	public int hashCode() {
		int result = api.hashCode();
		result = 31 * result + name.hashCode();
		return result;
	}
}
