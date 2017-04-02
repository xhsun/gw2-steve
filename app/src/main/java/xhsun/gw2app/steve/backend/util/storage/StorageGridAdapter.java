package xhsun.gw2app.steve.backend.util.storage;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import xhsun.gw2api.guildwars2.model.Item;
import xhsun.gw2app.steve.R;
import xhsun.gw2app.steve.backend.database.character.StorageInfo;
import xhsun.gw2app.steve.backend.util.Utility;
import xhsun.gw2app.steve.backend.util.ViewHolder;

/**
 * List adapter for storage grid
 *
 * @author xhsun
 * @since 2017-03-31
 */

public class StorageGridAdapter extends RecyclerView.Adapter<StorageGridAdapter.StorageViewHolder> {
	private List<StorageInfo> storage;

	public StorageGridAdapter(@NonNull List<StorageInfo> storage) {
		this.storage = storage;
	}

	/**
	 * replace data in the adapter and update view
	 *
	 * @param data list of item info
	 */
	public void setData(@NonNull List<StorageInfo> data) {
		storage = data;
		notifyDataSetChanged();
	}

	/**
	 * add new item to list and update view
	 *
	 * @param data item info
	 */
	public void addData(@NonNull StorageInfo data) {
		storage.add(data);
		notifyItemInserted(storage.size() - 1);
	}

	/**
	 * remove item from list and update view
	 *
	 * @param data item info
	 */
	public void removeData(@NonNull StorageInfo data) {
		int index;
		if ((index = storage.indexOf(data)) == -1) return;
		storage.remove(data);
		notifyItemRemoved(index);
	}

	@Override
	public StorageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.grid_storage_item, parent, false);
		return new StorageViewHolder(view);
	}

	@Override
	public void onBindViewHolder(StorageViewHolder holder, int position) {
		holder.bind(storage.get(position));
	}

	@Override
	public int getItemCount() {
		return storage.size();
	}

	class StorageViewHolder extends ViewHolder<StorageInfo> {
		@BindView(R.id.storage_item_rarity)
		FrameLayout rarity;
		@BindView(R.id.storage_item_img)
		ImageView image;
		@BindView(R.id.storage_item_size)
		TextView count;

		StorageViewHolder(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}

		protected void bind(StorageInfo info) {
			data = info;
			setRarity(data.getItemInfo().getRarity());
			Picasso.with(itemView.getContext()).load(data.getItemInfo().getIcon()).into(image);
			if (data.getCount() < 2) count.setVisibility(View.GONE);
			else count.setText(NumberFormat.getIntegerInstance().format(data.getCount()));
			itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					//TODO show item details
				}
			});
		}

		private void setRarity(Item.Rarity rarity) {
			switch (rarity) {
				case Junk:
					this.rarity.setBackgroundColor(Utility.Junk);
					break;
				case Basic:
					this.rarity.setBackgroundColor(Utility.Basic);
					break;
				case Fine:
					this.rarity.setBackgroundColor(Utility.Fine);
					break;
				case Masterwork:
					this.rarity.setBackgroundColor(Utility.Masterwork);
					break;
				case Rare:
					this.rarity.setBackgroundColor(Utility.Rare);
					break;
				case Exotic:
					this.rarity.setBackgroundColor(Utility.Exotic);
					break;
				case Ascended:
					this.rarity.setBackgroundColor(Utility.Ascended);
					break;
				case Legendary:
					this.rarity.setBackgroundColor(Utility.Legendary);
					break;
			}
		}
	}
}
