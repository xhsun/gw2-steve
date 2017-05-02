package xhsun.gw2app.steve.backend.injection;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import xhsun.gw2api.guildwars2.GuildWars2;
import xhsun.gw2app.steve.backend.database.account.AccountWrapper;
import xhsun.gw2app.steve.backend.database.wallet.WalletWrapper;

/**
 * Module class for GuildWars2 server wrapper
 *
 * @author xhsun
 * @since 2017-03-16
 */
@Module
public class ServiceModule {
	private Context context;

	public ServiceModule(Context context) {
		this.context = context;
	}

	@Provides
	@Singleton
	GuildWars2 providesServerWrapper() {
		return GuildWars2.getInstance();
	}

	@Provides
	@Singleton
	AccountWrapper providesAccountWrapper(GuildWars2 wrapper) {
		return new AccountWrapper(context, wrapper);
	}

	@Provides
	@Singleton
	WalletWrapper providesWalletWrapper(GuildWars2 wrapper, AccountWrapper accountWrapper) {
		return new WalletWrapper(context, wrapper, accountWrapper);
	}
}
