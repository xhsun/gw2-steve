package xhsun.gw2app.steve.view.dialog;

import android.app.Dialog;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import xhsun.gw2app.steve.R;
import xhsun.gw2app.steve.database.account.AccountInfo;
import xhsun.gw2app.steve.util.constant.RequestCode;
import xhsun.gw2app.steve.util.task.CreateAccountTask;
import xhsun.gw2app.steve.view.account.AccountFragment;

/**
 * dialog with input field for getting api key from user
 *
 * @author xhsun
 * @since 2017-02-06
 */

public class AddAccountDialog extends DialogFragment {
	private final String TAG = this.getClass().getSimpleName();
	private AccountInfo account;
	private TextInputEditText api;
	private TextInputLayout error;

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		final View view = View.inflate(getContext(), R.layout.dialog_add_account, null);
		builder.setView(view);

		api = (TextInputEditText) view.findViewById(R.id.dialog_add_api);
		error = (TextInputLayout) view.findViewById(R.id.dialog_add_api_wrapper);

		//remove error message if there is any
		api.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				if (error.getError() != null) {
					error.setError(null);
					api.getBackground().clearColorFilter();
				}
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			@Override
			public void afterTextChanged(Editable s) {
			}
		});

		//if input field is empty, prompt error; else, proceed to actually create account
		view.findViewById(R.id.dialog_add_confirm).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String key = api.getText().toString().trim();
				if (key.equals("")) {
					Log.w(TAG, "Empty input");
					api.getBackground().setColorFilter(ContextCompat.getColor(view.getContext(), R.color.colorRedAlert), PorterDuff.Mode.SRC_ATOP);
					error.setError("Please enter an API key");
				} else {
					Log.d(TAG, "Start add account");
					AddAccountDialog.this.getDialog().dismiss();
					onPositiveClick(key);
				}
			}
		});

		view.findViewById(R.id.dialog_add_cancel).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d(TAG, "Cancelled");
				AddAccountDialog.this.getDialog().dismiss();
			}
		});
		return builder.create();
	}

	/**
	 * use request code to know who called this dialog
	 * then let it know if created account or not
	 * Note: false doesn't necessarily mean there is an error
	 *
	 * @param isSuccess true on created account, false otherwise
	 */
	public void alertCreateAccount(boolean isSuccess) {
		switch (getTargetRequestCode()) {
			case RequestCode.ACCOUNT:
				AccountFragment fragment = (AccountFragment) getTargetFragment();
				fragment.createAccountResult(parseResult(isSuccess));
				break;
			default:
				Log.w(TAG, "alertCreateAccount: unknown request code");
		}
	}

	//use given API key to create an account
	private void onPositiveClick(String api) {
		account = new AccountInfo(api);
		new CreateAccountTask(getContext(), this).execute(account);
	}

	//if success return the account, otherwise, return null
	private AccountInfo parseResult(boolean isSuccess) {
		if (isSuccess) return account;
		else return null;
	}
}
