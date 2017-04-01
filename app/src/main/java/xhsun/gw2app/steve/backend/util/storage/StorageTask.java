package xhsun.gw2app.steve.backend.util.storage;

import android.os.AsyncTask;

/**
 * Created by hannah on 01/04/17.
 */

public abstract class StorageTask<T, P, R> extends AsyncTask<T, P, R> {
	protected boolean isCancelled = false;

	public void setCancelled() {
		isCancelled = true;
		onCancelled();
	}
}
