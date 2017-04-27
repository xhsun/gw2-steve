package xhsun.gw2app.steve.backend.util.inventory;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
	private Set<String> names;


	public AccountListAdapter(@NonNull OnLoadMoreListener listener, @NonNull List<AccountInfo> accounts) {
		this.accounts = accounts;
		this.listener = listener;
		names = new HashSet<>();
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
		int index;
		if ((index = accounts.indexOf(data)) >= 0) {
			accounts.remove(index);
			accounts.add(index, data);
			notifyItemChanged(index);
		} else {
			accounts.add(data);
			notifyItemInserted(accounts.size() - 1);
		}
	}

	public void addData(int index, @NonNull AccountInfo data, Set<String> names) {
		this.names = names;
		if (index >= accounts.size()) {
			accounts.add(data);
			notifyItemInserted(accounts.size() - 1);
		} else {
			accounts.add(index, data);
			notifyItemInserted(index);
		}
	}

	/**
	 * check if given account is displaying
	 * @param data account info
	 * @return boolean
	 */
	public boolean containData(AccountInfo data) {
		return accounts.contains(data);
	}

	/**
	 * @return list of all account that is currently displaying
	 */
	public AccountInfo getData(AccountInfo data) {
		if (!accounts.contains(data)) return null;
		return accounts.get(accounts.indexOf(data));
	}

	/**
	 * remove all data in the adapter and update view
	 */
	public void removeAllData() {
		for (AccountInfo a : accounts) if (a != null) a.setChild(null);
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
		if (data != null) data.setChild(null);
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
		//try to load more if current is not null
		if (listener.isMoreDataAvailable() && !listener.isLoading() && accounts.get(position) != null)
			listener.onLoadMore(accounts.get(position));
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
			data.setChild(characterList);
			name.setText(data.getName());
			//set up character list
			characterList.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
//			characterList.addItemDecoration(new DividerItemDecoration(characterList.getContext(), LinearLayoutManager.VERTICAL));
			characterList.setAdapter(new CharacterListAdapter(data, listener));
			if (names.size() > 0) {//display without load if the names are given
				listener.displayWithoutLoad(data, names);
				names = new HashSet<>();//reset
			}
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


