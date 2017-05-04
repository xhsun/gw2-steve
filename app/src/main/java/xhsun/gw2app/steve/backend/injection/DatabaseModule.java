package xhsun.gw2app.steve.backend.injection;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import xhsun.gw2app.steve.backend.database.account.AccountDB;
import xhsun.gw2app.steve.backend.database.character.CharacterDB;
import xhsun.gw2app.steve.backend.database.common.CurrencyDB;
import xhsun.gw2app.steve.backend.database.common.ItemDB;
import xhsun.gw2app.steve.backend.database.common.SkinDB;
import xhsun.gw2app.steve.backend.database.storage.InventoryDB;
import xhsun.gw2app.steve.backend.database.wallet.WalletDB;

/**
 * Module class for database classes
 *
 * @author xhsun
 * @since 2017-03-29
 */
@Module
public class DatabaseModule {
	private Context context;

	public DatabaseModule(Context context) {
		this.context = context;
	}

	@Provides
	@Singleton
	AccountDB providesAccountDB() {
		return new AccountDB(context);
	}

	@Provides
	@Singleton
	CurrencyDB providesCurrencyDB() {
		return new CurrencyDB(context);
	}

	@Provides
	@Singleton
	WalletDB providesWalletDB() {
		return new WalletDB(context);
	}

	@Provides
	@Singleton
	InventoryDB providesStorageDB() {
		return new InventoryDB(context);
	}

	@Provides
	@Singleton
	CharacterDB providesCharacterDB() {
		return new CharacterDB(context);
	}

	@Provides
	@Singleton
	ItemDB providesItemDB() {
		return new ItemDB(context);
	}

	@Provides
	@Singleton
	SkinDB providesSkinDB() {
		return new SkinDB(context);
	}
}
