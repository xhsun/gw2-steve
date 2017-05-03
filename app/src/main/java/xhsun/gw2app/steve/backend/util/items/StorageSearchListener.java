package xhsun.gw2app.steve.backend.util.items;

/**
 * Listen to key word provided by the user and filter/restore storage content accordingly
 *
 * @author xhsun
 * @since 2017-04-17
 */

public interface StorageSearchListener {
	/**
	 * filter storage content base on query given
	 *
	 * @param query key word
	 */
	void filter(String query);

	/**
	 * restore storage content back to what it was before the search
	 */
	void restore();
}
