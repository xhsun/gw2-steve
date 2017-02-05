package xhsun.gw2app.steve.database.account;

import android.content.Context;

/**
 * Account API contains various method used to manipulate an account
 *
 * @author xhsun
 * @since 2017-02-05
 */
public class AccountAPI {
	private AccountDB database;

	public AccountAPI(Context context) {
		database = AccountSourceFactory.getAccountDB(context);
	}

	/**
	 * mark this as invalid account
	 *
	 * @param account containing API key
	 * @return true if this is invalid, false otherwise
	 * @throws IllegalArgumentException if API key is empty or null
	 */
	public boolean markInvalid(Account account) throws IllegalArgumentException {
		if ("".equals(account.getAPI()))
			throw new IllegalArgumentException("GW2 API key cannot be NULL/empty");
		if (database.accountInvalid(account.getAPI())) account.setValid(false);
		return account.isValid();
	}

	/**
	 * add account to the database
	 *
	 * @param account containing GW2 API key and/or name
	 * @return true on success, false otherwise
	 */
	public boolean addAccount(Account account) throws IllegalArgumentException {
		if ("".equals(account.getAPI()))
			throw new IllegalArgumentException("GW2 API key cannot be NULL/empty");
		int worldID;
		boolean isAdd;
		//TODO remove empty string once api stuff is here
		String id = "", usr = "", world = "", access = "";

		//TODO get all other required info from gw2 api
		//TODO get world name using world id

		if (isAdd = database.createAccount(account.getAPI(), id, usr, account.getName(), world, access)) {
			account.setAccountID(id);
			account.setAccountName(usr);
			account.setWorld(world);
			account.setAccess(access);
		}
		return isAdd;
	}

	/**
	 * remove account from the database
	 *
	 * @param account containing GW2 API key
	 * @return true on success, false otherwise
	 */
	public boolean removeAccount(Account account) throws IllegalArgumentException {
		if ("".equals(account.getAPI()))
			throw new IllegalArgumentException("GW2 API key cannot be NULL/empty");
		boolean isSuccess;
		if (isSuccess = database.deleteAccount(account.getAPI()))
			account.setClosed(true);
		return isSuccess;
	}

	//close database connection
	public void close() {
		database.close();
	}
}
