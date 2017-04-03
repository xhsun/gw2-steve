package xhsun.gw2app.steve.backend.util.inventory;

import android.support.annotation.NonNull;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import xhsun.gw2app.steve.R;
import xhsun.gw2app.steve.backend.database.account.AccountInfo;
import xhsun.gw2app.steve.backend.util.ViewHolder;

/**
 * List adapter for character inventory
 *
 * @author xhsun
 * @since 2017-03-31
 */

public class AccountListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
	private static final int TYPE_CONTENT = 0;
	private static final int TYPE_LOAD = 1;
	private List<AccountInfo> accounts;
	private OnLoadMoreListener listener;


	public AccountListAdapter(@NonNull OnLoadMoreListener listener, @NonNull List<AccountInfo> accounts) {
		this.accounts = accounts;
		this.listener = listener;
	}

	public void setData(List<AccountInfo> data) {
		accounts = data;
		notifyDataSetChanged();
	}

	/**
	 * insert data in the adapter and update view
	 *
	 * @param data account info
	 */
	public void addData(AccountInfo data) {
		accounts.add(data);
		notifyItemInserted(accounts.size() - 1);
	}

	/**
	 * remove all data in the adapter and update view
	 */
	public void removeAllData() {
		accounts.clear();
		notifyDataSetChanged();
	}

	/**
	 * remove data from list
	 *
	 * @param data account info
	 * @return index of data | -1 if not find
	 */
	public int removeData(AccountInfo data) {
		int index = accounts.indexOf(data);
		accounts.remove(data);
		return index;
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		if (viewType == TYPE_CONTENT)
			return new AccountViewHolder(inflater.inflate(R.layout.list_inventory_account_item, parent, false));
		else return new ProgressViewHolder(inflater.inflate(R.layout.progress_item, parent, false));
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		if (holder instanceof AccountViewHolder)
			((AccountViewHolder) holder).bind(accounts.get(position));
		if (listener.isMoreDataAvailable() && !listener.isLoading()) listener.OnLoadMore(accounts.get(position));
	}

	@Override
	public int getItemViewType(int position) {
		if (accounts.get(position) == null) return TYPE_LOAD;
		else return TYPE_CONTENT;
	}

	@Override
	public int getItemCount() {
		return accounts.size();
	}

	class AccountViewHolder extends ViewHolder<AccountInfo> {
		@BindView(R.id.inventory_account_name)
		TextView name;
		@BindView(R.id.inventory_character_list)
		RecyclerView characterList;

		private AccountViewHolder(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}

		protected void bind(AccountInfo info) {
			data = info;
			name.setText(data.getName());
			name.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					//TODO hide?
				}
			});
			//set up character list
			characterList.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
			characterList.addItemDecoration(new DividerItemDecoration(characterList.getContext(), LinearLayoutManager.VERTICAL));
			data.setAdapter(new CharacterListAdapter(data, listener));
			characterList.setAdapter(data.getAdapter());
		}
	}

	class ProgressViewHolder extends RecyclerView.ViewHolder {
		@BindView(R.id.storage_progress)
		ProgressBar progressBar;

		private ProgressViewHolder(@NonNull View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}
	}
}


