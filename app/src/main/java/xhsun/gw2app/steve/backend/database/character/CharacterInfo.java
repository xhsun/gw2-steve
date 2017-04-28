package xhsun.gw2app.steve.backend.database.character;

import android.support.v7.widget.RecyclerView;

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
	private List<StorageInfo> filtered;
	private String name;
	private String api;
	private Item.Restriction race;
	private Core.Gender gender;
	private Item.Restriction profession;
	private int level;
	private RecyclerView child;

//	CharacterInfo(String api, Core core) {
//		name = core.getName();
//		this.api = api;
//		race = core.getRace();
//		gender = core.getGender();
//		profession = core.getProfession();
//		level = core.getLevel();
//		inventory = new ArrayList<>();
//	}

	public CharacterInfo(String name) {
		this.name = name;
		inventory = new ArrayList<>();
	}

	public CharacterInfo(String api, String name) {
		this.api = api;
		this.name = name;
		inventory = new ArrayList<>();
	}

	CharacterInfo() {
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

	public void setInventory(List<StorageInfo> inventory) {
		this.inventory = inventory;
	}

	public List<StorageInfo> getInventory() {
		return inventory;
	}

	public void update(CharacterInfo info) {
		race = info.race;
		gender = info.gender;
		profession = info.profession;
		level = info.level;
	}

	public List<StorageInfo> getFiltered() {
		return filtered;
	}

	public void setFiltered(List<StorageInfo> filtered) {
		this.filtered = filtered;
	}

	public RecyclerView getChild() {
		return child;
	}

	public void setChild(RecyclerView child) {
		this.child = child;
	}

	@Override
	public int hashCode() {
		int result = name.hashCode();
		result = 31 * result + (api != null ? api.hashCode() : 0);
		result = 31 * result + (race != null ? race.hashCode() : 0);
		result = 31 * result + (gender != null ? gender.hashCode() : 0);
		result = 31 * result + (profession != null ? profession.hashCode() : 0);
		result = 31 * result + level;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		return this == obj || obj != null && getClass() == obj.getClass() && ((CharacterInfo) obj).name.equals(name);
	}
}
