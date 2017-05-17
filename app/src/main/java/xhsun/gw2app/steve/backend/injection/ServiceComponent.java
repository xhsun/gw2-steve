package xhsun.gw2app.steve.backend.injection;

import javax.inject.Singleton;

import dagger.Component;
import xhsun.gw2app.steve.backend.database.account.AccountWrapper;
import xhsun.gw2app.steve.backend.database.wallet.WalletWrapper;
import xhsun.gw2app.steve.view.dialog.fragment.AddAccount;
import xhsun.gw2app.steve.view.fragment.AccountFragment;
import xhsun.gw2app.steve.view.fragment.WalletFragment;
import xhsun.gw2app.steve.view.fragment.storage.StorageFragment;

/**
 * service component
 *
 * @author xhsun
 * @since 2017-03-16
 */
@Singleton
@Component(modules = {WrapperModule.class, DatabaseModule.class})
public interface ServiceComponent {
	void inject(AccountWrapper wrapper);

	void inject(WalletWrapper wrapper);

	void inject(AccountFragment fragment);

	void inject(AddAccount dialog);

	void inject(WalletFragment fragment);

	void inject(StorageFragment fragment);
}
