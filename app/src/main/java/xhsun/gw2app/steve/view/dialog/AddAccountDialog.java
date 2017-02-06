package xhsun.gw2app.steve.view.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;

import xhsun.gw2app.steve.R;
import xhsun.gw2app.steve.misc.RequestCode;
import xhsun.gw2app.steve.view.account.AccountFragment;

/**
 * @author xhsun
 * @since 2017-02-06
 */

public class AddAccountDialog extends DialogFragment {

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();

		builder.setView(inflater.inflate(R.layout.dialog_add_account, null))
				.setPositiveButton(R.string.dialog_add_confirm, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						onPositiveClick();
					}
				})
				.setNegativeButton(R.string.dialog_add_cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						AddAccountDialog.this.getDialog().cancel();
					}
				});

		return builder.create();
	}

	private void onPositiveClick() {
		switch (getTargetRequestCode()) {
			case RequestCode.ACCOUNT:
				AccountFragment fragment = (AccountFragment) getTargetFragment();
				fragment.onPositiveClick();
				break;
			default:
				//uhhh
		}
	}
}
