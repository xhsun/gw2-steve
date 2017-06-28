package xhsun.gw2app.steve.backend.injection.module;

import android.content.Context;

import java.io.File;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import me.xhsun.guildwars2wrapper.GuildWars2;
import me.xhsun.guildwars2wrapper.error.GuildWars2Exception;
import okhttp3.Cache;
import xhsun.gw2app.steve.backend.data.wrapper.account.AccountDB;
import xhsun.gw2app.steve.backend.data.wrapper.account.AccountWrapper;
import xhsun.gw2app.steve.backend.data.wrapper.character.CharacterDB;
import xhsun.gw2app.steve.backend.data.wrapper.character.CharacterWrapper;
import xhsun.gw2app.steve.backend.data.wrapper.common.CurrencyDB;
import xhsun.gw2app.steve.backend.data.wrapper.common.CurrencyWrapper;
import xhsun.gw2app.steve.backend.data.wrapper.common.ItemDB;
import xhsun.gw2app.steve.backend.data.wrapper.common.ItemWrapper;
import xhsun.gw2app.steve.backend.data.wrapper.common.MiscDB;
import xhsun.gw2app.steve.backend.data.wrapper.common.MiscWrapper;
import xhsun.gw2app.steve.backend.data.wrapper.common.SkinDB;
import xhsun.gw2app.steve.backend.data.wrapper.common.SkinWrapper;
import xhsun.gw2app.steve.backend.data.wrapper.storage.BankDB;
import xhsun.gw2app.steve.backend.data.wrapper.storage.BankWrapper;
import xhsun.gw2app.steve.backend.data.wrapper.storage.InventoryDB;
import xhsun.gw2app.steve.backend.data.wrapper.storage.InventoryWrapper;
import xhsun.gw2app.steve.backend.data.wrapper.storage.MaterialDB;
import xhsun.gw2app.steve.backend.data.wrapper.storage.MaterialWrapper;
import xhsun.gw2app.steve.backend.data.wrapper.storage.WardrobeDB;
import xhsun.gw2app.steve.backend.data.wrapper.storage.WardrobeWrapper;
import xhsun.gw2app.steve.backend.data.wrapper.wallet.WalletDB;
import xhsun.gw2app.steve.backend.data.wrapper.wallet.WalletWrapper;

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
	MiscWrapper providesMiscWrapper(GuildWars2 wrapper, MiscDB miscDB) {
		return new MiscWrapper(wrapper, miscDB);
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
	InventoryWrapper providesInventoryWrapper(GuildWars2 wrapper, AccountWrapper accountWrapper,
	                                          CharacterWrapper characterWrapper, ItemWrapper itemWrapper,
	                                          SkinWrapper skinWrapper, InventoryDB storage) {
		return new InventoryWrapper(wrapper, accountWrapper, characterWrapper, itemWrapper, skinWrapper,
				storage);
	}

	@Provides
	BankWrapper providesBankWrapper(GuildWars2 wrapper, BankDB bankDB, AccountWrapper accountWrapper,
	                                ItemWrapper itemWrapper, SkinWrapper skinWrapper) {
		return new BankWrapper(wrapper, bankDB, accountWrapper, itemWrapper, skinWrapper);
	}

	@Provides
	MaterialWrapper providesMaterialWrapper(GuildWars2 wrapper, AccountWrapper accountWrapper,
	                                        ItemWrapper itemWrapper, MaterialDB materialDB) {
		return new MaterialWrapper(wrapper, accountWrapper, itemWrapper, materialDB);
	}

	@Provides
	WardrobeWrapper providesWardrobeWrapper(GuildWars2 wrapper, AccountWrapper accountWrapper,
	                                        SkinWrapper skinWrapper, MiscWrapper miscWrapper, WardrobeDB wardrobeDB) {
		return new WardrobeWrapper(wrapper, accountWrapper, skinWrapper, miscWrapper, wardrobeDB);
	}
}
