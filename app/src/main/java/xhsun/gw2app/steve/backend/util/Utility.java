package xhsun.gw2app.steve.backend.util;

import android.app.Activity;
import android.graphics.Point;
import android.util.TypedValue;
import android.view.View;

/**
 * Hold utility functions
 *
 * @author xhsun
 * @since 2017-02-16
 */

public class Utility {
	public static final int DELETING = 0xFFF27B87;
	public static final int DELETED = 0xFFF06472;

	public static final int UNDO_BKG = 0xFFBDBDBD;
	public static final int UNDO_SUBTITLE = 0xFF9e9e9e;
	public static final int UNDO_TITLE = 0xFF757575;

	public static final String COIN_GOLD = "https://render.guildwars2.com/file/090A980A96D39FD36FBB004903644C6DBEFB1FFB/156904.png";
	public static final String COIN_SILVER = "https://render.guildwars2.com/file/E5A2197D78ECE4AE0349C8B3710D033D22DB0DA6/156907.png";
	public static final String COIN_COPPER = "https://render.guildwars2.com/file/6CF8F96A3299CFC75D5CC90617C3C70331A1EF0E/156902.png";

	private static final double WIDTH = 800;

	/**
	 * calculate scale for the web view base on display size
	 * this method is base on answer find in <a href="http://stackoverflow.com/a/3916700">stack overflow</a>
	 *
	 * @return scale value
	 */
	public static int getScale(Activity activity) {
		Point size = new Point();
		activity.getWindowManager().getDefaultDisplay().getSize(size);
		int width = size.x;
		Double val = width / WIDTH;
		val = val * 100d;
		return val.intValue();
	}

	/**
	 * calculate dp using pixel
	 *
	 * @param value pixel
	 * @param view  view
	 * @return dp
	 */
	public static int getDiP(int value, View view) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, view.getResources().getDisplayMetrics());
	}
}
