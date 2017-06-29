package xhsun.gw2app.steve.backend.data.wrapper.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.xhsun.guildwars2wrapper.GuildWars2;
import me.xhsun.guildwars2wrapper.SynchronousRequest;
import me.xhsun.guildwars2wrapper.error.GuildWars2Exception;
import me.xhsun.guildwars2wrapper.model.identifiable.Unlockable;
import me.xhsun.guildwars2wrapper.model.v2.Mini;
import xhsun.gw2app.steve.backend.data.model.MiscItemModel;

import static xhsun.gw2app.steve.backend.util.Utility.ID_LIMIT;

/**
 * For manipulate misc item
 *
 * @author xhsun
 * @since 2017-06-27
 */

public class MiscWrapper {
	private SynchronousRequest request;
	private MiscDB miscDB;

	public MiscWrapper(GuildWars2 wrapper, MiscDB miscDB) {
		request = wrapper.getSynchronous();
		this.miscDB = miscDB;
	}

	public void bulkInsert(MiscItemModel.MiscItemType type, int[] ids) {
		int size = ids.length;
		if (size < 1) return;

		if (type == MiscItemModel.MiscItemType.MINI) {
			bulkInsertMini(size, ids);
			return;
		}

		List<Unlockable> origin = new ArrayList<>();
		try {
			if (size <= ID_LIMIT) origin = getContent(type, ids);
			else {
				int count = 0, limit = (size / ID_LIMIT) + 1;
				for (int i = 1; i <= limit; i++) {
					int[] newIds = Arrays.copyOfRange(ids, ID_LIMIT * (count++), (i == limit) ? size : i * ID_LIMIT);
					origin.addAll(getContent(type, newIds));
				}
			}
			if (origin.size() >= 1) miscDB.bulkReplace(type, origin);
		} catch (GuildWars2Exception ignored) {
		}
	}

	/**
	 * get all item that is in the database
	 *
	 * @return list of all item info | empty if not find
	 */
	public List<MiscItemModel> getAll() {
		return miscDB.getAll();
	}

//	/**
//	 * get item info
//	 *
//	 * @param type item type
//	 * @param id   item id
//	 * @return item info | null if not find
//	 */
//	public MiscItemModel get(MiscItemModel.MiscItemType type, int id) {
//		return miscDB.get(type, id);
//	}

	/**
	 * remove item from database
	 *
	 * @param type item type
	 * @param id   item id
	 */
	public void delete(MiscItemModel.MiscItemType type, int id) {
		miscDB.delete(type, id);
	}


	private void bulkInsertMini(int size, int[] ids) {
		List<Mini> origin = new ArrayList<>();
		try {
			if (size <= ID_LIMIT) origin = request.getMiniInfo(ids);
			else {
				int count = 0, limit = (size / ID_LIMIT) + 1;
				for (int i = 1; i <= limit; i++) {
					int[] newIds = Arrays.copyOfRange(ids, ID_LIMIT * (count++), (i == limit) ? size : i * ID_LIMIT);
					origin.addAll(request.getMiniInfo(newIds));
				}
			}
			if (origin.size() >= 1) miscDB.bulkReplaceMini(origin);
		} catch (GuildWars2Exception ignored) {
		}
	}

	private List<Unlockable> getContent(MiscItemModel.MiscItemType type, int[] ids) throws GuildWars2Exception {
		switch (type) {
			case GLIDER:
				return new ArrayList<>(request.getGliderInfo(ids));
			case MAILCARRIER:
				return new ArrayList<>(request.getMailCarrierInfo(ids));
			case OUTFIT:
				return new ArrayList<>(request.getOutfitInfo(ids));
			case FINISHER:
				return new ArrayList<>(request.getFinisherInfo(ids));
			default:
				return new ArrayList<>();
		}
	}
}
