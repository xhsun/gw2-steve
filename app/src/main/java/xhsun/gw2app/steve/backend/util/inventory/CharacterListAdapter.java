package xhsun.gw2app.steve.backend.util.inventory;

import android.support.annotation.NonNull;
import android.support.v7.util.SortedList;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import xhsun.gw2app.steve.R;
import xhsun.gw2app.steve.backend.database.account.AccountInfo;
import xhsun.gw2app.steve.backend.database.character.CharacterInfo;
import xhsun.gw2app.steve.backend.util.Utility;
import xhsun.gw2app.steve.backend.util.ViewHolder;
import xhsun.gw2app.steve.backend.util.items.OnLoadMoreListener;
import xhsun.gw2app.steve.backend.util.items.ProgressViewHolder;
import xhsun.gw2app.steve.backend.util.items.StorageGridAdapter;

/**
 * List adapter for nested recyclerview in character inventory
 * @author xhsun
 * @since 2017-04-1
 */
public class CharacterListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
	private static final int TYPE_CONTENT = 0;
	private static final int TYPE_LOAD = 1;
	private SortedList<CharacterInfo> characters;
	private AccountInfo account;
	private OnLoadMoreListener listener;

	CharacterListAdapter(@NonNull AccountInfo info, @NonNull OnLoadMoreListener listener) {
		account = info;
		this.listener = listener;
		characters = new SortedList<>(CharacterInfo.class, new CharacterSortedListCallback(this));
	}

	//update data set and set loading to false
	public void addData(CharacterInfo data) {
		characters.add(data);
		if (data != null) {
			if (!account.getCharacters().contains(data)) account.getCharacters().add(data);
			listener.setLoading(false);
		}
	}

	//update data set
	public void addDataWithoutLoad(@NonNull CharacterInfo data) {
		if (!account.getCharacters().contains(data)) account.getCharacters().add(data);
		characters.add(data);
	}

	/**
	 * check if the list contain the given data
	 *
	 * @param data account info
	 * @return true if contain, false otherwise
	 */
	boolean containData(@NonNull CharacterInfo data) {
		return characters.indexOf(data) >= 0;
	}

	/**
	 * remove data from list and return index of that item
	 *
	 * @param data character info
	 */
	public void removeData(CharacterInfo data) {
		if (data != null) {
			account.getAllCharacters().get(account.getAllCharacters().indexOf(data)).setAdapter(null);
			account.getCharacters().remove(data);
		}
		characters.remove(data);
	}

	@Override
	public int getItemViewType(int position) {
		if (characters.get(position) == null) return TYPE_LOAD;
		else return TYPE_CONTENT;
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		if (viewType == TYPE_CONTENT)
			return new CharacterViewHolder(inflater.inflate(R.layout.list_storage_sublist_item, parent, false));
		else return new ProgressViewHolder(inflater.inflate(R.layout.progress_item, parent, false));
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		if (holder instanceof ProgressViewHolder) return;
		((CharacterViewHolder) holder).bind(characters.get(position));
		if (account.getCharacters().size() <= account.getAllCharacterNames().size() + 1
				&& listener.isMoreDataAvailable() && !listener.isLoading())
			account.getChild().post(new Runnable() {
				@Override
				public void run() {
					listener.onLoadMore(account);//reached end of list try to get more
				}
			});
	}

	@Override
	public int getItemCount() {
		return characters.size();
	}

	class CharacterViewHolder extends ViewHolder<CharacterInfo> {
		@BindView(R.id.storage_sublist_name)
		TextView name;
		@BindView(R.id.storage_sublist_content)
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

			StorageGridAdapter adapter = new StorageGridAdapter(data.getInventory());
			content.setLayoutManager(new GridLayoutManager(itemView.getContext(),
					Utility.calculateColumns(itemView)));
			content.setAdapter(adapter);

			data.setAdapter(adapter);
			account.getAllCharacters().get(account.getAllCharacters().indexOf(data)).setAdapter(adapter);
		}
	}
}

