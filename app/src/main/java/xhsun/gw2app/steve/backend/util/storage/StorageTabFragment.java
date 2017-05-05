package xhsun.gw2app.steve.backend.util.storage;

import xhsun.gw2app.steve.backend.util.items.StorageContentFragment;
import xhsun.gw2app.steve.backend.util.items.StorageType;

/**
 * template fragment for storage tab content
 *
 * @author xhsun
 * @since 2017-05-04
 */

public abstract class StorageTabFragment<A> extends StorageContentFragment<A, StorageType> {
	protected StorageTabContentSupport provider;
	protected boolean isLoading = false, isMoreDataAvailable = true, isRefresh = false;
	protected String query = "";

	public void setProvider(StorageTabContentSupport provider) {
		this.provider = provider;
	}

	public abstract void notifyAccountUpdate();

	protected abstract void onListRefresh();
}
