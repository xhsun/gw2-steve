package xhsun.gw2app.steve.misc;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;

import xhsun.gw2api.guildwars2.GuildWars2;
import xhsun.gw2app.steve.R;
import xhsun.gw2app.steve.database.account.AccountAPI;
import xhsun.gw2app.steve.database.account.AccountInfo;
import xhsun.gw2app.steve.view.dialog.AddAccountDialog;

/**
 * @author xhsun
 * @since 2017-02-06
 */

public class CreateAccountTask extends AsyncTask<AccountInfo, Void, AccountAPI.state> {
	private ProgressDialog dialog;
	private GuildWars2 wrapper;
	private Context context;
	private AddAccountDialog add;

	public CreateAccountTask(GuildWars2 wrapper, Context context, AddAccountDialog add) {
		this.context = context;
		this.wrapper = wrapper;
		this.add = add;
	}

	@Override
	public void onPreExecute() {
		dialog = new ProgressDialog(context);
		dialog.setMessage(context.getResources().getString(R.string.dialog_loading));
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.setCancelable(false);
		dialog.show();
	}

	@Override
	public AccountAPI.state doInBackground(AccountInfo... params) {
		AccountAPI create = new AccountAPI(context, wrapper);
		return create.addAccount(params[0]);
	}

	@Override
	public void onPostExecute(AccountAPI.state result) {
		String title, msg;
		if (dialog != null && dialog.isShowing())
			dialog.dismiss();
		switch (result) {
			case SUCCESS:
				add.alertCreateAccount(true);
				return;
			case PERMISSION://not enough permission
				title = "Not Enough Permission";
				msg = "This app need permission for account, characters, inventories, tradingpost, and wallet";
				break;
			case KEY://invalid key
				title = "Invalid GW2 API key";
				msg = "Please make sure you have entered the correct API key";
				break;
			case SQL://already exist
			case ACCOUNT:
				title = "GW2 Account Already Registered";
				msg = "API key provided are tied to a GW2 account that is already registered";
				break;
			case NETWORK://network error
				title = "Network Error";
				msg = "Please turn on your internet connection and try again";
				break;
			default:
				title = "Unknown Error";
				msg = "Please wait for few seconds and try again";
				break;
		}
		showMessage(title, msg);
		add.alertCreateAccount(false);
	}

	private void showMessage(String title, String msg) {
		AlertDialog alertDialog = new AlertDialog.Builder(context).create();
		alertDialog.setTitle(title);
		alertDialog.setMessage(msg);
		alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		alertDialog.show();
	}
}