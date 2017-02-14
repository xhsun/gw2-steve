package xhsun.gw2app.steve.view.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

import xhsun.gw2app.steve.R;
import xhsun.gw2app.steve.util.model.AccountInfo;

/**
 * Dialog to display account detail
 *
 * @author xhsun
 * @since 2017-02-11
 */
public class AccountDetailDialog extends DialogFragment {
	private AccountInfo account;

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		final View view = View.inflate(getContext(), R.layout.dialog_account_detail, null);
		builder.setView(view);

		((TextView) view.findViewById(R.id.dialog_detail_name)).setText(account.getName());
		((TextView) view.findViewById(R.id.dialog_detail_access)).setText(account.getAccess());
		((TextView) view.findViewById(R.id.dialog_detail_world)).setText(account.getWorld());
		((TextView) view.findViewById(R.id.dialog_detail_api)).setText(account.getAPI());

		builder.setNeutralButton(R.string.dialog_detail_ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				AccountDetailDialog.this.getDialog().dismiss();
			}
		});

		return builder.create();
	}

	public void accountDetail(AccountInfo account) {
		this.account = account;
	}
}
