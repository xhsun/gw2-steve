package xhsun.gw2app.steve.backend.database.common;

import java.util.List;

import xhsun.gw2api.guildwars2.GuildWars2;
import xhsun.gw2api.guildwars2.err.GuildWars2Exception;
import xhsun.gw2api.guildwars2.model.Skin;
import xhsun.gw2app.steve.backend.data.SkinInfo;

/**
 * for manipulate skins
 *
 * @author xhsun
 * @since 2017-05-04
 */

public class SkinWrapper {
	private GuildWars2 wrapper;
	private SkinDB skinDB;

	public SkinWrapper(GuildWars2 wrapper, SkinDB skinDB) {
		this.wrapper = wrapper;
		this.skinDB = skinDB;
	}

	/**
	 * get skin info
	 *
	 * @param id skin if
	 * @return skin info | null if not find
	 */
	public SkinInfo get(long id) {
		return skinDB.get(id);
	}

	/**
	 * get all skin that is in the database
	 *
	 * @return list of all skin info | empty if not find
	 */
	public List<SkinInfo> getAll() {
		return skinDB.getAll();
	}

	/**
	 * add if the skin is not in database | update if the skin is already in the database
	 *
	 * @param id skin id
	 * @return skin info on success | null otherwise
	 */
	public SkinInfo update(long id) {
		try {
			List<Skin> origin = wrapper.getSkinInfo(new long[]{id});
			if (origin.size() < 1) return null;

			Skin skin = origin.get(0);
			if (skinDB.replace(skin.getId(), skin.getName(), skin.getType(), skin.getIcon(),
					skin.getRarity(), skin.getRestrictions(), skin.getFlags(), skin.getDescription()))
				return new SkinInfo(skin);
		} catch (GuildWars2Exception ignored) {
		}
		return null;
	}

//	/**
//	 * add if any given skin is not in database, update if it is in
//	 *
//	 * @param ids list of skin ids
//	 * @return list of skin info on success | empty otherwise
//	 */
//	public Set<SkinInfo> update(long[] ids) {
//		Set<SkinInfo> info = new HashSet<>();
//		try {
//			List<Skin> origin = wrapper.getSkinInfo(ids);
//			if (origin.size() < 1) return null;
//
//			for (Skin s : origin) {
//				SkinInfo temp = new SkinInfo(s);
//				if (skinDB.replace(s.getId(), s.getName(), s.getType(), s.getIcon(),
//						s.getRarity(), s.getRestrictions(), s.getFlags(), s.getDescription())) {
//					if (!info.contains(temp)) info.add(temp);
//				}
//			}
//		} catch (GuildWars2Exception ignored) {
//		}
//		return info;
//	}

	/**
	 * remove skin from database
	 *
	 * @param id skin id
	 */
	public void delete(long id) {
		skinDB.delete(id);
	}
}
