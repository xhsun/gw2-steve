package xhsun.gw2app.steve.backend.data;

import java.util.ArrayList;
import java.util.List;

import me.xhsun.guildwars2wrapper.model.v2.Item;
import me.xhsun.guildwars2wrapper.model.v2.character.CharacterCore;
import xhsun.gw2app.steve.backend.data.vault.item.InventoryItemData;

/**
 * Character data type
 *
 * @author xhsun
 * @since 2017-03-29
 */

public class CharacterData extends AbstractData {
	private String api;
	private Item.Restriction race;
	private CharacterCore.Gender gender;
	private Item.Restriction profession;
	private int level;
	private List<InventoryItemData> inventory;

	public CharacterData() {
		super("");
		inventory = new ArrayList<>();
	}

	public CharacterData(String name) {
		super(name);
		inventory = new ArrayList<>();
	}

	public CharacterData(String api, String name) {
		super(name);
		this.api = api;
		inventory = new ArrayList<>();
	}

//	public CharacterData(CharacterData info) {
//		super(info.getName());
//		this.api = info.getApi();
//		this.race = info.getRace();
//		this.gender = info.getGender();
//		this.profession = info.getProfession();
//		this.level = info.getLevel();
//		this.inventory = info.getInventory();
//	}
//
//	CharacterData(String api, Core core) {
//		name = core.getName();
//		this.api = api;
//		race = core.getRace();
//		gender = core.getGender();
//		profession = core.getProfession();
//		level = core.getLevel();
//		inventory = new ArrayList<>();
//	}

	public String getApi() {
		return api;
	}

	public void setApi(String api) {
		this.api = api;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Item.Restriction getRace() {
		return race;
	}

	public void setRace(Item.Restriction race) {
		this.race = race;
	}

	public CharacterCore.Gender getGender() {
		return gender;
	}

	public void setGender(CharacterCore.Gender gender) {
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

	public void setInventory(List<InventoryItemData> inventory) {
		this.inventory = inventory;
	}

	public List<InventoryItemData> getInventory() {
		return inventory;
	}

	public void update(CharacterData info) {
		race = info.race;
		gender = info.gender;
		profession = info.profession;
		level = info.level;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		CharacterData that = (CharacterData) o;

		return name != null ? name.equals(that.name) : that.name == null;
	}

	@Override
	public int hashCode() {
		return name != null ? name.hashCode() : 0;
	}

	@Override
	public String toString() {
		return "CharacterData{" +
				"name='" + name +
				", api='" + api + '\'' +
				'}';
	}
}
