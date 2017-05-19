package xhsun.gw2app.steve.backend.data;

/**
 * @author xhsun
 * @since 2017-05-09
 */

public abstract class AbstractData {
	protected String name = "";

	protected AbstractData(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public abstract boolean equals(Object o);

	public abstract int hashCode();

	public abstract String toString();
}
