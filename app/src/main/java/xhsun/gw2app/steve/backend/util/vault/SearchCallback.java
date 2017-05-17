package xhsun.gw2app.steve.backend.util.vault;

/**
 * Listen to key word provided by the user and filter/restore storage content accordingly
 *
 * @author xhsun
 * @since 2017-04-17
 */

public interface SearchCallback {
	/**
	 * filter storage content base on query given
	 *
	 * @param query key word
	 */
	void filter(String query);
}
