package xhsun.gw2app.steve.database.account;

import me.nithanim.gw2api.v2.api.account.Account;

/**
 * account data type
 *
 * @author xhsun
 * @since 2017-02-04
 */

public class AccountInfo {
	private String api, id, name, world;
	private Account.Access access;
	private boolean state, isClosed;

	public AccountInfo(String api) {
		this.api = api;
	}

	//this is for account source to populate account with info
	public AccountInfo(String api, String id, String name, String world, Account.Access access, boolean state) {
		this.api = api;
		this.id = id;
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

	void setName(String name) {
		this.name = name;
	}

	public String getAccountID() {
		return id;
	}

	void setAccountID(String id) {
		this.id = id;
	}


	public String getWorld() {
		return world;
	}

	void setWorld(String world) {
		this.world = world;
	}

	public String getAccess() {
		switch (access) {
			case PLAY_FOR_FREE:
				return "Free Game";
			case GUILD_WARS_2:
				return "Base Game";
			case HEART_OF_THORNS:
				return "HOT Expac";
			default:
				return "Not Apply";
		}
	}

	void setAccess(Account.Access access) {
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

	@Override
	public int hashCode() {
		return api.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return this == obj || obj != null && getClass() == obj.getClass() && ((AccountInfo) obj).getAPI().equals(api);
	}
}
