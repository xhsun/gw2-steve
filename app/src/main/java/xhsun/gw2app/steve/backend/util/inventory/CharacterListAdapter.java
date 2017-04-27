package xhsun.gw2app.steve.backend.util.inventory;

import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import xhsun.gw2app.steve.R;
import xhsun.gw2app.steve.backend.database.account.AccountInfo;
import xhsun.gw2app.steve.backend.database.character.CharacterInfo;
import xhsun.gw2app.steve.backend.util.ViewHolder;
import xhsun.gw2app.steve.backend.util.storage.StorageGridAdapter;

/**
 * List adapter for nested recyclerview in character inventory
 *
 * @author xhsun
 * @since 2017-04-1
 */
public class CharacterListAdapter extends RecyclerView.Adapter<CharacterListAdapter.CharacterViewHolder> {
	private List<CharacterInfo> characters;
	private AccountInfo account;
	private OnLoadMoreListener listener;

	CharacterListAdapter(@NonNull AccountInfo info, @NonNull OnLoadMoreListener listener) {
		account = info;
		this.listener = listener;
		characters = new ArrayList<>();
	}

	//update data set and set loading to false
	public void addData(@NonNull CharacterInfo character) {
		characters.add(character);
		notifyItemInserted(characters.size() - 1);
		listener.setLoading(false);
	}

	//update data set and set loading to false
	public void addDataWithoutLoad(int index, @NonNull CharacterInfo character) {
		if (index >= characters.size()) {
			characters.add(character);
			notifyItemInserted(characters.size() - 1);
		} else {
			characters.add(index, character);
			notifyItemInserted(index);
		}
	}

	/**
	 * check if the list contain the given data
	 *
	 * @param data account info
	 * @return true if contain, false otherwise
	 */
	public boolean containData(@NonNull CharacterInfo data) {
		return characters.contains(data);
	}

	/**
	 * remove data from list and return index of that item
	 *
	 * @param character character info
	 * @return idex | -1 if not find
	 */
	public int removeData(@NonNull CharacterInfo character) {
		int index = characters.indexOf(character);
		characters.remove(character);
		return index;
	}

	@Override
	public CharacterListAdapter.CharacterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_inventory_character_item, parent, false);
		return new CharacterViewHolder(view);
	}

	@Override
	public void onBindViewHolder(CharacterListAdapter.CharacterViewHolder holder, int position) {
		holder.bind(characters.get(position));
		if (position >= getItemCount() - 1 && listener.isMoreDataAvailable() && !listener.isLoading())
			listener.onLoadMore(account);//reached end of list try to get more
	}

	@Override
	public int getItemCount() {
		return characters.size();
	}

	class CharacterViewHolder extends ViewHolder<CharacterInfo> {
		private static final int SIZE = 51;
		@BindView(R.id.inventory_character_name)
		TextView name;
		@BindView(R.id.inventory_content_list)
		RecyclerView content;

		private CharacterViewHolder(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}

		@Override
		protected void bind(CharacterInfo info) {
			data = info;
			name.setText(data.getName());
			name.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (content.getVisibility() == View.VISIBLE) content.setVisibility(View.GONE);
					else content.setVisibility(View.VISIBLE);
				}
			});
			content.setLayoutManager(new GridLayoutManager(itemView.getContext(), calculateColumns()));
			data.setAdapter(new StorageGridAdapter(data.getInventory()));
			content.setAdapter(data.getAdapter());
		}

		private int calculateColumns() {
			DisplayMetrics displayMetrics = itemView.getContext().getResources().getDisplayMetrics();
			float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
			return (int) (dpWidth / SIZE);
		}
	}
}
