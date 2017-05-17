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
import xhsun.gw2app.steve.backend.database.storage.BankDB;
import xhsun.gw2app.steve.backend.database.storage.InventoryDB;
import xhsun.gw2app.steve.backend.database.storage.MaterialDB;
import xhsun.gw2app.steve.backend.database.storage.WardrobeDB;
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
	CurrencyDB providesCurrencyDB() {
		return new CurrencyDB(context);
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

	@Provides
	@Singleton
	AccountDB providesAccountDB() {
		return new AccountDB(context);
	}

	@Provides
	@Singleton
	CharacterDB providesCharacterDB() {
		return new CharacterDB(context);
	}

	@Provides
	@Singleton
	WalletDB providesWalletDB() {
		return new WalletDB(context);
	}

	@Provides
	@Singleton
	InventoryDB providesInventoryDB() {
		return new InventoryDB(context);
	}

	@Provides
	@Singleton
	BankDB providesBankDB() {
		return new BankDB(context);
	}

	@Provides
	@Singleton
	MaterialDB providesMaterialDB() {
		return new MaterialDB(context);
	}

	@Provides
	@Singleton
	WardrobeDB providesWardrobeDB() {
		return new WardrobeDB(context);
	}

}
