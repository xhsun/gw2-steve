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

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.grantland.widget.AutofitTextView;
import xhsun.gw2app.steve.R;

/**
 * Expandable list adapter for wallet
 *
 * @author xhsun
 * @since 2017-03-26
 */

public class ListAdapter extends ExpandableRecyclerAdapter<TotalWallet, IndividualWallet, ListAdapter.TotalViewHolder, ListAdapter.IndividualViewHolder> {
	private LayoutInflater inflater;

	public ListAdapter(Context context, @NonNull List<TotalWallet> list) {
		super(list);
		inflater = LayoutInflater.from(context);
	}

	@NonNull
	@Override
	public TotalViewHolder onCreateParentViewHolder(@NonNull ViewGroup parentViewGroup, int viewType) {
		return new TotalViewHolder(inflater.inflate(R.layout.list_wallet_parent_item, parentViewGroup, false));
	}

	@NonNull
	@Override
	public IndividualViewHolder onCreateChildViewHolder(@NonNull ViewGroup childViewGroup, int viewType) {
		return new IndividualViewHolder(inflater.inflate(R.layout.list_wallet_child_item, childViewGroup, false));
	}

	@Override
	public void onBindParentViewHolder(@NonNull TotalViewHolder parentViewHolder, int parentPosition, @NonNull TotalWallet parent) {
		parentViewHolder.bind(parent);
	}

	@Override
	public void onBindChildViewHolder(@NonNull IndividualViewHolder childViewHolder, int parentPosition, int childPosition, @NonNull IndividualWallet child) {
		childViewHolder.bind(child);
	}

	//view holder for parent
	class TotalViewHolder extends ParentViewHolder {
		private View view;
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
		@BindView(R.id.wallet_parent_currency_img)
		ImageView image;

		/**
		 * Default constructor.
		 *
		 * @param itemView The {@link View} being hosted in this ViewHolder
		 */
		TotalViewHolder(@NonNull View itemView) {
			super(itemView);
			view = itemView;
			ButterKnife.bind(this, itemView);
		}

		void bind(TotalWallet wallet) {
			name.setText(wallet.getName());
			if (wallet.getId() == 1) {
				parseCoins(wallet);
			} else {
				Picasso.with(view.getContext()).load(wallet.getIcon()).into(image);
				currency.setText(String.valueOf(wallet.getValue()));
				goldHolder.setVisibility(View.GONE);
				silverHolder.setVisibility(View.GONE);
			}
		}

		private void parseCoins(TotalWallet wallet) {
			long value = wallet.getValue();
			image.setImageResource(R.mipmap.ic_coin_copper);
			currency.setText(String.valueOf(value % 100));
			value = value / 100;
			this.silver.setText(String.valueOf(value % 100));
			this.gold.setText(String.valueOf(value / 100));
			goldHolder.setVisibility(View.VISIBLE);
			silverHolder.setVisibility(View.VISIBLE);
		}
	}

	//view holder for child
	class IndividualViewHolder extends ChildViewHolder {
		private View view;
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
		@BindView(R.id.wallet_child_currency_img)
		ImageView image;

		/**
		 * Default constructor.
		 *
		 * @param itemView The {@link View} being hosted in this ViewHolder
		 */
		IndividualViewHolder(@NonNull View itemView) {
			super(itemView);
			view = itemView;
			ButterKnife.bind(this, itemView);
		}

		void bind(IndividualWallet individual) {
			account.setText(individual.getAccount());
			if (individual.getId() == 1) {
				parseCoins(individual);
			} else {
				Picasso.with(view.getContext()).load(individual.getIcon()).into(image);
				currency.setText(String.valueOf(individual.getValue()));
				goldHolder.setVisibility(View.GONE);
				silverHolder.setVisibility(View.GONE);
			}
		}

		private void parseCoins(IndividualWallet wallet) {
			long value = wallet.getValue();
			image.setImageResource(R.mipmap.ic_coin_copper);
			currency.setText(String.valueOf(value % 100));
			value = value / 100;
			this.silver.setText(String.valueOf(value % 100));
			this.gold.setText(String.valueOf(value / 100));
			goldHolder.setVisibility(View.VISIBLE);
			silverHolder.setVisibility(View.VISIBLE);
		}
	}
}
