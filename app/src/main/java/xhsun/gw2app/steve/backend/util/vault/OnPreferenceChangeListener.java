package xhsun.gw2app.steve.backend.util.vault;

import java.util.Set;

/**
 * listener for changes on preference
 *
 * @author xhsun
 * @since 2017-05-09
 */

public interface OnPreferenceChangeListener<T> {
	void notifyPreferenceChange(VaultType type, Set<T> result);
}
