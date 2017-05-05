package xhsun.gw2app.steve.backend.util.items;

import android.support.v4.app.Fragment;

import xhsun.gw2app.steve.backend.util.storage.OnPreferenceModifyListener;

/**
 * template fragment class for storage view
 *
 * @author xhsun
 * @since 2017-05-03
 */

public abstract class StorageContentFragment<A, P> extends Fragment implements StorageSearchListener,
		OnPreferenceModifyListener, OnLoadMoreListener<A, P> {
	private StorageType type;

	public void setType(StorageType type) {
		this.type = type;
	}

	public StorageType getType() {
		return type;
	}
}
