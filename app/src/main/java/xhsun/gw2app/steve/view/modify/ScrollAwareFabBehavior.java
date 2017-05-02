package xhsun.gw2app.steve.view.modify;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.view.View;

/**
 * auto hide fab when scroll
 * Base on <a href="https://guides.codepath.com/android/floating-action-buttons">this</a>
 *
 * @author xhsun
 * @since 2017-02-05
 */

public class ScrollAwareFabBehavior extends FloatingActionButton.Behavior {
	public ScrollAwareFabBehavior(Context context, AttributeSet attrs) {
		super();
	}

	@Override
	public void onNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child,
	                           View target, int dxConsumed, int dyConsumed, int dxUnconsumed,
	                           int dyUnconsumed) {
		super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed,
				dyUnconsumed);
		if (dyConsumed > 0 && child.getVisibility() == View.VISIBLE) {
			child.hide();
		} else if (dyConsumed < 0 && child.getVisibility() != View.VISIBLE) {
			child.show();
		}
	}
}
