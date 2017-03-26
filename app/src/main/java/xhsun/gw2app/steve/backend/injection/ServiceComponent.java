package xhsun.gw2app.steve.backend.injection;

import javax.inject.Singleton;

import dagger.Component;
import xhsun.gw2app.steve.backend.database.account.AccountWrapper;
import xhsun.gw2app.steve.view.dialog.AddAccount;
import xhsun.gw2app.steve.view.fragment.AccountFragment;

/**
 * service component
 *
 * @author xhsun
 * @since 2017-03-16
 */
@Singleton
@Component(modules = {ServiceModule.class})
public interface ServiceComponent {
	void inject(AccountWrapper wrapper);

	void inject(AccountFragment fragment);

	void inject(AddAccount dialog);
}
