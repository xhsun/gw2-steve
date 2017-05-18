package xhsun.gw2app.steve.backend.util.wallet;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.grantland.widget.AutofitTextView;
import xhsun.gw2app.steve.R;
import xhsun.gw2app.steve.backend.data.CurrencyInfo;
import xhsun.gw2app.steve.backend.util.Utility;

/**
 * list adapter for wallet
 *
 * @author xhsun
 * @since 2017-05-01
 */

public class CurrencyListAdapter extends RecyclerView.Adapter<CurrencyListAdapter.ParentViewHolder> {
	private List<CurrencyInfo> currencies;

	public CurrencyListAdapter() {
		this.currencies = new ArrayList<>();
	}

	public void setData(List<CurrencyInfo> data) {
		currencies = data;
		notifyDataSetChanged();
	}

	@Override
	public ParentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new CurrencyListAdapter.ParentViewHolder(LayoutInflater.from(parent.getContext()).
				inflate(R.layout.list_wallet_parent_item, parent, false));
	}

	@Override
	public void onBindViewHolder(ParentViewHolder holder, int position) {
		holder.bind(currencies.get(position));
	}

	@Override
	public int getItemCount() {
		return currencies.size();
	}

	class ParentViewHolder extends CurrencyViewHolder<CurrencyInfo> {
		@BindView(R.id.wallet_parent_desc)
		RelativeLayout expandable;
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
		@BindView(R.id.wallet_list_divider)
		View divider;
		@BindView(R.id.wallet_sublist)
		RecyclerView lists;

		ParentViewHolder(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}

		protected void bind(CurrencyInfo info) {
			name.setText(info.getName());
			expandable.setOnClickListener(v -> {
				if (lists.getVisibility() == View.VISIBLE) {
					lists.setVisibility(View.GONE);
					divider.setVisibility(View.GONE);
				} else {
					lists.setVisibility(View.VISIBLE);
					divider.setVisibility(View.VISIBLE);
				}
			});
			if (info.getId() == 1) {
				parseCoins(info);
			} else {
				Picasso.with(itemView.getContext()).load(info.getIcon()).into(image);
				this.currency.setText(String.valueOf(NumberFormat.getIntegerInstance().format(info.getTotalValue())));
				goldHolder.setVisibility(View.GONE);
				silverHolder.setVisibility(View.GONE);
			}

			lists.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
			lists.setAdapter(new DetailListAdapter(info.getChildList()));
		}

		protected void parseCoins(CurrencyInfo currency) {
			fillCoins(currency.getTotalValue(), gold, silver, this.currency);

			image.setLayoutParams(getCopperLayout(6, 12, this.currency.getId(), itemView));
			Picasso.with(itemView.getContext()).load(Utility.COIN_GOLD).into(goldImg);
			Picasso.with(itemView.getContext()).load(Utility.COIN_SILVER).into(silverImg);
			Picasso.with(itemView.getContext()).load(Utility.COIN_COPPER).into(image);

			goldHolder.setVisibility(View.VISIBLE);
			silverHolder.setVisibility(View.VISIBLE);
		}
	}
}
