package xhsun.gw2app.steve.database.account;

import android.content.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.nithanim.gw2api.v2.GuildWars2Api;
import me.nithanim.gw2api.v2.GuildWars2ApiException;
import me.nithanim.gw2api.v2.api.account.Account;
import me.nithanim.gw2api.v2.api.worlds.World;

/**
 * AccountInfo API contains various method used to manipulate an account
 *
 * @author xhsun
 * @since 2017-02-05
 */
public class AccountAPI {
	private static final String[] PERMISSIONS = {"wallet", "tradingpost", "account", "inventories", "characters"};

	public enum state {SUCCESS, SQL, PERMISSION, KEY, ACCOUNT, NETWORK}

	private GuildWars2Api api;
	private AccountDB database;

	public AccountAPI(Context context, GuildWars2Api api) {
		this.api = api;
		database = AccountSourceFactory.getAccountDB(context);
	}

	/**
	 * add account to the database
	 *
	 * @param account containing GW2 API key and/or name
	 * @return enum state: success means success create account, other means error
	 */
	public state addAccount(AccountInfo account) throws IllegalArgumentException {
		ArrayList<String> permissions = new ArrayList<>(Arrays.asList(PERMISSIONS));
		Account.Access access;
		String key, id, name, world;
		Account gw2info;
		World worldInfo;

		if (account == null || (key = account.getAPI()) == null || "".equals(key))
			throw new IllegalArgumentException("GW2 API key cannot be NULL/empty");

		try {
			//Must have all the permission listed for the given key
			permissions.removeAll(new ArrayList<>(Arrays.asList(api.tokeninfo().get(key).getPermissions())));
			if (!permissions.isEmpty())
				return state.PERMISSION;

			//get gw2 account info
			gw2info = api.account().get(key);

			id = gw2info.getId();
			//TODO disabled for testing only
//			if (database.getUsingGUID(id) != null)
//				return state.ACCOUNT;//account already exist

			name = gw2info.getName();
			access = gw2info.getAccess();

			//compile world info
			worldInfo = api.worlds().get(gw2info.getWorld());
			world = ((worldInfo.isNorthAmerica()) ? "[NA] " : "[EU] ") + worldInfo.getName();

			//create account in the database
			if (database.createAccount(key, id, name, world, access)) {
				account.setAccountID(id);
				account.setName(name);
				account.setWorld(world);
				account.setAccess(access);
				return state.SUCCESS;
			}
		} catch (GuildWars2ApiException e) {
			return state.KEY;//invalid API key
		} catch (Exception e) {
			return state.NETWORK;//network error
		}
		return state.SQL;//SQL error, probably b/c account already exist
	}

	/**
	 * remove account from the database
	 *
	 * @param account containing GW2 API key
	 * @return true on success, false otherwise
	 */
	public boolean removeAccount(AccountInfo account) throws IllegalArgumentException {
		boolean isSuccess;
		String api;
		if (account == null || (api = account.getAPI()) == null || "".equals(api))
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
	public boolean markInvalid(AccountInfo account) throws IllegalArgumentException {
		boolean isSuccess;
		String api;
		if (account == null || (api = account.getAPI()) == null || "".equals(api))
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
	public AccountInfo get(boolean isAPI, String value) {
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
	public List<AccountInfo> getAll(Boolean state) {
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
	public AccountInfo getAPI(String id) {
		return database.getAPI(id);
	}

	/**
	 * get all stored API keys
	 *
	 * @param state true to get all valid | false to get all invalid | null to get all
	 * @return list of accounts | empty if not find
	 */
	public List<AccountInfo> getAllAPI(Boolean state) {
		if (state == null)
			return database.getAllAPI();
		return database.getAllAPIWithState(state);
	}

	//close database connection
	public void close() {
		database.close();
	}
}
