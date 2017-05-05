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
	private List<StorageInfo> material;
	private List<StorageInfo> wardrobe;
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
		material = new ArrayList<>();
		wardrobe = new ArrayList<>();
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

	int getWorldID() {
		return worldID;
	}

	public String getWorld() {
		return world;
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

	Account.Access getAccessSource() {
		return access;
	}

	public boolean isValid() {
		return isValid;
	}

	void setValid(boolean state) {
		this.isValid = state;
	}

	public boolean isClosed() {
		return isClosed;
	}

	void setClosed(boolean closed) {
		isClosed = closed;
	}

	public List<StorageInfo> getBank() {
		return bank;
	}

	public void setBank(List<StorageInfo> bank) {
		this.bank = bank;
	}

	public List<StorageInfo> getMaterial() {
		return material;
	}

	public void setMaterial(List<StorageInfo> material) {
		this.material = material;
	}

	public List<StorageInfo> getWardrobe() {
		return wardrobe;
	}

	public void setWardrobe(List<StorageInfo> wardrobe) {
		this.wardrobe = wardrobe;
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
	public boolean equals(Object obj) {
		return this == obj || obj != null && getClass() == obj.getClass() &&
				(((AccountInfo) obj).api.equals(api));
	}

	@Override
	public int hashCode() {
		int result = worldID;
		result = 31 * result + (access != null ? access.hashCode() : 0);
		result = 31 * result + api.hashCode();
		result = 31 * result + (id != null ? id.hashCode() : 0);
		result = 31 * result + (name != null ? name.hashCode() : 0);
		result = 31 * result + (world != null ? world.hashCode() : 0);
		result = 31 * result + (isValid ? 1 : 0);
		result = 31 * result + (isClosed ? 1 : 0);
		result = 31 * result + (charNames != null ? charNames.hashCode() : 0);
		result = 31 * result + (bank != null ? bank.hashCode() : 0);
		result = 31 * result + (material != null ? material.hashCode() : 0);
		result = 31 * result + (wardrobe != null ? wardrobe.hashCode() : 0);
		return result;
	}
}
