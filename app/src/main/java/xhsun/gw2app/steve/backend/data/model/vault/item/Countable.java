package xhsun.gw2app.steve.backend.data.model.vault.item;

/**
 * Countable interface for {@link VaultItemModel} that can be counted
 *
 * @author xhsun
 * @since 2017-05-18
 */

public interface Countable {
	int getId();

	void setId(int id);

	long getCount();

	void setCount(long count);

	boolean equals(Object o);
}
