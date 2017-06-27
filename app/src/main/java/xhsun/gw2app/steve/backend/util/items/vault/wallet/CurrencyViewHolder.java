package xhsun.gw2app.steve.backend.util.items.vault.wallet;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.NumberFormat;

import xhsun.gw2app.steve.backend.util.Utility;

/**
 * View holder for wallet
 *
 * @author xhsun
 * @since 2017-05-01
 */
public abstract class CurrencyViewHolder<T> extends RecyclerView.ViewHolder {
	public CurrencyViewHolder(View itemView) {
		super(itemView);
	}

	protected abstract void bind(T info);

	protected abstract void parseCoins(T wallet);

	//do value calculation and fill in the appropriate type of coin
	protected void fillCoins(long value, TextView gold, TextView silver, TextView copper) {
		copper.setText(NumberFormat.getIntegerInstance().format(value % 100));
		value = value / 100;
		silver.setText(NumberFormat.getIntegerInstance().format(value % 100));
		gold.setText(NumberFormat.getIntegerInstance().format(value / 100));
	}

	//setup layout parameters for copper coin image
	protected RelativeLayout.LayoutParams getCopperLayout(int margin, int size, int id, View view) {
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(Utility.getDiP(size, view), Utility.getDiP(size, view));
		lp.topMargin = Utility.getDiP(margin, view);
		lp.addRule(RelativeLayout.RIGHT_OF, id);
		return lp;
	}
}
