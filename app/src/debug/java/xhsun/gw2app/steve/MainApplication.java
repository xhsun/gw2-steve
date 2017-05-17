package xhsun.gw2app.steve;

import android.app.Application;

import timber.log.Timber;
import xhsun.gw2app.steve.backend.injection.DaggerServiceComponent;
import xhsun.gw2app.steve.backend.injection.DatabaseModule;
import xhsun.gw2app.steve.backend.injection.ServiceComponent;
import xhsun.gw2app.steve.backend.injection.WrapperModule;

/**
 * Main application for init injection and debug tree
 *
 * @author xhsun
 * @since 2017-02-16
 */

public class MainApplication extends Application {
	private ServiceComponent serviceComponent;

	@Override
	public void onCreate() {
		super.onCreate();

		//debug log
		Timber.plant(new Timber.DebugTree() {
			@Override
			protected String createStackElementTag(StackTraceElement element) {
				return super.createStackElementTag(element) + ":" + element.getLineNumber();
			}
		});

		serviceComponent = DaggerServiceComponent.builder()
				.wrapperModule(new WrapperModule(getApplicationContext()))
				.databaseModule(new DatabaseModule(getApplicationContext()))
				.build();
	}

	public ServiceComponent getServiceComponent() {
		return serviceComponent;
	}
}
