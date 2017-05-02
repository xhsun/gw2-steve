package xhsun.gw2app.steve.backend.util;

/**
 * Based on <a href="http://stackoverflow.com/questions/1739515/asynctask-and-error-handling-on-android">this</a><br/>
 * For handling exceptions in onPostExecute (AsyncTask)
 *
 * @author xhsun
 * @since 2017-03-20
 */

public class AsyncTaskResult<T> {
	private T data;
	private Exception error;

	public AsyncTaskResult(T data) {
		this.data = data;
	}

	public AsyncTaskResult(Exception e) {
		error = e;
	}

	public Exception getError() {
		return error;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}
}
