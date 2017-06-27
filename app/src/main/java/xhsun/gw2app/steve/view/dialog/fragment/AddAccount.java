package xhsun.gw2app.steve.view.dialog.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
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
import me.xhsun.guildwars2wrapper.error.GuildWars2Exception;
import timber.log.Timber;
import xhsun.gw2app.steve.MainApplication;
import xhsun.gw2app.steve.R;
import xhsun.gw2app.steve.backend.data.model.AccountModel;
import xhsun.gw2app.steve.backend.data.wrapper.account.AccountWrapper;
import xhsun.gw2app.steve.backend.data.wrapper.character.CharacterWrapper;
import xhsun.gw2app.steve.backend.util.support.dialog.AddAccountListener;
import xhsun.gw2app.steve.backend.util.support.dialog.QROnClickListener;
import xhsun.gw2app.steve.backend.util.task.AsyncTaskResult;
import xhsun.gw2app.steve.backend.util.task.CancellableAsyncTask;

/**
 * dialog with input field for getting API key from user
 *
 * @author xhsun
 * @since 2017-02-06
 */

public class AddAccount extends DialogFragment {
	private AddAccountTask task;
	@BindView(R.id.dialog_add_api)
	TextInputEditText input;
	@BindView(R.id.dialog_add_api_wrapper)
	TextInputLayout error;
	@BindView(R.id.dialog_add_confirm)
	Button confirm;
	@BindView(R.id.dialog_add_cancel)
	Button cancel;
	@BindView(R.id.dialog_add_qr)
	Button openQR;
	@Inject
	AccountWrapper wrapper;
	@Inject
	CharacterWrapper characterWrapper;

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		((MainApplication) getActivity().getApplication()).getServiceComponent().inject(this);//injection

		View view = View.inflate(getContext(), R.layout.dialog_add_account, null);
		ButterKnife.bind(this, view);

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setView(view);

		//remove error message if there is any
		input.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				if (error.getError() != null) error.setError(null);
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
		confirm.setOnClickListener(v -> onConfirmClick());
		cancel.setOnClickListener(v -> AddAccount.this.getDialog().dismiss());
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
			error.post(() -> error.setError("Please enter a valid API key"));
			Timber.i("Didn't provide a API key");
		} else {//add account
			Timber.i("Start adding API key (%s)", key);
			task = new AddAccountTask(this);
			task.execute(key);
		}
	}

	//alert add account result to target fragment
	private void alertAdd(AccountModel result) {
		Timber.i("New account (%s) added", result.getAPI());
		AddAccount.this.dismiss();
		((AddAccountListener) getTargetFragment()).addAccountCallback(result);
	}

	//async task for adding account with spinner and error message dialog
	private class AddAccountTask extends AsyncTask<String, Void, AsyncTaskResult<AccountModel>> {
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
		protected AsyncTaskResult<AccountModel> doInBackground(String... params) {
			Timber.i("Send Key (%s) to AccountAPI.addAccount", params[0]);
			try {
				AccountModel account = wrapper.addAccount(params[0]);
				try {
					account.setAllCharacterNames(characterWrapper.getAllNames(account.getAPI()));
				} catch (GuildWars2Exception ignored) {
				}
				return new AsyncTaskResult<>(account);
			} catch (IllegalArgumentException e) {
				Timber.d("Something is not right when adding account");
				return new AsyncTaskResult<>(e);
			}
		}

		@Override
		public void onPostExecute(AsyncTaskResult<AccountModel> result) {
			Timber.i("Processing addAccount result");
			if (spinner != null && spinner.isShowing()) spinner.dismiss();
			if (isCancelled()) return;//task cancelled, abort
			if (result.getError() != null) {//show error message
				String title, message;
				switch (result.getError().getMessage()) {
					case "PERMISSION"://not enough permission
						title = "Not Enough Permission";
						message = "This app need permission for Account, Characters, Inventories, Trading Post, Wallet, and Unlocks";
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
				AccountModel account = result.getData();
				for (String name : account.getAllCharacterNames())
					new AddCharacter(account.getAPI(), name).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

				task = null;
				dialog.alertAdd(account);
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
					(dialog1, which) -> dialog1.dismiss());
			alertDialog.show();
		}
	}

	private class AddCharacter extends CancellableAsyncTask<Void, Void, Void> {
		private String api;
		private String name;

		private AddCharacter(String api, String name) {
			this.api = api;
			this.name = name;
		}

		@Override
		protected Void doInBackground(Void... params) {
			if (isCancelled() || isCancelled) return null;
			Timber.i("Try to add all unknown characters to database for account (%s)", api);
			try {
				characterWrapper.update(api, name);
			} catch (GuildWars2Exception ignored) {
			}
			return null;
		}
	}
}
