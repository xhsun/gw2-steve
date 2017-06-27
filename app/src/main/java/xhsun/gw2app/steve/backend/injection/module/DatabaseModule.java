package xhsun.gw2app.steve.backend.injection.module;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import xhsun.gw2app.steve.backend.data.wrapper.account.AccountDB;
import xhsun.gw2app.steve.backend.data.wrapper.character.CharacterDB;
import xhsun.gw2app.steve.backend.data.wrapper.common.CurrencyDB;
import xhsun.gw2app.steve.backend.data.wrapper.common.ItemDB;
import xhsun.gw2app.steve.backend.data.wrapper.common.SkinDB;
import xhsun.gw2app.steve.backend.data.wrapper.storage.BankDB;
import xhsun.gw2app.steve.backend.data.wrapper.storage.InventoryDB;
import xhsun.gw2app.steve.backend.data.wrapper.storage.MaterialDB;
import xhsun.gw2app.steve.backend.data.wrapper.storage.WardrobeDB;
import xhsun.gw2app.steve.backend.data.wrapper.wallet.WalletDB;

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
