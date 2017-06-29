package xhsun.gw2app.steve.backend.util.items.checkbox;

import xhsun.gw2app.steve.backend.data.model.dialog.AbstractSelectModel;

/**
 * for notifying listeners able checkbox click event
 *
 * @author xhsun
 * @since 2017-05-16
 */

interface OnCheckboxClicked {
	/**
	 * notify checkbox clicked
	 *
	 * @param holder data in checkbox
	 */
	void notifyClicked(AbstractSelectModel holder);
}
