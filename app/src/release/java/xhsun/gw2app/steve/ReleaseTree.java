package xhsun.gw2app.steve;

import android.util.Log;

import timber.log.Timber;

/**
 * Timer tree for release variant<br/>
 * @author xhsun
 * @since 2017-03-14
 */
public class ReleaseTree extends Timber.DebugTree {

	@Override
	protected void log(int priority, String tag, String message, Throwable t) {
		if (priority == Log.VERBOSE || priority == Log.DEBUG || priority == Log.INFO) return;
		super.log(priority,tag, message, t);
	}
}
