package xhsun.gw2app.steve.backend.injection;

import android.content.Context;

import java.io.File;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import xhsun.gw2api.guildwars2.GuildWars2;
import xhsun.gw2api.guildwars2.err.GuildWars2Exception;
import xhsun.gw2app.steve.backend.database.account.AccountDB;
import xhsun.gw2app.steve.backend.database.account.AccountWrapper;
import xhsun.gw2app.steve.backend.database.character.CharacterDB;
import xhsun.gw2app.steve.backend.database.character.CharacterWrapper;
import xhsun.gw2app.steve.backend.database.common.CurrencyDB;
import xhsun.gw2app.steve.backend.database.common.CurrencyWrapper;
import xhsun.gw2app.steve.backend.database.common.ItemDB;
import xhsun.gw2app.steve.backend.database.common.ItemWrapper;
import xhsun.gw2app.steve.backend.database.common.SkinDB;
import xhsun.gw2app.steve.backend.database.common.SkinWrapper;
import xhsun.gw2app.steve.backend.database.storage.InventoryDB;
import xhsun.gw2app.steve.backend.database.storage.InventoryWrapper;
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
	private Context context;

	public WrapperModule(Context context) {
		this.context = context;
	}

	@Provides
	@Singleton
	Cache providesCache() {
		return new Cache(new File(context.getCacheDir(), "http-cache"), 10 * 1024 * 1024);
	}

	@Provides
	@Singleton
	GuildWars2 providesServerWrapper(Cache cache) {
		try {
			GuildWars2.setInstance(cache);
		} catch (GuildWars2Exception ignored) {
		}
		return GuildWars2.getInstance();
	}

	@Provides
	AccountWrapper providesAccountWrapper(AccountDB database, GuildWars2 wrapper) {
		return new AccountWrapper(database, wrapper);
	}

	@Provides
	CurrencyWrapper providesCurrencyWrapper(CurrencyDB currency) {
		return new CurrencyWrapper(currency);
	}

	@Provides
	ItemWrapper providesItemWrapper(GuildWars2 wrapper, ItemDB itemDB) {
		return new ItemWrapper(wrapper, itemDB);
	}

	@Provides
	SkinWrapper providesSkinWrapper(GuildWars2 wrapper, SkinDB skinDB) {
		return new SkinWrapper(wrapper, skinDB);
	}

	@Provides
	CharacterWrapper providesCharacterWrapper(GuildWars2 wrapper, AccountWrapper accountWrapper,
	                                          CharacterDB characterDB) {
		return new CharacterWrapper(wrapper, accountWrapper, characterDB);
	}

	@Provides
	WalletWrapper providesWalletWrapper(WalletDB wallet, CurrencyWrapper currency, GuildWars2 wrapper,
	                                    AccountWrapper accountWrapper) {
		return new WalletWrapper(wallet, currency, wrapper, accountWrapper);
	}

	@Provides
	InventoryWrapper providesStorageWrapper(GuildWars2 wrapper, AccountWrapper accountWrapper,
	                                        CharacterWrapper characterWrapper, ItemWrapper itemWrapper,
	                                        SkinWrapper skinWrapper, InventoryDB storage) {
		return new InventoryWrapper(wrapper, accountWrapper, characterWrapper, itemWrapper, skinWrapper,
				storage);
	}
}
