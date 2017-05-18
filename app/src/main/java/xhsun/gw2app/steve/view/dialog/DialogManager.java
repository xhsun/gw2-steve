package xhsun.gw2app.steve.view.dialog;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import java.util.List;
import java.util.Map;
import java.util.Set;

import xhsun.gw2app.steve.backend.data.AccountData;
import xhsun.gw2app.steve.backend.util.dialog.AddAccountListener;
import xhsun.gw2app.steve.backend.util.dialog.CustomAlertDialogListener;
import xhsun.gw2app.steve.backend.util.dialog.select.selectAccount.SelectAccAccountHolder;
import xhsun.gw2app.steve.backend.util.dialog.select.selectCharacter.SelectCharAccountHolder;
import xhsun.gw2app.steve.backend.util.vault.OnPreferenceChangeListener;
import xhsun.gw2app.steve.backend.util.vault.VaultType;
import xhsun.gw2app.steve.view.dialog.fragment.AddAccount;
import xhsun.gw2app.steve.view.dialog.fragment.CustomAlertDialog;
import xhsun.gw2app.steve.view.dialog.fragment.PromptAddAccount;
import xhsun.gw2app.steve.view.dialog.fragment.SelectAccounts;
import xhsun.gw2app.steve.view.dialog.fragment.SelectCharacters;
import xhsun.gw2app.steve.view.dialog.fragment.ShowAccountDetail;

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
	public void ShowAccount(AccountData account) {
		ShowAccountDetail dialog = new ShowAccountDetail();
		dialog.setAccountInfo(account);
		dialog.show(manager, "ShowAccountDetailDialog");
	}

	/**
	 * create and show select characters dialog
	 * @param listener for set preference callback
	 * @param accounts data
	 * @param preference initial preference
	 */
	public void selectCharacters(OnPreferenceChangeListener<SelectCharAccountHolder> listener,
	                             List<AccountData> accounts,
	                             Map<AccountData, Set<String>> preference) {
		SelectCharacters dialog = new SelectCharacters();
		dialog.setAccounts(listener, accounts, preference);
		dialog.show(manager, "SelectCharactersDialog");
	}

	/**
	 * create and show select accounts dialog
	 *
	 * @param listener   for set preference callback
	 * @param accounts   data
	 * @param type       vault type
	 * @param preference initial preference
	 */
	public void selectAccounts(OnPreferenceChangeListener<SelectAccAccountHolder> listener,
	                           List<AccountData> accounts, VaultType type, Set<String> preference) {
		SelectAccounts dialog = new SelectAccounts();
		dialog.setAccounts(listener, accounts, type, preference);
		dialog.show(manager, "SelectAccountsDialog");
	}

	public void customAlert(String title, String content, CustomAlertDialogListener listener) {
		CustomAlertDialog dialog = new CustomAlertDialog();
		dialog.setListener(listener);
		dialog.setTitle(title);
		dialog.setContent(content);
		dialog.show(manager, "customAlertDialog");
	}
}
