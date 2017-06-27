package xhsun.gw2app.steve.backend.data.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.xhsun.guildwars2wrapper.model.v2.account.Account;
import xhsun.gw2app.steve.backend.data.model.vault.MaterialStorageModel;
import xhsun.gw2app.steve.backend.data.model.vault.WardrobeModel;
import xhsun.gw2app.steve.backend.data.model.vault.item.BankItemModel;

/**
 * account data type
 *
 * @author xhsun
 * @since 2017-02-04
 */

public class AccountModel extends AbstractModel {
	private int worldID;
	private Account.Access access;
	private String api, id, world;
	private boolean isValid, isClosed, isSearched;

	private List<String> charNames;
	private List<CharacterModel> characters;
	private List<BankItemModel> bank;
	private List<MaterialStorageModel> material;
	private List<WardrobeModel> wardrobe;

	public AccountModel(String api) {
		super("");
		this.api = api;
		initLists();
	}

	//this is for account source to populate account with info
	public AccountModel(String api, String id, String name, int worldID, String world, Account.Access access, boolean isValid) {
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

	public List<BankItemModel> getBank() {
		return bank;
	}

	public void setBank(List<BankItemModel> bank) {
		this.bank = bank;
	}

	public List<MaterialStorageModel> getMaterial() {
		return material;
	}

	public void setMaterial(List<MaterialStorageModel> material) {
		this.material = material;
	}

	public List<WardrobeModel> getWardrobe() {
		return wardrobe;
	}

	public void setWardrobe(List<WardrobeModel> wardrobe) {
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

	public void setAllCharacters(List<CharacterModel> characters) {
		this.characters = characters;
	}

	public List<CharacterModel> getAllCharacters() {
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

		AccountModel that = (AccountModel) o;

		return api != null ? api.equals(that.api) : that.api == null;
	}

	@Override
	public int hashCode() {
		return api.hashCode();
	}

	@Override
	public String toString() {
		return "AccountModel{" +
				"api='" + api + '\'' +
				'}';
	}
}
