package xhsun.gw2app.steve.view.dialog.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import xhsun.gw2app.steve.R;
import xhsun.gw2app.steve.backend.util.dialog.AddAccountListener;
import xhsun.gw2app.steve.view.dialog.DialogManager;

/**
 * Dialog for prompt user to add an account
 *
 * @author xhsun
 * @since 2017-02-14
 */

public class PromptAddAccount extends DialogFragment {
	@BindView(R.id.dialog_alert_title)
	TextView title;
	@BindView(R.id.dialog_alert_content)
	TextView content;

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		View view = View.inflate(getContext(), R.layout.dialog_alert_template, null);
		ButterKnife.bind(this, view);

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setView(view);

		title.setText(R.string.dialog_prompt_title);
		content.setText(R.string.dialog_prompt_content);

		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				PromptAddAccount.this.dismiss();
				new DialogManager(getFragmentManager()).addAccount((AddAccountListener) getTargetFragment());
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
