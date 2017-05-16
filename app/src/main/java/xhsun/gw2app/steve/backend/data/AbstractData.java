package xhsun.gw2app.steve.backend.data;

/**
 * @author xhsun
 * @since 2017-05-09
 */

public abstract class AbstractData {
	protected String name;

	AbstractData(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public abstract boolean equals(Object o);
}
