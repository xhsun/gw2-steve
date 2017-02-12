package xhsun.gw2app.steve.util.listener;

/**
 * Web history listener
 * - for change availability of back/forward button (in WikiFragment)
 * -int item can be 0 or 1
 * - 0 : back button
 * - 1 : forward button
 *
 * @author xhsun
 * @since 2017-02-03
 */

public interface WikiWebHistoryListener {
	/**
	 * enable given button
	 *
	 * @param item 0 for back button, 1 for forward button
	 */
	void switchEnable(int item);

	/**
	 * disable given button
	 *
	 * @param item 0 for back button, 1 for forward button
	 */
	void switchDisable(int item);
}
