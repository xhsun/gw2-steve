package xhsun.gw2app.steve.backend.database.character;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import xhsun.gw2api.guildwars2.GuildWars2;
import xhsun.gw2api.guildwars2.err.ErrorCode;
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
	 * @param api API key
	 * @return list of all character info | empty if not find
	 * @throws GuildWars2Exception server issue
	 */
	public List<CharacterInfo> update(final String api) throws GuildWars2Exception {
		try {
			List<String> characterNames = wrapper.getAllCharacterName(api);
			for (final String name : characterNames)
				wrapper.characterInformationProcessor(api, name, new Callback<Core>() {
					@SuppressWarnings("ThrowableResultOfMethodCallIgnored")
					@Override
					public void onResponse(Call<Core> call, Response<Core> response) {
						if (response.isSuccessful()) {
							Core info = response.body();
							characterDB.replace(name, api, info.getRace(), info.getGender(), info.getProfession(), info.getLevel());
						} else {
							try {
								checkCharacterValid(ErrorCode.checkErrorResponse(response.code(), response.errorBody().string()).getErrorCode(), new AccountInfo(api), name);
							} catch (IOException ignored) {
							}
						}
					}

					@Override
					public void onFailure(Call<Core> call, Throwable throwable) {
					}
				});
		} catch (GuildWars2Exception e) {
			switch (e.getErrorCode()) {
				case Server:
				case Limit:
					throw e;
				case Key:
					accountWrapper.markInvalid(new AccountInfo(api));
			}
		}
		return getAll(api);
	}

	/**
	 * check to see if the character need to get deleted
	 *
	 * @param code    error code
	 * @param account account info
	 * @param name    character name
	 */
	public void checkCharacterValid(ErrorCode code, AccountInfo account, String name) {
		switch (code) {
			case Key:
				accountWrapper.markInvalid(account);
			case Character:
				characterDB.delete(name);
		}
	}
}
