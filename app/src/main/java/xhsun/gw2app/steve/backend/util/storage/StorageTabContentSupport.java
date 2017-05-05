package xhsun.gw2app.steve.backend.util.storage;

import java.util.List;
import java.util.Set;

import xhsun.gw2app.steve.backend.database.account.AccountInfo;
import xhsun.gw2app.steve.backend.util.items.StorageType;

/**
 * for provide method for tabs to function
 *
 * @author xhsun
 * @since 2017-05-04
 */

public interface StorageTabContentSupport {
	List<AccountInfo> getAccounts();

	Set<String> getPreferences(StorageType type);

	void showContent();

	void hideContent();
}
