package xhsun.gw2app.steve.backend.util.dialog;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import xhsun.gw2app.steve.backend.database.account.AccountInfo;
import xhsun.gw2app.steve.view.dialog.AddAccount;
import xhsun.gw2app.steve.view.dialog.CustomAlertDialog;
import xhsun.gw2app.steve.view.dialog.PromptAddAccount;
import xhsun.gw2app.steve.view.dialog.ShowAccountDetail;

/**
 * Dialog manager for account related dialogs
 *
 * @author xhsun
 * @since 2017-03-20
 */

public class DialogManager {
	public static final int ACCOUNT = 0;

	private FragmentManager manager;

	public DialogManager(FragmentManager manager) {
		this.manager = manager;
	}

	/**
	 * create and show add account dialog
	 *
	 * @param requestCode code of the target fragment
	 * @param fragment    target fragment
	 */
	public void addAccount(int requestCode, Fragment fragment) {
		AddAccount dialog = new AddAccount();
		dialog.setTargetFragment(fragment, requestCode);
		dialog.show(manager, "AddAccountDialog");
	}

	/**
	 * create and show prompt add account dialog
	 *
	 * @param requestCode code of the target fragment
	 * @param fragment    target fragment
	 */
	public void promptAdd(int requestCode, Fragment fragment) {
		PromptAddAccount dialog = new PromptAddAccount();
		dialog.setTargetFragment(fragment, requestCode);
		dialog.show(manager, "PromptAddAccountDialog");
	}

	/**
	 * create and show show account detail dialog
	 *
	 * @param account account info
	 */
	public void ShowAccount(AccountInfo account) {
		ShowAccountDetail dialog = new ShowAccountDetail();
		dialog.setAccountInfo(account);
		dialog.show(manager, "ShowAccountDetailDialog");
	}

	public void customAlert(String title, String content, CustomAlertDialogListener listener) {
		CustomAlertDialog dialog = new CustomAlertDialog();
		dialog.setListener(listener);
		dialog.setTitle(title);
		dialog.setContent(content);
		dialog.show(manager, "customAlertDialog");
	}
}
