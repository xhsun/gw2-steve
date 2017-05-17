package xhsun.gw2app.steve.backend.util.wallet;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.grantland.widget.AutofitTextView;
import xhsun.gw2app.steve.R;
import xhsun.gw2app.steve.backend.data.WalletInfo;
import xhsun.gw2app.steve.backend.util.Utility;

/**
 * recycler view adapter for wallet detail view
 *
 * @author xhsun
 * @since 2017-05-01
 */

class DetailListAdapter extends RecyclerView.Adapter<DetailListAdapter.WalletViewHolder> {
	private List<WalletInfo> wallet;

	DetailListAdapter(List<WalletInfo> wallet) {
		this.wallet = wallet;
	}

	@Override
	public WalletViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new DetailListAdapter.WalletViewHolder(LayoutInflater.from(parent.getContext()).
				inflate(R.layout.list_wallet_child_item, parent, false));
	}

	@Override
	public void onBindViewHolder(WalletViewHolder holder, int position) {
		holder.bind(wallet.get(position));
	}

	@Override
	public int getItemCount() {
		return wallet.size();
	}

	class WalletViewHolder extends CurrencyViewHolder<WalletInfo> {
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
		@BindView(R.id.wallet_child_list_divider)
		View divider;

		WalletViewHolder(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}

		@Override
		protected void bind(WalletInfo info) {
			if (wallet.indexOf(info) == wallet.size() - 1) divider.setVisibility(View.GONE);
			String cappedName = info.getAccount().substring(0, 1).toUpperCase() + info.getAccount().substring(1);
			account.setText(cappedName);
			if (info.getCurrencyID() == 1) {
				parseCoins(info);
			} else {
				Picasso.with(itemView.getContext()).load(info.getIcon()).into(image);
				currency.setText(String.valueOf(NumberFormat.getIntegerInstance().format(info.getValue())));
				goldHolder.setVisibility(View.GONE);
				silverHolder.setVisibility(View.GONE);
			}
		}

		@Override
		protected void parseCoins(WalletInfo wallet) {
			fillCoins(wallet.getValue(), gold, silver, currency);

			image.setLayoutParams(getCopperLayout(7, 10, currency.getId(), itemView));
			Picasso.with(itemView.getContext()).load(Utility.COIN_GOLD).into(goldImg);
			Picasso.with(itemView.getContext()).load(Utility.COIN_SILVER).into(silverImg);
			Picasso.with(itemView.getContext()).load(Utility.COIN_COPPER).into(image);

			goldHolder.setVisibility(View.VISIBLE);
			silverHolder.setVisibility(View.VISIBLE);
		}
	}
}
