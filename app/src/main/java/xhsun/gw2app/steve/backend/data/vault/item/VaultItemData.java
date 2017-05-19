package xhsun.gw2app.steve.backend.data.vault.item;

import xhsun.gw2api.guildwars2.model.util.Storage;
import xhsun.gw2app.steve.backend.data.AbstractData;
import xhsun.gw2app.steve.backend.data.ItemData;
import xhsun.gw2app.steve.backend.data.SkinData;

/**
 * Storage data type
 *
 * @author xhsun
 * @since 2017-03-29
 */

public abstract class VaultItemData extends AbstractData {
	private long id = -1;
	String api = "";
	ItemData itemData;
	SkinData skinData;
	Storage.Binding binding;//null if no binding
	String boundTo = "";

	protected VaultItemData(String name) {
		super(name);
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getApi() {
		return api;
	}

	public void setApi(String api) {
		this.api = api;
	}

	public ItemData getItemData() {
		return itemData;
	}

	public SkinData getSkinData() {
		return skinData;
	}
}
