package xhsun.gw2app.steve.backend.database.account;

import java.util.ArrayList;
import java.util.List;

import xhsun.gw2api.guildwars2.model.account.Account;
import xhsun.gw2app.steve.backend.database.character.CharacterInfo;
import xhsun.gw2app.steve.backend.util.inventory.CharacterListAdapter;

/**
 * account data type
 *
 * @author xhsun
 * @since 2017-02-04
 */

public class AccountInfo {
	private List<CharacterInfo> characters;
	private List<String> characterNames;
	private int worldID;
	private Account.Access access;
	private String api, id, name, world;
	private boolean isValid, isClosed, isSearched;
	private CharacterListAdapter adapter;

	public AccountInfo(String api) {
		this.api = api;
		characters = new ArrayList<>();
		characterNames = new ArrayList<>();
	}

	//this is for account source to populate account with info
	public AccountInfo(String api, String id, String name, int worldID, String world, Account.Access access, boolean isValid) {
		this.api = api;
		this.id = id;
		this.name = name;
		this.worldID = worldID;
		this.world = world;
		this.access = access;
		this.isValid = isValid;
		characters = new ArrayList<>();
		characterNames = new ArrayList<>();
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

	public boolean isValid() {
		return isValid;
	}

	public void setValid(boolean state) {
		this.isValid = state;
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

	public void setCharacters(List<CharacterInfo> characters) {
		this.characters = characters;
	}

	public List<CharacterInfo> getCharacters() {
		return characters;
	}

	public List<String> getCharacterNames() {
		return characterNames;
	}

	public void setCharacterNames(List<String> characterNames) {
		this.characterNames = characterNames;
	}

	public String toString() {
		return name + " : " + api;
	}

	public boolean isSearched() {
		return isSearched;
	}

	public void setSearched(boolean searched) {
		isSearched = searched;
	}

	public CharacterListAdapter getAdapter() {
		return adapter;
	}

	public void setAdapter(CharacterListAdapter adapter) {
		this.adapter = adapter;
	}
}
