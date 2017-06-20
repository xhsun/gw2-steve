package xhsun.gw2app.steve.backend.database.storage;

import android.support.v4.util.LongSparseArray;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.util.ArrayList;
import java.util.List;

import me.xhsun.guildwars2wrapper.GuildWars2;
import me.xhsun.guildwars2wrapper.SynchronousRequest;
import me.xhsun.guildwars2wrapper.error.GuildWars2Exception;
import me.xhsun.guildwars2wrapper.model.v2.MaterialCategory;
import me.xhsun.guildwars2wrapper.model.v2.account.MaterialStorage;
import timber.log.Timber;
import xhsun.gw2app.steve.backend.data.AccountData;
import xhsun.gw2app.steve.backend.data.vault.MaterialStorageData;
import xhsun.gw2app.steve.backend.data.vault.item.Countable;
import xhsun.gw2app.steve.backend.data.vault.item.MaterialItemData;
import xhsun.gw2app.steve.backend.database.account.AccountWrapper;
import xhsun.gw2app.steve.backend.database.common.ItemWrapper;

/**
 * for manipulate material storage item
 *
 * @author xhsun
 * @since 2017-05-04
 */

public class MaterialWrapper extends StorageWrapper<MaterialStorageData, MaterialItemData> {
	private LongSparseArray<String> categoryName;
	private SynchronousRequest request;
	private ItemWrapper itemWrapper;
	private AccountWrapper accountWrapper;

	public MaterialWrapper(GuildWars2 wrapper, AccountWrapper accountWrapper, ItemWrapper itemWrapper,
	                       MaterialDB materialDB) {
		super(materialDB);
		request = wrapper.getSynchronous();
		this.accountWrapper = accountWrapper;
		this.itemWrapper = itemWrapper;
	}

	/**
	 * update material info for given account
	 *
	 * @param api API key
	 * @return updated list of materials for this account
	 * @throws GuildWars2Exception error when interacting with server
	 */
	public List<MaterialStorageData> update(String api) throws GuildWars2Exception {
		Timber.i("Start updating material storage info for %s", api);
		try {
			List<MaterialItemData> original = Stream.of(get(api)).flatMap(m -> Stream.of(m.getItems())).collect(Collectors.toList());
			//populate map of id and name
			categoryName = new LongSparseArray<>();
			Stream.of(original)
					.collect(Collectors.toMap(MaterialItemData::getCategoryID, MaterialItemData::getCategoryName))
					.forEach((l, s) -> {
						if (categoryName.indexOfKey(l) < 0) categoryName.put(l, s);
					});

			startUpdate(api, original, request.getMaterialStorage(api));
		} catch (GuildWars2Exception e) {
			Timber.e(e, "Error occurred when trying to get bank information for %s", api);
			switch (e.getErrorCode()) {
				case Server:
				case Limit:
				case Network:
					throw e;
				case Key://mark account invalid
					accountWrapper.markInvalid(new AccountData(api));
			}
		}

		return get(api);
	}

	//update or add item to material storage
	private void startUpdate(String api, List<MaterialItemData> original, List<MaterialStorage> bank) {
		List<Countable> known = new ArrayList<>(original);
		List<Countable> seen = new ArrayList<>();
		for (MaterialStorage b : bank) {
			if (isCancelled) return;
			if (b == null || b.getCount() == 0) continue;//nothing here, move on
			updateRecord(known, seen, new MaterialItemData(api, b));
		}

		//remove all outdated storage item from database
		for (Countable i : known) {
			if (isCancelled) return;
			delete((MaterialItemData) i);
		}
	}

	@Override
	protected void updateDatabase(MaterialItemData info, boolean isItemSeen) {
		if (isCancelled) return;
		String category;
		if ((category = getCategoryName(info.getCategoryID())).equals("")) return;
		info.setCategoryName(category);
		//insert item if needed
		if (!isItemSeen && itemWrapper.get(info.getItemData().getId()) == null)
			itemWrapper.update(info.getItemData().getId());
		replace(info);//update
	}

	private String getCategoryName(int id) {
		if (categoryName.indexOfKey(id) < 0) {
			try {
				List<MaterialCategory> categories = request.getMaterialCategoryInfo(new int[]{id});
				if (!categories.isEmpty()) {
					String name = categories.get(0).getName();
					categoryName.put(id, name);
					return name;
				}
			} catch (GuildWars2Exception ignored) {
			}
		} else {
			return categoryName.get(id);
		}
		return "";
	}
}
