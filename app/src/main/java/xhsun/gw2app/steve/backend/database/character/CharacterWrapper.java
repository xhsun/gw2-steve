package xhsun.gw2app.steve.backend.database.character;

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
	 * update all character info for this account
	 *
	 * @param account account info
	 * @return list of all character info | empty if not find
	 * @throws GuildWars2Exception server issue
	 */
	public List<CharacterInfo> update(final AccountInfo account) throws GuildWars2Exception {
		String api = account.getAPI();
		Timber.i("Start update character information for %s", account.getName());
		List<String> characterNames = wrapper.getAllCharacterName(api);
		for (final String name : characterNames) {
			if (isCancelled) break;
			try {
				Core character = wrapper.getCharacterInformation(api, name);
				characterDB.replace(name, api, character.getRace(), character.getGender(), character.getProfession(), character.getLevel());
			} catch (GuildWars2Exception e) {
				Timber.e(e, "Error when trying to update character information");
				switch (e.getErrorCode()) {
					case Server:
					case Limit:
					case Network:
						throw e;
					case Key://mark account invalid and remove character from database
						accountWrapper.markInvalid(account);
					case Character://remove character from database
						characterDB.delete(name);
				}
			}
		}
		return getAll(api);
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
}
