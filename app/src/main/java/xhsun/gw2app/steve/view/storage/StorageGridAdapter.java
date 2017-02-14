package xhsun.gw2app.steve.view.storage;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import xhsun.gw2api.guildwars2.model.util.Storage;
import xhsun.gw2app.steve.R;

/**
 * @author xhsun
 * @since 2017-02-13
 */
public class StorageGridAdapter extends RecyclerView.Adapter<StorageGridAdapter.ViewHolder> {
	public enum Type {BANK, MATERIAL, INVENTORY}

	private Type type;
	private List<Storage> storage;

	public StorageGridAdapter(List<Storage> items, Type type) {
		storage = items;
		this.type = type;
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
		holder.type = type;
		holder.initInfo();
	}

	@Override
	public int getItemCount() {
		return storage.size();
	}

	class ViewHolder extends RecyclerView.ViewHolder {
		private View view;
		private ImageView image;
		private TextView count;
		private Type type;
		private Storage content;

		ViewHolder(View view) {
			super(view);
			this.view = view;
			count = (TextView) view.findViewById(R.id.storage_item_size);
			image = (ImageView) view.findViewById(R.id.storage_item_img);
		}

		private void initInfo() {
			int size = content.getCount();
			if (size == 0 || size == 1)
				count.setVisibility(View.GONE);
			else count.setText(size);
			//TODO get url then inflate image
			view.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					//TODO on grid item click
				}
			});
		}
	}
}
