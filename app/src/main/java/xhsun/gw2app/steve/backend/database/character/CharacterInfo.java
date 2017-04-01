package xhsun.gw2app.steve.backend.database.character;

import java.util.ArrayList;
import java.util.List;

import xhsun.gw2api.guildwars2.model.Item;
import xhsun.gw2api.guildwars2.model.character.Core;

/**
 * Character data type
 *
 * @author xhsun
 * @since 2017-03-29
 */

public class CharacterInfo {
	private List<StorageInfo> inventory;
	private String name;
	private String api;
	private Item.Restriction race;
	private Core.Gender gender;
	private Item.Restriction profession;
	private int level;
	private boolean isEnabled = true;

	public CharacterInfo(String name) {
		this.name = name;
		inventory = new ArrayList<>();
	}

	public CharacterInfo() {
		inventory = new ArrayList<>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getApi() {
		return api;
	}

	public void setApi(String api) {
		this.api = api;
	}

	public Item.Restriction getRace() {
		return race;
	}

	public void setRace(Item.Restriction race) {
		this.race = race;
	}

	public Core.Gender getGender() {
		return gender;
	}

	public void setGender(Core.Gender gender) {
		this.gender = gender;
	}

	public Item.Restriction getProfession() {
		return profession;
	}

	public void setProfession(Item.Restriction profession) {
		this.profession = profession;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public List<StorageInfo> getInventory() {
		return inventory;
	}

	public void setInventory(List<StorageInfo> inventory) {
		this.inventory = inventory;
	}

	public boolean isEnabled() {
		return isEnabled;
	}

	public void setEnabled(boolean enabled) {
		isEnabled = enabled;
	}

	@Override
	public int hashCode() {
		return api.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return this == obj || obj != null && getClass() == obj.getClass() && ((CharacterInfo) obj).name.equals(name);
	}
}
