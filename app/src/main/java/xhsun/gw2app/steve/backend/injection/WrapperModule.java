package xhsun.gw2app.steve.backend.injection;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import xhsun.gw2api.guildwars2.GuildWars2;
import xhsun.gw2app.steve.backend.database.account.AccountDB;
import xhsun.gw2app.steve.backend.database.account.AccountWrapper;
import xhsun.gw2app.steve.backend.database.character.CharacterDB;
import xhsun.gw2app.steve.backend.database.character.StorageDB;
import xhsun.gw2app.steve.backend.database.character.StorageWrapper;
import xhsun.gw2app.steve.backend.database.wallet.CurrencyDB;
import xhsun.gw2app.steve.backend.database.wallet.WalletDB;
import xhsun.gw2app.steve.backend.database.wallet.WalletWrapper;

/**
 * Module class for wrappers
 *
 * @author xhsun
 * @since 2017-03-16
 */
@Module(includes = DatabaseModule.class)
public class WrapperModule {
	@Provides
	@Singleton
	GuildWars2 providesServerWrapper() {
		return GuildWars2.getInstance();
	}

	@Provides
	@Singleton
	AccountWrapper providesAccountWrapper(AccountDB database, GuildWars2 wrapper) {
		return new AccountWrapper(database, wrapper);
	}

	@Provides
	@Singleton
	WalletWrapper providesWalletWrapper(WalletDB wallet, CurrencyDB currency, GuildWars2 wrapper, AccountWrapper accountWrapper) {
		return new WalletWrapper(wallet, currency, wrapper, accountWrapper);
	}

	@Provides
	@Singleton
	StorageWrapper providesStorageWrapper(CharacterDB character, StorageDB storage, GuildWars2 wrapper) {
		return new StorageWrapper(character, storage, wrapper);
	}
}
