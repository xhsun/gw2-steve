package xhsun.gw2app.steve.view.account;

import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import xhsun.gw2app.steve.R;

/**
 * Base on <a href="github.com/nemanja-kovacevic/recycler-view-swipe-to-delete">this</a>
 *
 * @author xhsun
 * @since 2017-02-05
 */

class AccountSwipeCallback extends ItemTouchHelper.SimpleCallback {
	private RecyclerView view;
	private Drawable background;
	private Drawable clear;
	private int clearMargin;

	AccountSwipeCallback(RecyclerView view) {
		super(0, ItemTouchHelper.LEFT);
		this.view = view;
		background = new ColorDrawable(0xFFF44336);
		clear = ContextCompat.getDrawable(view.getContext(), R.drawable.ic_action_clear);
		clearMargin = (int) view.getContext().getResources().getDimension(R.dimen.activity_horizontal_margin);
	}

	@Override
	public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
		return false;
	}

	@Override
	public int getSwipeDirs(RecyclerView view, RecyclerView.ViewHolder holder) {
		int position = holder.getAdapterPosition();
		AccountListAdapter adapter = (AccountListAdapter) view.getAdapter();
		if (adapter.isPendingRemoval(position)) {
			return 0;
		}
		return super.getSwipeDirs(view, holder);
	}

	@Override
	public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
		int position = viewHolder.getAdapterPosition();
		AccountListAdapter adapter = (AccountListAdapter) view.getAdapter();
		adapter.pendingRemoval(position);
	}

	@Override
	public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
		View itemView = viewHolder.itemView;
		if (viewHolder.getAdapterPosition() == -1) return;//prevent draw deleted item

		// draw red background
		background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
		background.draw(c);

		// draw clear icon
		int itemHeight = itemView.getBottom() - itemView.getTop();
		int width = clear.getIntrinsicWidth();
		int height = clear.getIntrinsicWidth();

		int clearLeft = itemView.getRight() - clearMargin - width;
		int clearRight = itemView.getRight() - clearMargin;
		int clearTop = itemView.getTop() + (itemHeight - height) / 2;
		int clearBottom = clearTop + height;
		clear.setBounds(clearLeft, clearTop, clearRight, clearBottom);

		clear.draw(c);

		super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
	}
}
