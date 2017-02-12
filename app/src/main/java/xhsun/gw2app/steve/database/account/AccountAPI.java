package xhsun.gw2app.steve.database.account;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import xhsun.gw2api.guildwars2.GuildWars2;
import xhsun.gw2api.guildwars2.model.account.Account;
import xhsun.gw2api.guildwars2.model.util.World;
import xhsun.gw2api.guildwars2.util.GuildWars2Exception;

/**
 * AccountInfo API contains various method used to manipulate an account
 *
 * @author xhsun
 * @since 2017-02-05
 */
public class AccountAPI {
	private static final String[] PERMISSIONS = {"wallet", "tradingpost", "account", "inventories", "characters"};
	private final String TAG = this.getClass().getSimpleName();
	private AccountDB database;

	public enum state {SUCCESS, SQL, PERMISSION, KEY, ACCOUNT, NETWORK}

	public AccountAPI(Context context) {
		database = AccountSourceFactory.getAccountDB(context);
	}

	/**
	 * add account to the database
	 *
	 * @param account containing GW2 API key and/or name
	 * @return enum state: success means success create account, other means error
	 */
	public state addAccount(AccountInfo account) {
		ArrayList<String> permissions = new ArrayList<>(Arrays.asList(PERMISSIONS));
		GuildWars2 api = GuildWars2.getInstance();
		String key, id, name, world = "No World";
		Account gw2info;
		int worldID;
		Account.Access access;

		if (account == null || (key = account.getAPI()) == null || "".equals(key)) {
			Log.w(TAG, "addAccount: Empty key");
			return state.KEY;//invalid key
		}

		try {
			//Must have all the permission listed for the given key
			permissions.removeAll(new ArrayList<>(Arrays.asList(api.getAPIInfo(key).getPermissions())));
			if (!permissions.isEmpty()) {
				Log.w(TAG, "addAccount: Not enough permission");
				return state.PERMISSION;
			}

			//get gw2 account info
			gw2info = api.getAccount(key);

			id = gw2info.getId();
			if (database.getUsingGUID(id) != null)
				return state.ACCOUNT;//account already exist

			name = gw2info.getName();
			access = gw2info.getAccess();

			//compile world info
			worldID = gw2info.getWorldId();
			List<World> worlds = api.getWorldsInfo(new int[]{worldID});
			if (!worlds.isEmpty())
				world = "[" + worlds.get(0).getRegion() + "] " + ((worlds.get(0).getName() == null) ? "" : worlds.get(0).getName());

			//create account in the database
			if (database.createAccount(key, id, name, worldID, world, access)) {
				account.setAccountID(id);
				account.setName(name);
				account.setWorldID(worldID);
				account.setWorld(world);
				account.setAccess(access);
				return state.SUCCESS;
			}
		} catch (GuildWars2Exception e) {
			Log.w(TAG, "addAccount: Invalid key");
			return state.KEY;//invalid API key
		} catch (Exception e) {
			Log.w(TAG, "addAccount: Network error");
			return state.NETWORK;//network error
		}
		Log.w(TAG, "addAccount: Account already exist");
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
		if (isSuccess = database.accountInvalid(api)) account.setIsAccessible(false);
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

	/**
	 * this is going to be taxing, please try to do it in the background
	 */
	public void updateAccounts() {
		Account gw2info;
		int worldID;
		Account.Access access;
		String name, world = null;
		GuildWars2 api = GuildWars2.getInstance();
		List<AccountInfo> accounts = getAll(true);//no point check inaccessible account
		for (AccountInfo account : accounts) {
			try {
				gw2info = api.getAccount(account.getAPI());

				name = (gw2info.getName().equals(account.getName())) ? null : gw2info.getName();
				access = (gw2info.getAccess() == account.getAccessSource()) ? null : gw2info.getAccess();

				if ((worldID = gw2info.getWorldId()) != account.getWorldID()) {
					//compile world info
					List<World> worlds = api.getWorldsInfo(new int[]{gw2info.getWorldId()});
					if (!worlds.isEmpty())
						world = "[" + worlds.get(0).getRegion() + "] " + ((worlds.get(0).getName() == null) ? "" : worlds.get(0).getName());
				} else worldID = -1;

				database.updateAccount(account.getAPI(), name, worldID, world, access);

			} catch (GuildWars2Exception e) {
				Log.w(TAG, "updateAccounts: account " + account.getAPI() + " is no longer accessible");
				database.accountInvalid(account.getAPI());
			} catch (IOException e) {
				Log.w(TAG, "updateAccounts: no internet connection");
				break;//no point on trying
			}
		}
	}

	//close database connection
	public void close() {
		database.close();
	}
}
