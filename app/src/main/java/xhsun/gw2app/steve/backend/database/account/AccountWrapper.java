package xhsun.gw2app.steve.backend.database.account;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;
import xhsun.gw2api.guildwars2.GuildWars2;
import xhsun.gw2api.guildwars2.err.ErrorCode;
import xhsun.gw2api.guildwars2.err.GuildWars2Exception;
import xhsun.gw2api.guildwars2.model.World;
import xhsun.gw2api.guildwars2.model.account.Account;
import xhsun.gw2app.steve.backend.data.AccountData;

/**
 * For manipulate account(s)
 *
 * @author xhsun
 * @since 2017-02-05
 */

public class AccountWrapper {
	private AccountDB database;
	private GuildWars2 wrapper;
	private boolean isCancelled = false;

	@Inject
	public AccountWrapper(AccountDB database, GuildWars2 wrapper) {
		this.database = database;
		this.wrapper = wrapper;
	}

	/**
	 * add account to the database<br/>
	 * Really expensive task, do it in background
	 *
	 * @param api GW2 API key
	 * @return Account information
	 * @throws IllegalArgumentException If there is something not right with the API and account associated
	 */
	public AccountData addAccount(String api) throws IllegalArgumentException {
		//Note: add item to this list to increase # of permissions needed
		ArrayList<String> permissions = new ArrayList<>(Arrays.asList("wallet", "tradingpost", "account", "inventories", "characters", "unlocks"));

		//TODO maybe a limit on how many account user can put in?

		if (api == null || "".equals(api)) {
			Timber.d("Did not provide an API key");
			throw new IllegalArgumentException("KEY");
		}

		try {
			//Must have all the permission listed for the given key
			permissions.removeAll(new ArrayList<>(Arrays.asList(wrapper.getAPIInfo(api).getPermissions())));
			if (!permissions.isEmpty()) {
				Timber.d("Not enough permission for the given API key (%s)", api);
				throw new IllegalArgumentException("PERMISSION");
			}

			//get gw2 account info
			Account account = wrapper.getAccount(api);

			String name = account.getName();
			Account.Access access = account.getAccess();

			String id = account.getId();
			if (database.getUsingGUID(id) != null) {
				Timber.d("Account already exist for API (%s)", api);
				throw new IllegalArgumentException("ACCOUNT");
			}
			//compile world infomation
			int world_id = account.getWorldId();
			String world = "No World";
			List<World> worlds = wrapper.getWorldsInfo(new long[]{world_id});
			if (!worlds.isEmpty())
				world = "[" + worlds.get(0).getRegion() + "] " + ((worlds.get(0).getName() == null) ? "" : worlds.get(0).getName());

			//create account in the database
			if (database.createAccount(api, id, name, world_id, world, access))
				return new AccountData(api, id, name, world_id, world, access, true);

		} catch (GuildWars2Exception e) {
			switch (e.getErrorCode()) {
				case Server:
					Timber.d("Server unavailable");
					throw new IllegalArgumentException("SERVER");
				case Key:
					Timber.d("Illegal API key (%s)", api);
					throw new IllegalArgumentException("KEY");
				case Limit:
					Timber.d("server limit (600 requests per minute) reached");
					throw new IllegalArgumentException("LIMIT");
				case Network:
					Timber.d("Network error: (%s)", e.getMessage());
					throw new IllegalArgumentException("NETWORK");
				default:
					Timber.d("ugh... something happened...");
					throw new IllegalArgumentException("UNKNOWN");
			}
		}
		Timber.d("Account already exist for the given API key (%s)", api);
		throw new IllegalArgumentException("SQL");
	}

	/**
	 * remove account from the database
	 *
	 * @param account containing GW2 API key
	 * @return true on success, false otherwise
	 */
	public boolean removeAccount(AccountData account) {
		boolean isSuccess;
		String api;
		if (account == null || (api = account.getAPI()) == null || "".equals(api)) {
			Timber.d("Did not provide an API key");
			return false;
		}

		if (isSuccess = database.deleteAccount(api)) account.setClosed(true);
		return isSuccess;
	}

	/**
	 * mark this as invalid account
	 *
	 * @param account containing API key
	 * @return true if this is invalid, false otherwise
	 */
	public boolean markInvalid(AccountData account) {
		boolean isSuccess;
		String api;
		if (account == null || (api = account.getAPI()) == null || "".equals(api)) {
			Timber.d("Did not provide an API key");
			return false;
		}

		if (isSuccess = database.accountInvalid(api)) account.setValid(false);
		return isSuccess;
	}

	/**
	 * get account detail using GW2 API key
	 *
	 * @param api API key
	 * @return account detail | NULL if not find
	 */
	public AccountData get(String api) {
		return database.getUsingAPI(api);
	}

	/**
	 * get all return all accounts in detail
	 *
	 * @param state true to get all valid | false to get all invalid | null to get all
	 * @return list of accounts | empty if not find
	 */
	public List<AccountData> getAll(Boolean state) {
		if (state == null) return database.getAll();
		return database.getAllWithState(state);
	}

//	/**
//	 * get all stored API keys
//	 *
//	 * @param state true to get all valid | false to get all invalid | null to get all
//	 * @return list of accounts | empty if not find
//	 */
//	public List<String> getAllAPI(Boolean state) {
//		if (state == null) return database.getAllAPI();
//		return database.getAllAPIWithState(state);
//	}

	/**
	 * Update every account that is in the database<br/>
	 * Really expensive, do it in the background
	 */
	public void updateAccounts() {
		List<AccountData> accounts = getAll(true);//no point check inaccessible account
		for (AccountData info : accounts) {
			if (isCancelled) break;
			String api = info.getAPI();
			try {
				//get up to date account information
				Account account = wrapper.getAccount(api);
				String name = (account.getName().equals(account.getName())) ? null : account.getName();
				Account.Access access = (account.getAccess() == info.getAccessSource()) ? null : account.getAccess();

				int world_id;
				String world = null;
				if ((world_id = account.getWorldId()) != info.getWorldID()) {//compile world info only if it's different
					List<World> worlds = wrapper.getWorldsInfo(new long[]{world_id});
					if (!worlds.isEmpty())
						world = "[" + worlds.get(0).getRegion() + "] " + ((worlds.get(0).getName() == null) ? "" : worlds.get(0).getName());
				} else world_id = -1;

				database.updateAccount(api, name, world_id, world, access);

			} catch (GuildWars2Exception e) {
				if (e.getErrorCode() == ErrorCode.Key) {
					Timber.e(e, "Account (%s) is no longer accessible", api);
					markInvalid(info);//mark account as invalid
				} else {
					Timber.e(e, e.getMessage());
					break;//no point on trying
				}
			}
		}
	}

	public void setCancelled(boolean cancelled) {
		isCancelled = cancelled;
	}
}
