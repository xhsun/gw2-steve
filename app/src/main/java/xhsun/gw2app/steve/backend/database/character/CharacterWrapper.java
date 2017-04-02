package xhsun.gw2app.steve.backend.database.character;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;
import xhsun.gw2api.guildwars2.GuildWars2;
import xhsun.gw2api.guildwars2.err.GuildWars2Exception;
import xhsun.gw2api.guildwars2.model.character.Core;
import xhsun.gw2app.steve.backend.database.account.AccountInfo;
import xhsun.gw2app.steve.backend.database.account.AccountWrapper;

/**
 * For manipulate characters in database
 *
 * @author xhsun
 * @since 2017-03-30
 */

public class CharacterWrapper {
	private GuildWars2 wrapper;
	private CharacterDB characterDB;
	private AccountWrapper accountWrapper;
	private boolean isCancelled = false;

	@Inject
	public CharacterWrapper(GuildWars2 wrapper, AccountWrapper accountWrapper, CharacterDB characterDB) {
		this.wrapper = wrapper;
		this.accountWrapper = accountWrapper;
		this.characterDB = characterDB;
	}

	/**
	 * get all character information that is in the database
	 *
	 * @return list of character | empty if not find
	 */
	public List<CharacterInfo> getAll() {
		return characterDB.getAll();
	}

	/**
	 * get all character info for given account
	 *
	 * @param api API key
	 * @return list of character | empty if not find
	 */
	public List<CharacterInfo> getAll(String api) {
		return characterDB.getAll(api);
	}

	/**
	 * get all character names for this account from server
	 * @param api API key
	 * @return list of character name | empty if not find or error
	 * @throws GuildWars2Exception server issue/network error
	 */
	public List<String> getAllNames(String api) throws GuildWars2Exception {
		if (isCancelled) return new ArrayList<>();
		try {
			return wrapper.getAllCharacterName(api);
		} catch (GuildWars2Exception e) {
			Timber.e(e, "ERROR when trying to get character names for %s", api);
			switch (e.getErrorCode()) {
				case Server:
				case Limit:
				case Network:
					throw e;
				case Key://mark account invalid and remove character from database
					accountWrapper.markInvalid(new AccountInfo(api));
			}
		}
		return new ArrayList<>();
	}

	/**
	 * update or insert given character
	 *
	 * @param api  API key
	 * @param name character name
	 * @throws GuildWars2Exception server issue\network error
	 */
	public void update(String api, String name) throws GuildWars2Exception {
		if (isCancelled) return;
		try {
			Core character = wrapper.getCharacterInformation(api, name);
			if (characterDB.get(name) != null)
				characterDB.update(name, character.getRace(), character.getGender(), character.getProfession(), character.getLevel());
			else
				characterDB.add(name, api, character.getRace(), character.getGender(), character.getProfession(), character.getLevel());
		} catch (GuildWars2Exception e) {
			Timber.e(e, "ERROR when trying to update character info for (%s, %s)", name, api);
			switch (e.getErrorCode()) {
				case Server:
				case Limit:
				case Network:
					throw e;
				case Key://mark account invalid and remove character from database
					accountWrapper.markInvalid(new AccountInfo(api));
				case Character://remove character from database
					characterDB.delete(name);
			}
		}
	}

	public void setCancelled(boolean cancelled) {
		isCancelled = cancelled;
	}

	/**
	 * remove character from database
	 * @param name character name
	 */
	public void delete(String name) {
		characterDB.delete(name);
	}

	/**
	 * get character information in the database
	 *
	 * @param name character name
	 * @return character info | null if not exist
	 */
	public CharacterInfo get(String name) {
		return characterDB.get(name);
	}
}
