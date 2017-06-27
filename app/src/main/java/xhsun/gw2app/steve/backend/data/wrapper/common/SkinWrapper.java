package xhsun.gw2app.steve.backend.data.wrapper.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.xhsun.guildwars2wrapper.GuildWars2;
import me.xhsun.guildwars2wrapper.SynchronousRequest;
import me.xhsun.guildwars2wrapper.error.GuildWars2Exception;
import me.xhsun.guildwars2wrapper.model.v2.Skin;
import xhsun.gw2app.steve.backend.data.model.SkinModel;

import static xhsun.gw2app.steve.backend.util.Utility.ID_LIMIT;

/**
 * for manipulate skins
 *
 * @author xhsun
 * @since 2017-05-04
 */

public class SkinWrapper {
	private SynchronousRequest request;
	private SkinDB skinDB;

	public SkinWrapper(GuildWars2 wrapper, SkinDB skinDB) {
		request = wrapper.getSynchronous();
		this.skinDB = skinDB;
	}

	/**
	 * get skin info
	 *
	 * @param id skin if
	 * @return skin info | null if not find
	 */
	public SkinModel get(int id) {
		return skinDB.get(id);
	}

	/**
	 * get all skin that is in the database
	 *
	 * @return list of all skin info | empty if not find
	 */
	public List<SkinModel> getAll() {
		return skinDB.getAll();
	}

	/**
	 * add if the skin is not in database | update if the skin is already in the database
	 *
	 * @param id skin id
	 * @return skin info on success | null otherwise
	 */
	public SkinModel update(int id) {
		try {
			List<Skin> origin = request.getSkinInfo(new int[]{id});
			if (origin.size() < 1) return null;

			Skin skin = origin.get(0);
			if (skinDB.replace(skin.getId(), skin.getName(), skin.getType(),
					(skin.getDetails() == null) ? null : skin.getDetails().getType(),
					skin.getIcon(), skin.getRarity(), skin.getRestrictions(), skin.getFlags(), skin.getDescription()))
				return new SkinModel(skin);
		} catch (GuildWars2Exception ignored) {
		}
		return null;
	}

	public void bulkInsert(int[] ids) {
		int size = ids.length;
		if (size < 1) return;

		List<Skin> origin = new ArrayList<>();
		try {
			if (size <= ID_LIMIT) origin = request.getSkinInfo(ids);
			else {
				int count = 0, limit = (size / ID_LIMIT) + 1;
				for (int i = 1; i <= limit; i++) {
					int[] newIds = Arrays.copyOfRange(ids, ID_LIMIT * (count++), (i == limit) ? size : i * ID_LIMIT);
					origin.addAll(request.getSkinInfo(newIds));
				}
			}
			if (origin.size() >= 1) skinDB.bulkReplace(origin);
		} catch (GuildWars2Exception ignored) {
		}
	}

	/**
	 * remove skin from database
	 *
	 * @param id skin id
	 */
	public void delete(int id) {
		skinDB.delete(id);
	}
}
