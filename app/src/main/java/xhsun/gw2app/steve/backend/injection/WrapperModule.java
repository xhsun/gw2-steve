package xhsun.gw2app.steve.backend.injection;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import xhsun.gw2api.guildwars2.GuildWars2;
import xhsun.gw2app.steve.backend.database.account.AccountDB;
import xhsun.gw2app.steve.backend.database.account.AccountWrapper;
import xhsun.gw2app.steve.backend.database.character.CharacterDB;
import xhsun.gw2app.steve.backend.database.character.CharacterWrapper;
import xhsun.gw2app.steve.backend.database.character.StorageDB;
import xhsun.gw2app.steve.backend.database.character.StorageWrapper;
import xhsun.gw2app.steve.backend.database.common.CurrencyDB;
import xhsun.gw2app.steve.backend.database.common.CurrencyWrapper;
import xhsun.gw2app.steve.backend.database.common.ItemDB;
import xhsun.gw2app.steve.backend.database.common.ItemWrapper;
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
	CurrencyWrapper providesCurrencyWrapper(CurrencyDB currency) {
		return new CurrencyWrapper(currency);
	}

	@Provides
	@Singleton
	ItemWrapper providesItemWrapper(GuildWars2 wrapper, ItemDB itemDB) {
		return new ItemWrapper(wrapper, itemDB);
	}

	@Provides
	@Singleton
	CharacterWrapper providesCharacterWrapper(GuildWars2 wrapper, AccountWrapper accountWrapper, CharacterDB characterDB) {
		return new CharacterWrapper(wrapper, accountWrapper, characterDB);
	}

	@Provides
	@Singleton
	WalletWrapper providesWalletWrapper(WalletDB wallet, CurrencyWrapper currency, GuildWars2 wrapper, AccountWrapper accountWrapper) {
		return new WalletWrapper(wallet, currency, wrapper, accountWrapper);
	}

	@Provides
	@Singleton
	StorageWrapper providesStorageWrapper(GuildWars2 wrapper, AccountWrapper accountWrapper, CharacterWrapper characterWrapper, ItemWrapper itemWrapper, StorageDB storage) {
		return new StorageWrapper(wrapper, accountWrapper, characterWrapper, itemWrapper, storage);
	}
}
