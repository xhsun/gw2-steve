package xhsun.gw2app.steve.backend.util.vault;

import xhsun.gw2app.steve.backend.util.items.ProgressItem;

/**
 * helper interface to provide various method to check if endless loader should load more
 *
 * @author xhsun
 * @since 2017-05-13
 */

public interface ShouldLoadCheckHelper {
	/**
	 * @return number of columns | -1 if not applicable
	 */
	int getColumns();

	/**
	 * @return {@link ProgressItem} used
	 */
	ProgressItem getProgressItem();

	/**
	 * check if there is more to load
	 *
	 * @return true if need to should more | false otherwise
	 */
	boolean shouldLoad();
}
