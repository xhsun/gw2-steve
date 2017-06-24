package xhsun.gw2app.steve.backend.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.xhsun.guildwars2wrapper.model.v2.account.Account;
import xhsun.gw2app.steve.backend.data.vault.MaterialStorageData;
import xhsun.gw2app.steve.backend.data.vault.WardrobeData;
import xhsun.gw2app.steve.backend.data.vault.item.BankItemData;

/**
 * account data type
 *
 * @author xhsun
 * @since 2017-02-04
 */

public class AccountData extends AbstractData {
	private int worldID;
	private Account.Access access;
	private String api, id, world;
	private boolean isValid, isClosed, isSearched;

	private List<String> charNames;
	private List<CharacterData> characters;
	private List<BankItemData> bank;
	private List<MaterialStorageData> material;
	private List<WardrobeData> wardrobe;

	public AccountData(String api) {
		super("");
		this.api = api;
		initLists();
	}

	//this is for account source to populate account with info
	public AccountData(String api, String id, String name, int worldID, String world, Account.Access access, boolean isValid) {
		super(name);
		this.api = api;
		this.id = id;
		this.worldID = worldID;
		this.world = world;
		this.access = access;
		this.isValid = isValid;
		initLists();
	}

	private void initLists() {
		charNames = new ArrayList<>();
		characters = new ArrayList<>();
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

	public String getAccountID() {
		return id;
	}

	public int getWorldID() {
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

	public Account.Access getAccessSource() {
		return access;
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

	public List<BankItemData> getBank() {
		return bank;
	}

	public void setBank(List<BankItemData> bank) {
		this.bank = bank;
	}

	public List<MaterialStorageData> getMaterial() {
		return material;
	}

	public void setMaterial(List<MaterialStorageData> material) {
		this.material = material;
	}

	public List<WardrobeData> getWardrobe() {
		return wardrobe;
	}

	public void setWardrobe(List<WardrobeData> wardrobe) {
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

	public void setAllCharacters(List<CharacterData> characters) {
		this.characters = characters;
	}

	public List<CharacterData> getAllCharacters() {
		return characters;
	}

	public boolean isSearched() {
		return isSearched;
	}

	public void setSearched(boolean searched) {
		isSearched = searched;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		AccountData that = (AccountData) o;

		return api != null ? api.equals(that.api) : that.api == null;
	}

	@Override
	public int hashCode() {
		return api.hashCode();
	}

	@Override
	public String toString() {
		return "AccountData{" +
				"api='" + api + '\'' +
				'}';
	}
}
