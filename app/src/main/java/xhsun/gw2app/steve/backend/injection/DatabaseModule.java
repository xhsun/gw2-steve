package xhsun.gw2app.steve.backend.injection;

import android.content.Context;

import dagger.Module;
import dagger.Provides;
import xhsun.gw2app.steve.backend.database.account.AccountDB;
import xhsun.gw2app.steve.backend.database.character.CharacterDB;
import xhsun.gw2app.steve.backend.database.character.StorageDB;
import xhsun.gw2app.steve.backend.database.wallet.CurrencyDB;
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
	AccountDB providesAccountDB() {
		return new AccountDB(context);
	}

	@Provides
	CurrencyDB providesCurrencyDB() {
		return new CurrencyDB(context);
	}

	@Provides
	WalletDB providesWalletDB() {
		return new WalletDB(context);
	}

	@Provides
	StorageDB providesStorageDB() {
		return new StorageDB(context);
	}

	@Provides
	CharacterDB providesCharacterDB() {
		return new CharacterDB(context);
	}
}
