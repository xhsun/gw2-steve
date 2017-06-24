package xhsun.gw2app.steve.backend.util.support.account;

import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import xhsun.gw2app.steve.backend.util.Utility;

/**
 * Base on <a href="github.com/nemanja-kovacevic/recycler-view-swipe-to-delete">this</a>
 *
 * @author xhsun
 * @since 2017-02-05
 */

public class CustomItemDecoration extends RecyclerView.ItemDecoration {
	private Drawable background;

	public CustomItemDecoration() {
		super();
		background = new ColorDrawable(Utility.DELETED);
	}

	@Override
	public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
		View lastViewComingDown = null;
		View firstViewComingUp = null;

		// this is fixed
		int left = 0;
		int right = parent.getWidth();

		// this we need to find out
		int top = 0;
		int bottom = 0;

		// find relevant translating views
		int childCount = parent.getLayoutManager().getChildCount();
		for (int i = 0; i < childCount; i++) {
			View child = parent.getLayoutManager().getChildAt(i);
			if (child.getTranslationY() < 0) {
				// view is coming down
				lastViewComingDown = child;
			} else if (child.getTranslationY() > 0) {
				// view is coming up
				if (firstViewComingUp == null) {
					firstViewComingUp = child;
				}
			}
		}

		if (lastViewComingDown != null && firstViewComingUp != null) {
			// views are coming down AND going up to fill the void
			top = lastViewComingDown.getBottom() + (int) lastViewComingDown.getTranslationY();
			bottom = firstViewComingUp.getTop() + (int) firstViewComingUp.getTranslationY();
		} else if (lastViewComingDown != null) {
			// views are going down to fill the void
			top = lastViewComingDown.getBottom() + (int) lastViewComingDown.getTranslationY();
			bottom = lastViewComingDown.getBottom();
		} else if (firstViewComingUp != null) {
			// views are coming up to fill the void
			top = firstViewComingUp.getTop();
			bottom = firstViewComingUp.getTop() + (int) firstViewComingUp.getTranslationY();
		}

		background.setBounds(left, top, right, bottom);
		background.draw(c);

		super.onDraw(c, parent, state);
	}
}
