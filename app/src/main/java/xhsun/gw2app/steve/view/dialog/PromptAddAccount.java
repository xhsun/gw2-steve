package xhsun.gw2app.steve.view.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import xhsun.gw2app.steve.backend.util.dialog.DialogManager;

/**
 * Dialog for prompt user to add an account
 *
 * @author xhsun
 * @since 2017-02-14
 */

public class PromptAddAccount extends DialogFragment {

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("No Account Present")
				.setMessage("Do you want to register an account?")
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						PromptAddAccount.this.dismiss();
						new DialogManager(getFragmentManager()).addAccount(getTargetRequestCode(), getTargetFragment());
					}
				})
				.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						PromptAddAccount.this.dismiss();
					}
				});
		return builder.create();
	}
}
