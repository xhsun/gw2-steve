package xhsun.gw2app.steve.backend.util.items;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import xhsun.gw2app.steve.R;

/**
 * View holder for list progress indicator
 *
 * @author xhsun
 * @since 2017-05-04
 */

public class ProgressViewHolder extends RecyclerView.ViewHolder {
	@BindView(R.id.list_progress)
	ProgressBar progressBar;

	public ProgressViewHolder(@NonNull View itemView) {
		super(itemView);
		ButterKnife.bind(this, itemView);
	}
}