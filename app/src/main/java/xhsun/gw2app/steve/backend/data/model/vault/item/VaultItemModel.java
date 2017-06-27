package xhsun.gw2app.steve.backend.data.model.vault.item;

import me.xhsun.guildwars2wrapper.model.v2.util.Storage;
import xhsun.gw2app.steve.backend.data.model.AbstractModel;
import xhsun.gw2app.steve.backend.data.model.ItemModel;
import xhsun.gw2app.steve.backend.data.model.SkinModel;

/**
 * Storage data type
 *
 * @author xhsun
 * @since 2017-03-29
 */

public abstract class VaultItemModel extends AbstractModel {
	private int id = -1;
	String api = "";
	ItemModel itemModel;
	SkinModel skinModel;
	Storage.Binding binding;//null if no binding
	String boundTo = "";

	VaultItemModel(String name) {
		super(name);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getApi() {
		return api;
	}

	public void setApi(String api) {
		this.api = api;
	}

	public ItemModel getItemModel() {
		return itemModel;
	}

	public SkinModel getSkinModel() {
		return skinModel;
	}
}
