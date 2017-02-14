package xhsun.gw2app.steve.view.storage;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import xhsun.gw2app.steve.R;
import xhsun.gw2app.steve.util.constant.Color;
import xhsun.gw2app.steve.util.model.InventoryItem;

/**
 * @author xhsun
 * @since 2017-02-13
 */
public class StorageGridAdapter extends RecyclerView.Adapter<StorageGridAdapter.ViewHolder> {
	private Context context;
	private List<InventoryItem> storage;


	public StorageGridAdapter(List<InventoryItem> items, Context context) {
		storage = items;
		this.context = context;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.fragment_storage_item, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(final ViewHolder holder, int position) {
		holder.content = storage.get(position);
		holder.initInfo();
	}

	@Override
	public int getItemCount() {
		return storage.size();
	}

	class ViewHolder extends RecyclerView.ViewHolder {
		private View view;
		private FrameLayout rarity;
		private ImageView image;
		private TextView count;
		private InventoryItem content;

		ViewHolder(View view) {
			super(view);
			this.view = view;
			rarity = (FrameLayout) view.findViewById(R.id.storage_item_rarity);
			count = (TextView) view.findViewById(R.id.storage_item_size);
			image = (ImageView) view.findViewById(R.id.storage_item_img);
		}

		private void initInfo() {
			int size = content.getCount();
			if (size == 0 || size == 1)
				count.setVisibility(View.GONE);
			else count.setText(size);
			setRarity();
			Picasso.with(context).load(content.getIcon()).into(image);
			view.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					//TODO on grid item click
				}
			});
		}

		private void setRarity() {
			switch (content.getRarity()) {
				case Junk:
					rarity.setBackgroundColor(Color.Junk);
					break;
				case Basic:
					rarity.setBackgroundColor(Color.Basic);
					break;
				case Fine:
					rarity.setBackgroundColor(Color.Fine);
					break;
				case Masterwork:
					rarity.setBackgroundColor(Color.Masterwork);
					break;
				case Rare:
					rarity.setBackgroundColor(Color.Rare);
					break;
				case Ascended:
					rarity.setBackgroundColor(Color.Ascended);
					break;
				case Legendary:
					rarity.setBackgroundColor(Color.Legendary);
					break;
			}
		}
	}
}
