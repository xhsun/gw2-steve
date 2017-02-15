package xhsun.gw2app.steve.util.constant;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import xhsun.gw2app.steve.view.dialog.AddAccountDialog;

/**
 * storing request code represent each fragment
 *
 * @author xhsun
 * @since 2017-02-11
 */

public class RequestCode {
	public static final int ACCOUNT = 0;


	public static void showCreateAccount(Fragment target, FragmentManager manager) {
		AddAccountDialog add = new AddAccountDialog();
		add.setTargetFragment(target, RequestCode.ACCOUNT);
		add.show(manager, "AddAccountDialog");
	}
}
