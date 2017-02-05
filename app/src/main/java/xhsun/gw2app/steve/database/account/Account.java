package xhsun.gw2app.steve.database.account;

import xhsun.gw2app.steve.database.DataBaseHelper;

/**
 * wrapper for account database
 *
 * @author xhsun
 * @since 2017-02-04
 */

public class Account {
	private String api, id, usr, name, world, access;
	private boolean state, isClosed;

	public Account(String api) {
		this.api = api;
	}

	//this is for account source to populate account with info
	Account(String api, String id, String usr, String name, String world, String access, boolean state) {
		this.api = api;
		this.id = id;
		this.usr = usr;
		this.name = name;
		this.world = world;
		this.access = access;
		this.state = state;
	}

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
		if (name.length() > DataBaseHelper.NAME_LIMIT)
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

	void setWorld(String world) {
		this.world = world;
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

	public boolean isValid() {
		return state;
	}

	void setValid(boolean state) {
		this.state = state;
	}

	public boolean isClosed() {
		return isClosed;
	}

	void setClosed(boolean closed) {
		isClosed = closed;
	}
}
