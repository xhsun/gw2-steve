package xhsun.gw2app.steve.backend.util.items;

import java.util.List;

import xhsun.gw2app.steve.backend.util.dialog.AccountHolder;

/**
 * Created by hannah on 03/05/17.
 */

public interface OnPreferenceChangeListener {
	/**
	 * modify preference base on user selection
	 *
	 * @param holders list of updated preference
	 */
	void setPreference(List<AccountHolder> holders);
}
