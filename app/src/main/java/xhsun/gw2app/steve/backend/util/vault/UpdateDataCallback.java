package xhsun.gw2app.steve.backend.util.vault;

import xhsun.gw2app.steve.backend.data.AbstractData;

/**
 * callback for when data is updated using the server
 *
 * @author xhsun
 * @since 2017-05-14
 */

interface UpdateDataCallback {
	void updateData(AbstractData data);

	void refreshData(AbstractData data);
}
