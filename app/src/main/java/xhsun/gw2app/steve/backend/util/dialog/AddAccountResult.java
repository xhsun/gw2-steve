package xhsun.gw2app.steve.backend.util.dialog;

import xhsun.gw2app.steve.backend.database.account.AccountInfo;

/**
 * Based on <a href="http://stackoverflow.com/questions/1739515/asynctask-and-error-handling-on-android">this</a><br/>
 * For handling exceptions in onPostExecute (AsyncTask)
 *
 * @author xhsun
 * @since 2017-03-20
 */

public class AddAccountResult {
	private AccountInfo result;
	private Exception error;

	public AddAccountResult(Exception error) {
		super();
		this.error = error;
	}

	public AccountInfo getResult() {
		return result;
	}

	public Exception getError() {
		return error;
	}

	public AddAccountResult(AccountInfo result) {
		super();
		this.result = result;
	}
}
