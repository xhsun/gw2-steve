package xhsun.gw2app.steve.backend.data.wrapper.storage;

import android.util.SparseArray;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.util.ArrayList;
import java.util.List;

import me.xhsun.guildwars2wrapper.GuildWars2;
import me.xhsun.guildwars2wrapper.SynchronousRequest;
import me.xhsun.guildwars2wrapper.error.GuildWars2Exception;
import me.xhsun.guildwars2wrapper.model.v2.MaterialCategory;
import timber.log.Timber;
import xhsun.gw2app.steve.backend.data.model.AccountModel;
import xhsun.gw2app.steve.backend.data.model.ItemModel;
import xhsun.gw2app.steve.backend.data.model.vault.MaterialStorageModel;
import xhsun.gw2app.steve.backend.data.model.vault.item.MaterialItemModel;
import xhsun.gw2app.steve.backend.data.wrapper.account.AccountWrapper;
import xhsun.gw2app.steve.backend.data.wrapper.common.ItemWrapper;

/**
 * for manipulate material storage item
 *
 * @author xhsun
 * @since 2017-05-04
 */

public class MaterialWrapper extends StorageWrapper<MaterialStorageModel, MaterialItemModel> {
	private SparseArray<String> categoryName = null;
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
	public List<MaterialStorageModel> update(String api) throws GuildWars2Exception {
		Timber.i("Start updating material storage info for %s", api);
		try {
			List<Integer> categories = request.getAllMaterialCategoryID();
			List<MaterialItemModel> materials = new ArrayList<>(), original = Stream.of(get(api))
					.flatMap(m -> Stream.of(m.getItems())).collect(Collectors.toList());

			//populate map of id and name
			if (categoryName == null) {
				categoryName = new SparseArray<>();
				Stream.of(original)
						.collect(Collectors.toMap(MaterialItemModel::getCategoryID, MaterialItemModel::getCategoryName))
						.forEach((l, s) -> {
							if (categoryName.indexOfKey(l) < 0) categoryName.put(l, s);
						});
			}

			//see if we need to update category names
			if (categoryName.size() < categories.size()) {
				Stream.of(request.getMaterialCategoryInfo(Stream.of(categories)
						.filter(i -> categoryName.indexOfKey(i) < 0)
						.mapToInt(Integer::intValue).toArray()))
						.collect(Collectors.toMap(MaterialCategory::getId, MaterialCategory::getName))
						.forEach((i, n) -> categoryName.put(i, n));
			}

			//get material storage
			Stream.of(request.getMaterialStorage(api)).withoutNulls().filterNot(i -> i.getCount() < 1)
					.forEach(m -> materials.add(new MaterialItemModel(api, categoryName.get(m.getCategory()), m)));

			//start insert or update
			if (original.size() < 1) startInsert(materials);
			else startUpdate(original, materials);
		} catch (GuildWars2Exception e) {
			Timber.e(e, "Error occurred when trying to get bank information for %s", api);
			switch (e.getErrorCode()) {
				case Server:
				case Limit:
				case Network:
					throw e;
				case Key://mark account invalid
					accountWrapper.markInvalid(new AccountModel(api));
			}
		}

		return get(api);
	}

	@Override
	protected void checkBaseItem(List<MaterialItemModel> data) {
		List<Integer> oItem = Stream.of(itemWrapper.getAll()).map(ItemModel::getId).collect(Collectors.toList());

		itemWrapper.bulkInsert(Stream.of(data).filterNot(i -> oItem.contains(i.getItemModel().getId()))
				.map(i -> i.getItemModel().getId()).mapToInt(Integer::intValue).toArray());
	}

	@Override
	protected void checkOriginal(MaterialItemModel old, MaterialItemModel current) {
		updateIfDifferent(old, current);
	}

	protected void updateDB(MaterialItemModel info) {
		if (isCancelled) return;
		if (info.getCategoryName().equals("")) {
			String category;
			if ((category = getCategoryName(info.getCategoryID())).equals("")) return;
			info.setCategoryName(category);
		}
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
