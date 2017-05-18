package xhsun.gw2app.steve.backend.util.items;

import android.animation.Animator;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.Payload;
import eu.davidea.flexibleadapter.helpers.AnimatorHelper;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.viewholders.ExpandableViewHolder;
import timber.log.Timber;
import xhsun.gw2app.steve.R;

/**
 * {@link AbstractFlexibleItem} for progress indicator
 *
 * @author xhsun
 * @since 2017-05-09
 */

public class ProgressItem extends AbstractFlexibleItem<ProgressItem.ProgressViewHolder> {
	private enum LoadStatus {
		MORE_TO_LOAD, //Default = should have an empty Payload
		DISABLE_ENDLESS, //Endless is disabled because user has set limits
		NO_MORE_LOAD, //Non-empty Payload = Payload.NO_MORE_LOAD
		ON_CANCEL,
		ON_ERROR
	}

	private LoadStatus status = LoadStatus.MORE_TO_LOAD;

//	public LoadStatus getStatus() {
//		return status;
//	}

	private void setStatus(LoadStatus status) {
		this.status = status;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ProgressItem that = (ProgressItem) o;

		return status == that.status;

	}

	@Override
	public int hashCode() {
		return status != null ? status.hashCode() : 0;
	}

	@Override
	public int getLayoutRes() {
		return R.layout.item_progress;
	}

	@Override
	public ProgressViewHolder createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater, ViewGroup parent) {
		return new ProgressViewHolder(inflater.inflate(getLayoutRes(), parent, false), adapter);
	}

	@Override
	public void bindViewHolder(FlexibleAdapter adapter, ProgressViewHolder holder, int position, List payloads) {
		holder.progressBar.setVisibility(View.GONE);

		if (!adapter.isEndlessScrollEnabled()) {
			setStatus(LoadStatus.DISABLE_ENDLESS);
		} else if (payloads.contains(Payload.NO_MORE_LOAD)) {
			setStatus(LoadStatus.NO_MORE_LOAD);
		}
		Timber.i("Status: %s", status);
		switch (this.status) {
			case NO_MORE_LOAD:
				// Reset to default status for next binding
				setStatus(LoadStatus.MORE_TO_LOAD);
				break;
			case DISABLE_ENDLESS:
				break;
			case ON_CANCEL:
				// Reset to default status for next binding
				setStatus(LoadStatus.MORE_TO_LOAD);
				break;
			case ON_ERROR:
				// Reset to default status for next binding
				setStatus(LoadStatus.MORE_TO_LOAD);
				break;
			default:
				holder.progressBar.setVisibility(View.VISIBLE);
				break;
		}
	}

	class ProgressViewHolder extends ExpandableViewHolder {

		@BindView(R.id.list_progress)
		ProgressBar progressBar;

		ProgressViewHolder(View view, FlexibleAdapter adapter) {
			super(view, adapter);
			ButterKnife.bind(this, view);
		}

		@Override
		public void scrollAnimators(@NonNull List<Animator> animators, int position, boolean isForward) {
			AnimatorHelper.scaleAnimator(animators, itemView, 0f);
		}

		@Override
		protected boolean isViewExpandableOnClick() {
			return false;
		}
	}
}

