package xhsun.gw2app.steve.backend.data.model;

/**
 * @author xhsun
 * @since 2017-05-09
 */

public abstract class AbstractModel {
	protected String name = "";

	protected AbstractModel(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public abstract boolean equals(Object o);

	public abstract int hashCode();

	@Override
	public String toString() {
		return "AbstractModel{" +
				"name='" + name + '\'' +
				'}';
	}
}
