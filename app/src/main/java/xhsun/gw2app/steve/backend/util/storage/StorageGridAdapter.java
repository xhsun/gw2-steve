package xhsun.gw2app.steve.backend.util.storage;

import android.support.annotation.NonNull;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import xhsun.gw2api.guildwars2.model.Item;
import xhsun.gw2app.steve.R;
import xhsun.gw2app.steve.backend.database.character.StorageInfo;
import xhsun.gw2app.steve.backend.util.Utility;
import xhsun.gw2app.steve.databinding.GridStorageItemBinding;

/**
 * List adapter for storage grid
 *
 * @author xhsun
 * @since 2017-03-31
 */

public class StorageGridAdapter extends RecyclerView.Adapter<StorageGridAdapter.StorageViewHolder> {
	private Set<StorageInfo> record;
	private SortedList<StorageInfo> storage;

	public StorageGridAdapter(@NonNull List<StorageInfo> storage) {
		this.storage = new SortedList<>(StorageInfo.class, new SortedListCallback(this));
		this.storage.addAll(storage);
		record = new HashSet<>(storage);
	}

	/**
	 * replace data in the adapter and update view
	 *
	 * @param data list of item info
	 */
	public void setData(@NonNull List<StorageInfo> data) {
		storage.beginBatchedUpdates();
		for (int i = 0; i < storage.size(); i++) {
			StorageInfo info = storage.get(i);
			if (!data.contains(info)) storage.remove(info);
		}
		storage.addAll(data);
		storage.endBatchedUpdates();
		record = new HashSet<>(data);
	}

	/**
	 * remove all data that is not in the list provided
	 * @param data list of data to keep
	 */
	public void keepProvided(@NonNull List<StorageInfo> data) {
		storage.beginBatchedUpdates();
		for (StorageInfo s : record) if (!data.contains(s)) storage.remove(s);

		storage.addAll(data);
		storage.endBatchedUpdates();

		record = new HashSet<>(data);
	}

	/**
	 * add new item to list and update view
	 *
	 * @param data item info
	 */
	public void addData(@NonNull StorageInfo data) {
		storage.add(data);
		record.add(data);
	}

	/**
	 * remove item from list and update view
	 *
	 * @param data item info
	 */
	public void removeData(@NonNull StorageInfo data) {
		storage.remove(data);
		record.remove(data);
	}

	@Override
	public StorageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		GridStorageItemBinding binding = GridStorageItemBinding.inflate(inflater, parent, false);
		return new StorageViewHolder(binding);
//		View view = LayoutInflater.from(parent.getContext())
//				.inflate(R.layout.grid_storage_item, parent, false);
//		return new StorageViewHolder(view);
	}

	@Override
	public void onBindViewHolder(StorageViewHolder holder, int position) {
		holder.bind(storage.get(position));
	}

	@Override
	public int getItemCount() {
		return storage.size();
	}

	class StorageViewHolder extends RecyclerView.ViewHolder {
		GridStorageItemBinding binding;
		@BindView(R.id.storage_item_rarity)
		FrameLayout rarity;
		@BindView(R.id.storage_item_img)
		ImageView image;
		@BindView(R.id.storage_item_size)
		TextView count;

		StorageViewHolder(GridStorageItemBinding binding) {
			super(binding.getRoot());
			ButterKnife.bind(this, binding.getRoot());
			this.binding = binding;
		}

		private void bind(StorageInfo info) {
			binding.setItem(info);
			setRarity(info.getItemInfo().getRarity());
			Picasso.with(itemView.getContext()).load(info.getItemInfo().getIcon()).into(image);
			if (info.getCount() < 2) count.setVisibility(View.GONE);
			else count.setText(NumberFormat.getIntegerInstance().format(info.getCount()));
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
