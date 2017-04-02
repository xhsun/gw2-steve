package xhsun.gw2app.steve.backend.util.inventory;

import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

//public class CharacterListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
public class CharacterListAdapter extends RecyclerView.Adapter<CharacterListAdapter.CharacterViewHolder> {
	//	private final int VIEW_CONTENT = 1;
	private AccountInfo account;
	private AccountInfo next;
	private WrapperProvider provider;

	CharacterListAdapter(@NonNull AccountInfo info, AccountInfo next, @NonNull WrapperProvider provider) {
		account = info;
		this.next = next;
		this.provider = provider;
	}

	@Override
	public CharacterListAdapter.CharacterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//		if(viewType==VIEW_CONTENT) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_inventory_character_item, parent, false);
		return new CharacterViewHolder(view);
//		}
//		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.progress_item, parent, false);
//		return new ProgressViewHolder(view);
	}

	@Override
	public void onBindViewHolder(CharacterListAdapter.CharacterViewHolder holder, int position) {
		holder.bind(account.getCharacters().get(position));
		if (!account.isSearched()) provider.onLoad(account);
		if ((account.getCharacters().size() == account.getCharacterNames().size()) && (next != null && !next.isSearched()))
			provider.onLoad(next);
	}

	@Override
	public int getItemCount() {
		return account.getCharacters().size();
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
					//TODO hide content
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

//	class ProgressViewHolder extends RecyclerView.ViewHolder {
//		@BindView(R.id.storage_progress)
//		ProgressBar progressBar;
//
//		private ProgressViewHolder(@NonNull View itemView) {
//			super(itemView);
//			ButterKnife.bind(this, itemView);
//		}
//	}
}
