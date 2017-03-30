package xhsun.gw2app.steve.backend.database.character;

import javax.inject.Inject;

import xhsun.gw2api.guildwars2.GuildWars2;

/**
 * Created by hannah on 29/03/17.
 */

public class StorageWrapper {
	private GuildWars2 wrapper;
	private CharacterDB character;
	private StorageDB storage;

	@Inject
	public StorageWrapper(CharacterDB character, StorageDB storage, GuildWars2 wrapper) {
		this.character = character;
		this.storage = storage;
		this.wrapper = wrapper;
	}
}
