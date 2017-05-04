package xhsun.gw2app.steve.backend.database.account;

import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import xhsun.gw2api.guildwars2.model.account.Account;
import xhsun.gw2app.steve.backend.database.character.CharacterInfo;
import xhsun.gw2app.steve.backend.database.storage.StorageInfo;

/**
 * account data type
 *
 * @author xhsun
 * @since 2017-02-04
 */

public class AccountInfo {
	private int worldID;
	private Account.Access access;
	private String api, id, name = "", world;
	private boolean isValid, isClosed, isSearched;

	private List<String> charNames;
	private List<CharacterInfo> characters;
	private List<CharacterInfo> allCharacters;
	private List<StorageInfo> bank;

	private RecyclerView child;

	public AccountInfo(String api) {
		this.api = api;
		initLists();
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
		initLists();
	}

	private void initLists() {
		charNames = new ArrayList<>();
		characters = new ArrayList<>();
		allCharacters = new ArrayList<>();
		bank = new ArrayList<>();
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

	public List<StorageInfo> getBank() {
		return bank;
	}

	public void setBank(List<StorageInfo> bank) {
		this.bank = bank;
	}

	/**
	 * @return list of char that is known for this account
	 */
	public List<String> getAllCharacterNames() {
		return charNames;
	}

	public void setAllCharacterNames(List<String> names) {
		Collections.sort(names);
		charNames = names;
	}

	public void setAllCharacters(List<CharacterInfo> characters) {
		allCharacters = characters;
	}

	public List<CharacterInfo> getAllCharacters() {
		return allCharacters;
	}

	public void setCharacters(List<CharacterInfo> characters) {
		this.characters = characters;
	}

	public List<CharacterInfo> getCharacters() {
		return characters;
	}

	/**
	 * @return set of char that is showing right now
	 */
	public Set<String> getCharacterNames() {
		Set<String> names = new HashSet<>();
		for (CharacterInfo c : characters) names.add(c.getName());
		return names;
	}

	public RecyclerView getChild() {
		return child;
	}

	public void setChild(RecyclerView child) {
		this.child = child;
	}

	public boolean isSearched() {
		return isSearched;
	}

	public void setSearched(boolean searched) {
		isSearched = searched;
	}

	@Override
	public String toString() {
		return name + " : " + api;
	}

	@Override
	public int hashCode() {
		return api.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return this == obj || obj != null && getClass() == obj.getClass() &&
				(((AccountInfo) obj).api.equals(api));
	}
}
