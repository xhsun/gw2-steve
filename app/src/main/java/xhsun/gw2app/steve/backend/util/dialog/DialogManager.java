package xhsun.gw2app.steve.backend.util.dialog;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import xhsun.gw2app.steve.backend.database.account.AccountInfo;
import xhsun.gw2app.steve.backend.util.AddAccountListener;
import xhsun.gw2app.steve.backend.util.items.OnPreferenceModifyListener;
import xhsun.gw2app.steve.view.dialog.AddAccount;
import xhsun.gw2app.steve.view.dialog.CustomAlertDialog;
import xhsun.gw2app.steve.view.dialog.PromptAddAccount;
import xhsun.gw2app.steve.view.dialog.SelectCharacterInventory;
import xhsun.gw2app.steve.view.dialog.ShowAccountDetail;

/**
 * Dialog manager for account related dialogs
 *
 * @author xhsun
 * @since 2017-03-20
 */

public class DialogManager {
	private int code = 0;
	private FragmentManager manager;

	public DialogManager(FragmentManager manager) {
		this.manager = manager;
	}

	/**
	 * create and show add account dialog
	 *
	 * @param fragment target fragment that implements add account listener
	 */
	public void addAccount(AddAccountListener fragment) {
		AddAccount dialog = new AddAccount();
		dialog.setTargetFragment((Fragment) fragment, code);
		dialog.show(manager, "AddAccountDialog");
	}

	/**
	 * create and show prompt add account dialog
	 *
	 * @param fragment target fragment that implements add account listener
	 */
	public void promptAdd(AddAccountListener fragment) {
		PromptAddAccount dialog = new PromptAddAccount();
		dialog.setTargetFragment((Fragment) fragment, code);
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

	/**
	 * create and show select character inventory dialog
	 *
	 * @param listener for set preference callback
	 */
	public void selectCharacterInventory(OnPreferenceModifyListener listener) {
		SelectCharacterInventory dialog = new SelectCharacterInventory();
		dialog.setAccounts(listener);
		dialog.show(manager, "SelectCharacterInventoryDialog");
	}

	public void customAlert(String title, String content, CustomAlertDialogListener listener) {
		CustomAlertDialog dialog = new CustomAlertDialog();
		dialog.setListener(listener);
		dialog.setTitle(title);
		dialog.setContent(content);
		dialog.show(manager, "customAlertDialog");
	}
}
