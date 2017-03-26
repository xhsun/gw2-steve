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
import xhsun.gw2app.steve.backend.database.account.AccountInfo;

/**
 * Dialog to display account detail
 *
 * @author xhsun
 * @since 2017-02-11
 */

public class ShowAccountDetail extends DialogFragment {
	private AccountInfo account;
	@BindView(R.id.dialog_detail_name)
	TextView name;
	@BindView(R.id.dialog_detail_access)
	TextView access;
	@BindView(R.id.dialog_detail_world)
	TextView world;
	@BindView(R.id.dialog_detail_api)
	TextView api;

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		View view = View.inflate(getContext(), R.layout.dialog_account_detail, null);
		ButterKnife.bind(this, view);

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setView(view);

		name.setText(account.getName());
		access.setText(account.getAccess());
		world.setText(account.getWorld());
		api.setText(account.getAPI());

		builder.setNeutralButton(R.string.dialog_detail_ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				ShowAccountDetail.this.getDialog().dismiss();
			}
		});

		return builder.create();
	}

	public void setAccountInfo(AccountInfo account) {
		this.account = account;
	}
}
