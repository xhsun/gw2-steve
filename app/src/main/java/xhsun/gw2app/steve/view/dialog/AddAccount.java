package xhsun.gw2app.steve.view.dialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;
import xhsun.gw2app.steve.MainApplication;
import xhsun.gw2app.steve.R;
import xhsun.gw2app.steve.backend.database.account.AccountInfo;
import xhsun.gw2app.steve.backend.database.account.AccountWrapper;
import xhsun.gw2app.steve.backend.util.AddAccountListener;
import xhsun.gw2app.steve.backend.util.AsyncTaskResult;
import xhsun.gw2app.steve.backend.util.dialog.QROnClickListener;

/**
 * dialog with input field for getting API key from user
 *
 * @author xhsun
 * @since 2017-02-06
 */

public class AddAccount extends DialogFragment {
	private View view;
	private AddAccountTask task;
	@BindView(R.id.dialog_add_api)
	TextInputEditText input;
	@BindView(R.id.dialog_add_api_wrapper)
	TextInputLayout layout;
	@BindView(R.id.dialog_add_confirm)
	Button confirm;
	@BindView(R.id.dialog_add_cancel)
	Button cancel;
	@BindView(R.id.dialog_add_qr)
	Button openQR;
	@Inject
	AccountWrapper wrapper;

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		((MainApplication) getActivity().getApplication()).getServiceComponent().inject(this);//injection

		view = View.inflate(getContext(), R.layout.dialog_add_account, null);
		ButterKnife.bind(this, view);

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setView(view);

		//remove error message if there is any
		input.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				if (layout.getError() != null) {
					layout.setError(null);
					input.getBackground().clearColorFilter();
				}
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

		//open QR code scanner
		openQR.setOnClickListener(new QROnClickListener(this, "Scan Guild Wars 2 API Key QR Code"));

		//add account on confirm; dismiss this dialog on cancel
		confirm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onConfirmClick();
			}
		});
		cancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AddAccount.this.getDialog().dismiss();
			}
		});
		Timber.i("Initialization complete");
		return builder.create();
	}

	//for getting QR code from the scanner
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
		//result is not null then get content from it and start adding that API key

		if (result != null) {
			String key = result.getContents();
			Timber.i("start processing QR code content (%s)", key);
			startAddAccount(key);
		} else super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onPause() {
		Timber.i("onPause: stop all tasks");
		super.onPause();
		if (task != null && task.getStatus() != AsyncTask.Status.FINISHED)
			task.cancel(true);
	}

	//parse input from edit text and start adding account
	private void onConfirmClick() {
		String key = input.getText().toString().trim();
		Timber.i("start processing input (%s)", key);
		startAddAccount(key);
		input.clearFocus();
		input.setText("");
		dismissKeyboard();
	}

	//dismiss keyboard
	private void dismissKeyboard() {
		InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
	}

	private void startAddAccount(String key) {
		if (key == null || key.equals("")) {//null or empty prompt error
			input.getBackground().setColorFilter(ContextCompat.getColor(view.getContext(), R.color.colorRedAlert), PorterDuff.Mode.SRC_ATOP);
			layout.setError("Please enter an API key");
			Timber.i("Didn't provide a API key");
		} else {//add account
			Timber.i("Start adding API key (%s)", key);
			task = new AddAccountTask(this);
			task.execute(key);
		}
	}

	//alert add account result to target fragment
	private void alertAdd(AccountInfo result) {
		Timber.i("New account (%s) added", result.getAPI());
		AddAccount.this.dismiss();
		((AddAccountListener) getTargetFragment()).addAccountCallback(result);
	}

	//async task for adding account with spinner and error message dialog
	private class AddAccountTask extends AsyncTask<String, Void, AsyncTaskResult<AccountInfo>> {
		private ProgressDialog spinner;
		private AddAccount dialog;

		private AddAccountTask(AddAccount dialog) {
			this.dialog = dialog;
		}

		@Override
		public void onPreExecute() {
			Timber.i("Create spinner for adding account");
			spinner = new ProgressDialog(getContext());
			spinner.setMessage(getContext().getResources().getString(R.string.dialog_loading));
			spinner.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			spinner.setCancelable(false);
			spinner.show();
		}

		@Override
		protected AsyncTaskResult<AccountInfo> doInBackground(String... params) {
			Timber.i("Send Key (%s) to AccountAPI.addAccount", params[0]);
			try {
				return new AsyncTaskResult<>(wrapper.addAccount(params[0]));
			} catch (IllegalArgumentException e) {
				Timber.d("Something is not right when adding account");
				return new AsyncTaskResult<>(e);
			}
		}

		@Override
		public void onPostExecute(AsyncTaskResult<AccountInfo> result) {
			Timber.i("Processing addAccount result");
			if (spinner != null && spinner.isShowing()) spinner.dismiss();
			if (isCancelled()) return;//task cancelled, abort
			if (result.getError() != null) {//show error message
				String title, message;
				switch (result.getError().getMessage()) {
					case "PERMISSION"://not enough permission
						title = "Not Enough Permission";
						message = "This app need permission for Account, Characters, Inventories, Trading Post, and Wallet";
						break;
					case "KEY"://invalid key
						title = "Invalid GW2 API key";
						message = "Please make sure you have entered the correct API key";
						break;
					case "SQL"://already exist
					case "ACCOUNT":
						title = "Already Registered";
						message = "API key provided are tied to a registered GW2 account";
						break;
					case "SERVER"://server down
						title = "Server Down";
						message = "GW2 API server is currently offline\nPlease try again later";
						break;
					case "NETWORK"://network error
					case "LIMIT"://limit reached
						title = "Server Unavailable";
						message = "Please try again later";
						break;
					default:
						title = "Unknown Error";
						message = "Steve have no idea what just happened... Try again maybe?";
						break;
				}
				showMessage(title, message);
			} else {//get account information
				task = null;
				dialog.alertAdd(result.getData());
			}
		}

		@Override
		protected void onCancelled() {
			Timber.i("Task cancelled, dismiss the spinner");
			if (spinner != null && spinner.isShowing()) spinner.dismiss();
		}

		//show error dialog
		private void showMessage(String title, String msg) {
			android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(getContext()).create();
			alertDialog.setTitle(title);
			alertDialog.setMessage(msg);
			alertDialog.setButton(android.app.AlertDialog.BUTTON_NEUTRAL, "OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
			alertDialog.show();
		}
	}
}
