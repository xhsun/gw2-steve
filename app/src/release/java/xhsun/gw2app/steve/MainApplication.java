package xhsun.gw2app.steve;

import android.app.Application;

import timber.log.Timber;
import xhsun.gw2app.steve.backend.injection.DaggerServiceComponent;
import xhsun.gw2app.steve.backend.injection.DatabaseModule;
import xhsun.gw2app.steve.backend.injection.ServiceComponent;
import xhsun.gw2app.steve.backend.injection.WrapperModule;

/**
 * Created by hannah on 16/03/17.
 */

public class MainApplication extends Application {
	private ServiceComponent component;

	@Override
	public void onCreate() {
		super.onCreate();

		//Release log, only show error, warning, wtf
		Timber.plant(new ReleaseTree());

		component = DaggerServiceComponent.builder()
				.wrapperModule(new WrapperModule())
				.databaseModule(new DatabaseModule(getApplicationContext()))
				.build();
	}

	public ServiceComponent getServiceComponent() {
		return component;
	}
}
