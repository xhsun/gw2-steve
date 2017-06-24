package xhsun.gw2app.steve.backend.util.support.vault.load;

import xhsun.gw2app.steve.backend.data.model.AbstractModel;

/**
 * callback for when data is updated using the server
 *
 * @author xhsun
 * @since 2017-05-14
 */

interface UpdateDataCallback {
	void updateData(AbstractModel data);

	void refreshData(AbstractModel data);
}
