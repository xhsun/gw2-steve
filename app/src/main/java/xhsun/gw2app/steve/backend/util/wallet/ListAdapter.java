package xhsun.gw2app.steve.backend.util.wallet;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ChildViewHolder;
import com.bignerdranch.expandablerecyclerview.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.ParentViewHolder;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.grantland.widget.AutofitTextView;
import xhsun.gw2app.steve.R;
import xhsun.gw2app.steve.backend.database.common.CurrencyInfo;
import xhsun.gw2app.steve.backend.database.wallet.WalletInfo;
import xhsun.gw2app.steve.backend.util.Utility;

/**
 * Expandable list adapter for wallet
 *
 * @author xhsun
 * @since 2017-03-26
 */

public class ListAdapter extends ExpandableRecyclerAdapter<CurrencyInfo, WalletInfo, ListAdapter.CurrencyViewHolder, ListAdapter.WalletViewHolder> {
	private LayoutInflater inflater;

	public ListAdapter(Context context, @NonNull List<CurrencyInfo> list) {
		super(list);
		inflater = LayoutInflater.from(context);
	}

	@NonNull
	@Override
	public CurrencyViewHolder onCreateParentViewHolder(@NonNull ViewGroup parentViewGroup, int viewType) {
		return new CurrencyViewHolder(inflater.inflate(R.layout.list_wallet_parent_item, parentViewGroup, false));
	}

	@NonNull
	@Override
	public WalletViewHolder onCreateChildViewHolder(@NonNull ViewGroup childViewGroup, int viewType) {
		return new WalletViewHolder(inflater.inflate(R.layout.list_wallet_child_item, childViewGroup, false));
	}

	@Override
	public void onBindParentViewHolder(@NonNull CurrencyViewHolder parentViewHolder, int parentPosition, @NonNull CurrencyInfo parent) {
		parentViewHolder.bind(parent);
	}

	@Override
	public void onBindChildViewHolder(@NonNull WalletViewHolder childViewHolder, int parentPosition, int childPosition, @NonNull WalletInfo child) {
		childViewHolder.bind(child);
	}

	//view holder for parent
	class CurrencyViewHolder extends ParentViewHolder {
		@BindView(R.id.wallet_parent_name)
		AutofitTextView name;
		@BindView(R.id.wallet_parent_gold)
		RelativeLayout goldHolder;
		@BindView(R.id.wallet_parent_silver)
		RelativeLayout silverHolder;
		@BindView(R.id.wallet_parent_gold_amount)
		TextView gold;
		@BindView(R.id.wallet_parent_silver_amount)
		TextView silver;
		@BindView(R.id.wallet_parent_currency_amount)
		TextView currency;
		@BindView(R.id.wallet_parent_gold_img)
		ImageView goldImg;
		@BindView(R.id.wallet_parent_silver_img)
		ImageView silverImg;
		@BindView(R.id.wallet_parent_currency_img)
		ImageView image;

		/**
		 * Default constructor.
		 *
		 * @param itemView The {@link View} being hosted in this ViewHolder
		 */
		CurrencyViewHolder(@NonNull View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}

		void bind(CurrencyInfo currency) {
			name.setText(currency.getName());
			if (currency.getId() == 1) {
				parseCoins(currency);
			} else {
				Picasso.with(itemView.getContext()).load(currency.getIcon()).into(image);
				this.currency.setText(String.valueOf(NumberFormat.getIntegerInstance().format(currency.getTotalValue())));
				goldHolder.setVisibility(View.GONE);
				silverHolder.setVisibility(View.GONE);
			}
		}

		private void parseCoins(CurrencyInfo currency) {
			fillCoins(currency.getTotalValue(), gold, silver, this.currency);

			image.setLayoutParams(getCopperLayout(6, 12, this.currency.getId(), itemView));
			Picasso.with(itemView.getContext()).load(Utility.COIN_GOLD).into(goldImg);
			Picasso.with(itemView.getContext()).load(Utility.COIN_SILVER).into(silverImg);
			Picasso.with(itemView.getContext()).load(Utility.COIN_COPPER).into(image);

			goldHolder.setVisibility(View.VISIBLE);
			silverHolder.setVisibility(View.VISIBLE);
		}
	}

	//view holder for child
	class WalletViewHolder extends ChildViewHolder {
		@BindView(R.id.wallet_child_name)
		AutofitTextView account;
		@BindView(R.id.wallet_child_gold)
		RelativeLayout goldHolder;
		@BindView(R.id.wallet_child_silver)
		RelativeLayout silverHolder;
		@BindView(R.id.wallet_child_gold_amount)
		TextView gold;
		@BindView(R.id.wallet_child_silver_amount)
		TextView silver;
		@BindView(R.id.wallet_child_currency_amount)
		TextView currency;
		@BindView(R.id.wallet_child_gold_img)
		ImageView goldImg;
		@BindView(R.id.wallet_child_silver_img)
		ImageView silverImg;
		@BindView(R.id.wallet_child_currency_img)
		ImageView image;

		/**
		 * Default constructor.
		 *
		 * @param itemView The {@link View} being hosted in this ViewHolder
		 */
		WalletViewHolder(@NonNull View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}

		void bind(WalletInfo wallet) {
			account.setText(wallet.getAccount());
			if (wallet.getCurrencyID() == 1) {
				parseCoins(wallet);
			} else {
				Picasso.with(itemView.getContext()).load(wallet.getIcon()).into(image);
				currency.setText(String.valueOf(NumberFormat.getIntegerInstance().format(wallet.getValue())));
				goldHolder.setVisibility(View.GONE);
				silverHolder.setVisibility(View.GONE);
			}
		}

		private void parseCoins(WalletInfo wallet) {
			fillCoins(wallet.getValue(), gold, silver, currency);

			image.setLayoutParams(getCopperLayout(7, 10, currency.getId(), itemView));
			Picasso.with(itemView.getContext()).load(Utility.COIN_GOLD).into(goldImg);
			Picasso.with(itemView.getContext()).load(Utility.COIN_SILVER).into(silverImg);
			Picasso.with(itemView.getContext()).load(Utility.COIN_COPPER).into(image);

			goldHolder.setVisibility(View.VISIBLE);
			silverHolder.setVisibility(View.VISIBLE);
		}
	}

	//do value calculation and fill in the appropriate type of coin
	private void fillCoins(long value, TextView gold, TextView silver, TextView copper) {
		copper.setText(NumberFormat.getIntegerInstance().format(value % 100));
		value = value / 100;
		silver.setText(NumberFormat.getIntegerInstance().format(value % 100));
		gold.setText(NumberFormat.getIntegerInstance().format(value / 100));
	}

	//setup layout parameters for copper coin image
	private RelativeLayout.LayoutParams getCopperLayout(int margin, int size, int id, View view) {
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(Utility.getDiP(size, view), Utility.getDiP(size, view));
		lp.topMargin = Utility.getDiP(margin, view);
		lp.addRule(RelativeLayout.RIGHT_OF, id);
		return lp;
	}
}
