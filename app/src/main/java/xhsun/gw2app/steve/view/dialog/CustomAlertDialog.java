package xhsun.gw2app.steve.view.dialog;

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
import xhsun.gw2app.steve.backend.util.dialog.CustomAlertDialogListener;

/**
 * Template for custom alert dialog
 *
 * @author xhsun
 * @since 2017-03-26
 */

public class CustomAlertDialog extends DialogFragment {
	private CustomAlertDialogListener listener;
	private String title_str;
	private String content_str;
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

		title.setText(title_str);
		content.setText(content_str);

		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				listener.onPositiveClick();
				CustomAlertDialog.this.dismiss();
			}
		})
				.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						listener.onNegativeClick();
						CustomAlertDialog.this.dismiss();
					}
				});
		return builder.create();
	}

	public void setListener(CustomAlertDialogListener listener) {
		this.listener = listener;
	}

	public void setTitle(String title) {
		title_str = title;
	}

	public void setContent(String content) {
		content_str = content;
	}
}
