package xhsun.gw2app.steve.backend.data.wrapper.common;

import java.util.List;

import me.xhsun.guildwars2wrapper.GuildWars2;
import me.xhsun.guildwars2wrapper.SynchronousRequest;
import me.xhsun.guildwars2wrapper.error.GuildWars2Exception;
import me.xhsun.guildwars2wrapper.model.v2.Skin;
import xhsun.gw2app.steve.backend.data.model.SkinModel;

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

//	/**
//	 * add if any given skin is not in database, update if it is in
//	 *
//	 * @param ids list of skin ids
//	 * @return list of skin info on success | empty otherwise
//	 */
//	public Set<SkinModel> update(long[] ids) {
//		Set<SkinModel> info = new HashSet<>();
//		try {
//			List<Skin> origin = wrapper.getSkinModel(ids);
//			if (origin.size() < 1) return null;
//
//			for (Skin s : origin) {
//				SkinModel temp = new SkinModel(s);
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
	public void delete(int id) {
		skinDB.delete(id);
	}
}
