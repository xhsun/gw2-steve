package xhsun.gw2app.steve.backend.util.inventory;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class AccountListAdapter extends RecyclerView.Adapter<AccountListAdapter.AccountViewHolder> {
	private List<AccountInfo> accounts;
	private OnLoadMoreListener listener;


	public AccountListAdapter(@NonNull OnLoadMoreListener listener, @NonNull List<AccountInfo> accounts) {
		this.accounts = accounts;
		this.listener = listener;
	}

	public void setData(@NonNull List<AccountInfo> data) {
		accounts = data;
		notifyDataSetChanged();
	}

	/**
	 * insert data in the adapter and update view
	 *
	 * @param data account info
	 */
	public void addData(@NonNull AccountInfo data) {
		int index;
		if ((index = accounts.indexOf(data)) >= 0) {
			accounts.remove(index);
			accounts.add(index, data);
			notifyItemChanged(index);
		} else {
			addData(listener.getAccounts().indexOf(data), data);
		}
	}

	/**
	 * insert data at given position
	 * if given index is greater than list size, insert data at end of list
	 *
	 * @param index position in the list
	 * @param data  account info
	 */
	private void addData(int index, @NonNull AccountInfo data) {
		if (index >= accounts.size()) {
			accounts.add(data);
			notifyItemInserted(accounts.size() - 1);
		} else {
			accounts.add(index, data);
			notifyItemInserted(index);
		}
	}

	/**
	 * @return list of all account that is currently displaying
	 */
	AccountInfo getData(@NonNull AccountInfo data) {
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
	 */
	public void removeData(@NonNull AccountInfo data) {
		int index = accounts.indexOf(data);
		accounts.remove(data);
		data.setChild(null);
		notifyItemRemoved(index);
	}

	@Override
	public AccountViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new AccountViewHolder(LayoutInflater.from(parent.getContext()).
				inflate(R.layout.list_storage_account_item, parent, false));
	}

	@Override
	public void onBindViewHolder(AccountViewHolder holder, int position) {
		holder.bind(accounts.get(position));
		//try to load more if current is not null
		if (listener.isMoreDataAvailable() && !listener.isLoading() && accounts.get(position) != null) {
			final int index = position;
			listener.provideParentView().post(new Runnable() {
				@Override
				public void run() {
					listener.onLoadMore(accounts.get(index));
				}
			});
		}
	}

	@Override
	public int getItemCount() {
		return accounts.size();
	}

	class AccountViewHolder extends ViewHolder<AccountInfo> {
		@BindView(R.id.storage_account_name)
		TextView name;
		@BindView(R.id.storage_sublist)
		RecyclerView characterList;

		private AccountViewHolder(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}

		protected void bind(AccountInfo info) {
			data = info;
			data.setChild(characterList);
			String cappedName = data.getName().substring(0, 1).toUpperCase() + data.getName().substring(1);
			name.setText(cappedName);
			//set up character list
			characterList.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
			characterList.setAdapter(new CharacterListAdapter(data, listener));
			characterList.setNestedScrollingEnabled(false);
		}
	}
}


