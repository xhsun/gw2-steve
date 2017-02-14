package xhsun.gw2app.steve.util.model;


import xhsun.gw2api.guildwars2.model.account.Account;

/**
 * account data type
 *
 * @author xhsun
 * @since 2017-02-04
 */

public class AccountInfo {
	private int worldID;
	private String api, id, name, world;
	private Account.Access access;
	private boolean isAccessible = true, isClosed;

	public AccountInfo(String api) {
		this.api = api;
	}

	//this is for account source to populate account with info
	public AccountInfo(String api, String id, String name, int worldID, String world, Account.Access access, boolean isAccessible) {
		this.api = api;
		this.id = id;
		this.name = name;
		this.worldID = worldID;
		this.world = world;
		this.access = access;
		this.isAccessible = isAccessible;
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

	public void setName(String name) {
		this.name = name;
	}

	public String getAccountID() {
		return id;
	}

	public void setAccountID(String id) {
		this.id = id;
	}

	public int getWorldID() {
		return worldID;
	}

	public void setWorldID(int worldID) {
		this.worldID = worldID;
	}

	public String getWorld() {
		return world;
	}

	public void setWorld(String world) {
		this.world = world;
	}

	public String getAccess() {
		switch (access) {
			case PlayForFree:
				return "Free Game";
			case GuildWars2:
				return "Base Game";
			case HeartOfThorns:
				return "HOT Expac";
			default:
				return "Not Apply";
		}
	}

	public Account.Access getAccessSource() {
		return access;
	}

	public void setAccess(Account.Access access) {
		this.access = access;
	}

	public boolean isAccessible() {
		return isAccessible;
	}

	public void setIsAccessible(boolean state) {
		this.isAccessible = state;
	}

	public boolean isClosed() {
		return isClosed;
	}

	public void setClosed(boolean closed) {
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
