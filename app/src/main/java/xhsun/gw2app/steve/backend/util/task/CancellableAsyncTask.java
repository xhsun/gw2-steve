package xhsun.gw2app.steve.backend.util.task;

import android.os.AsyncTask;

/**
 * Custom async task
 *
 * @author xhsun
 * @since 2017-04-01
 */

public abstract class CancellableAsyncTask<T, P, R> extends AsyncTask<T, P, R> {
	protected boolean isCancelled = false;

	public void setCancelled() {
		isCancelled = true;
		onCancelled();
	}
}
