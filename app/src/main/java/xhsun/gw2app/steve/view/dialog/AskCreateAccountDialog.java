package xhsun.gw2app.steve.view.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;

import xhsun.gw2app.steve.util.constant.RequestCode;

/**
 * @author xhsun
 * @since 2017-02-14
 */

public class AskCreateAccountDialog extends DialogFragment {
	private Fragment target;

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("No Account Present")
				.setMessage("Do you want to register a account?")
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						AskCreateAccountDialog.this.dismiss();
						promptAddAccount();
					}
				})
				.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						AskCreateAccountDialog.this.dismiss();
					}
				});
		return builder.create();
	}

	public void setTarget(Fragment target) {
		this.target = target;
	}

	//display add account dialog
	private void promptAddAccount() {
		RequestCode.showCreateAccount(target, getFragmentManager());
	}
}
