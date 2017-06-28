package xhsun.gw2app.steve.backend.data.model;

/**
 * misc item data class
 * for things such as outfit and mini
 *
 * @author xhsun
 * @since 2017-06-27
 */

public class MiscItemModel {
	public static final String SPLIT = "/";

	public enum MiscItemType {GLIDER, MAILCARRIER, MINI, OUTFIT, FINISHER}

	private int id;
	private MiscItemType type;
	private String name, icon;

	public MiscItemModel(MiscItemType type, int id) {
		this.id = id;
		this.type = type;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCombinedID() {
		return formatID(type, id);
	}

	public MiscItemType getType() {
		return type;
	}

	public void setType(MiscItemType type) {
		this.type = type;
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

	public static String formatID(MiscItemModel.MiscItemType type, int id) {
		return type.name() + SPLIT + id;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		MiscItemModel that = (MiscItemModel) o;

		return id == that.id && type == that.type;
	}

	@Override
	public int hashCode() {
		int result = id;
		result = 31 * result + (type != null ? type.hashCode() : 0);
		return result;
	}
}
