package xhsun.gw2app.steve.backend.util.account;

import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import xhsun.gw2app.steve.R;
import xhsun.gw2app.steve.backend.util.Utility;

/**
 * Base on <a href="github.com/nemanja-kovacevic/recycler-view-swipe-to-delete">this</a>
 *
 * @author xhsun
 * @since 2017-02-05
 */

public class SwipeCallback extends ItemTouchHelper.SimpleCallback {
	private RecyclerView view;
	private Drawable background;
	private Drawable delete;
	private int iconMargin;

	public SwipeCallback(RecyclerView view) {
		super(0, ItemTouchHelper.LEFT);
		this.view = view;
		background = new ColorDrawable(Utility.DELETING);
		delete = ContextCompat.getDrawable(view.getContext(), R.drawable.ic_button_delete);
		iconMargin = (int) view.getContext().getResources().getDimension(R.dimen.activity_horizontal_margin);
	}

	@Override
	public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
		return false;
	}

	@Override
	public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
		int position = viewHolder.getAdapterPosition();
		ListAdapter adapter = (ListAdapter) view.getAdapter();
		adapter.initRemoval(position);
	}

	@Override
	public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
		View itemView = viewHolder.itemView;
		if (viewHolder.getAdapterPosition() == -1) return;//prevent draw deleted item

		// draw background
		background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
		background.draw(c);

		// draw trash icon
		int itemHeight = itemView.getBottom() - itemView.getTop();
		int width = delete.getIntrinsicWidth();
		int height = delete.getIntrinsicWidth();

		int clearLeft = itemView.getRight() - iconMargin - width;
		int clearRight = itemView.getRight() - iconMargin;
		int clearTop = itemView.getTop() + (itemHeight - height) / 2;
		int clearBottom = clearTop + height;
		delete.setBounds(clearLeft, clearTop, clearRight, clearBottom);

		delete.draw(c);

		super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
	}
}
