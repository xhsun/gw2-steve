package xhsun.gw2app.steve.database;

import android.content.Context;

/**
 * wrapper for account database
 *
 * @author xhsun
 * @version 0.1
 * @since 2017-02-04
 */

public class Account {
	private String api, id, usr, name, world, access;

	public String getAPI() {
		return api;
	}

	public void setAPI(String api) {
		this.api = api;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) throws IllegalArgumentException {
		if (name.length() > AccountDBHelper.NAME_LIMIT)
			throw new IllegalArgumentException("the given name exceed limit of 25 chars");
		this.name = name;
	}

	public String getAccountID() {
		return id;
	}

	void setAccountID(String id) {
		this.id = id;
	}

	public String getAccountName() {
		return usr;
	}

	void setAccountName(String usr) {
		this.usr = usr;
	}

	public String getWorld() {
		return world;
	}

	void setWorld(int world) {
		//TODO use world id to find world name, then store it
	}

	public String getAccess() {
		switch (access) {
			case "'PlayForFree'":
				return "Free";
			case "GuildWars2":
				return "Base";
			case "HeartOfThorns":
				return "HOT";
			default:
				return "None";
		}
	}

	void setAccess(String access) {
		this.access = access;
	}

	/**
	 * add account to the database
	 *
	 * @param context context
	 * @param api     GW2 API key
	 * @param name    name
	 * @return true on success, false otherwise
	 */
	public boolean addAccount(Context context, String api, String name) {
		int worldID;
		AccountSource source = null;
		//TODO remove empty string once api stuff is here
		String id = "", usr = "", world = "", access = "";
		try {
			//TODO get all other required info from gw2 api
			//TODO get world name using world id

			source = new AccountSource(context);
			return source.createAccount(api, id, usr, name, world, access);
		} finally {
			if (source != null) source.close();
		}
	}

	/**
	 * remove account from the database
	 *
	 * @param context context
	 * @param api     GW2 API key
	 * @return true on success, false otherwise
	 */
	public boolean removeAccount(Context context, String api) {
		AccountSource source = null;
		try {
			source = new AccountSource(context);
			return source.deleteAccount(api);
		} finally {
			if (source != null) source.close();
		}
	}
}
