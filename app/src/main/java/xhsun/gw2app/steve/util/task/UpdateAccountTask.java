package xhsun.gw2app.steve.util.task;

import android.os.AsyncTask;

import xhsun.gw2app.steve.database.account.AccountAPI;

/**
 * update account information stored in the database to make sure nothing is outdated
 *
 * @author xhsun
 * @since 2017-02-11
 */
public class UpdateAccountTask extends AsyncTask<Void, Void, Void> {
	private AccountAPI api;

	public UpdateAccountTask(AccountAPI api) {
		this.api = api;
	}

	@Override
	protected Void doInBackground(Void... params) {
		api.updateAccounts();
		return null;
	}

	@Override
	public void onPostExecute(Void nothing) {
		api.close();
	}
}
