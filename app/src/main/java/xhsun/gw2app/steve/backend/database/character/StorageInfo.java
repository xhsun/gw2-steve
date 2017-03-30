package xhsun.gw2app.steve.backend.database.character;

import xhsun.gw2api.guildwars2.model.util.Storage;

/**
 * Storage data type
 *
 * @author xhsun
 * @since 2017-03-29
 */

public class StorageInfo {
	private long id;
	private long itemID;
	private String itemName;
	private String characterName;
	private String api;
	private int count;
	private long skinID;
	private long statsID;
	private Storage.Binding binding;//null if no binding
	private String boundTo;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getItemID() {
		return itemID;
	}

	public void setItemID(long itemID) {
		this.itemID = itemID;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getCharacterName() {
		return characterName;
	}

	public void setCharacterName(String characterName) {
		this.characterName = characterName;
	}

	public String getApi() {
		return api;
	}

	public void setApi(String api) {
		this.api = api;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public long getSkinID() {
		return skinID;
	}

	public void setSkinID(long skinID) {
		this.skinID = skinID;
	}

	public long getStatsID() {
		return statsID;
	}

	public void setStatsID(long statsID) {
		this.statsID = statsID;
	}

	public Storage.Binding getBinding() {
		return binding;
	}

	public void setBinding(Storage.Binding binding) {
		this.binding = binding;
	}

	public String getBoundTo() {
		return boundTo;
	}

	public void setBoundTo(String boundTo) {
		this.boundTo = boundTo;
	}
}
