package xhsun.gw2app.steve.backend.util;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Recyclerview view holder template with bind method
 *
 * @author xhsun
 * @since 2017-04-01
 */

public abstract class ViewHolder<T> extends RecyclerView.ViewHolder {
	protected T data;

	public ViewHolder(View itemView) {
		super(itemView);
	}

	protected abstract void bind(T info);
}
