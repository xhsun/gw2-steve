package xhsun.gw2app.steve.database.account;

import android.content.Context;

import java.util.List;

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
	 * add account to the database
	 *
	 * @param account containing GW2 API key and/or name
	 * @return true on success, false otherwise
	 */
	public boolean addAccount(Account account) throws IllegalArgumentException {
		int worldID;
		boolean isSuccess;
		//TODO remove empty string once api stuff is here
		String api, id = "", usr = "", world = "", access = "";
		if ((api = account.getAPI()) == null || "".equals(api))
			throw new IllegalArgumentException("GW2 API key cannot be NULL/empty");

		//TODO get all other required info from gw2 api
		//TODO get world name using world id

		if (isSuccess = database.createAccount(api, id, usr, account.getName(), world, access)) {
			account.setAccountID(id);
			account.setAccountName(usr);
			account.setWorld(world);
			account.setAccess(access);
		}
		return isSuccess;
	}

	/**
	 * remove account from the database
	 *
	 * @param account containing GW2 API key
	 * @return true on success, false otherwise
	 */
	public boolean removeAccount(Account account) throws IllegalArgumentException {
		boolean isSuccess;
		String api;
		if ((api = account.getAPI()) == null || "".equals(api))
			throw new IllegalArgumentException("GW2 API key cannot be NULL/empty");
		if (isSuccess = database.deleteAccount(api))
			account.setClosed(true);
		return isSuccess;
	}

	/**
	 * mark this as invalid account
	 *
	 * @param account containing API key
	 * @return true if this is invalid, false otherwise
	 * @throws IllegalArgumentException if API key is empty or null
	 */
	public boolean markInvalid(Account account) throws IllegalArgumentException {
		boolean isSuccess;
		String api;
		if ((api = account.getAPI()) == null || "".equals(api))
			throw new IllegalArgumentException("GW2 API key cannot be NULL/empty");
		if (isSuccess = database.accountInvalid(api)) account.setValid(false);
		return isSuccess;
	}

	/**
	 * get account detail using GW2 API key or GW2 account id
	 *
	 * @param isAPI is the value given an API key
	 * @param value used for seek
	 * @return account detail | NULL if not find
	 */
	public Account get(boolean isAPI, String value) {
		if (isAPI)
			return database.getUsingAPI(value);
		return database.getUsingGUID(value);
	}

	/**
	 * get all return all accounts in detail
	 *
	 * @param state true to get all valid | false to get all invalid | null to get all
	 * @return list of accounts | empty if not find
	 */
	public List<Account> getAll(Boolean state) {
		if (state == null)
			return database.getAll();
		return database.getAllWithState(state);
	}

	/**
	 * get GW2 API key using GW2 account id
	 *
	 * @param id GW2 account id
	 * @return GW2 API key | NULL if not find
	 */
	public Account getAPI(String id) {
		return database.getAPI(id);
	}

	/**
	 * get all stored API keys
	 *
	 * @param state true to get all valid | false to get all invalid | null to get all
	 * @return list of accounts | empty if not find
	 */
	public List<Account> getAllAPI(Boolean state) {
		if (state == null)
			return database.getAllAPI();
		return database.getAllAPIWithState(state);
	}

	//close database connection
	public void close() {
		database.close();
	}
}
