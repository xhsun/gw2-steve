package xhsun.gw2app.steve.backend.util.storage;

import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
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
import xhsun.gw2app.steve.backend.util.Utility;
import xhsun.gw2app.steve.backend.util.ViewHolder;
import xhsun.gw2app.steve.backend.util.items.OnLoadMoreListener;
import xhsun.gw2app.steve.backend.util.items.ProgressViewHolder;
import xhsun.gw2app.steve.backend.util.items.StorageGridAdapter;

/**
 * list adapter for bank items
 *
 * @author xhsun
 * @since 2017-05-04
 */

public class BankListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
	private static final int TYPE_CONTENT = 0;
	private static final int TYPE_LOAD = 1;
	private List<AccountInfo> accounts;
	private OnLoadMoreListener listener;

	public BankListAdapter(@NonNull OnLoadMoreListener listener, @NonNull List<AccountInfo> accounts) {
		this.accounts = accounts;
		this.listener = listener;
	}

	public void setData(@NonNull List<AccountInfo> data) {
		accounts = data;
		notifyDataSetChanged();
	}

	/**
	 * insert data in the adapter and update view
	 * Note: this method will also set loading to false
	 *
	 * @param data account info
	 */
	public void addData(AccountInfo data) {
		int index;
		if (data == null) {
			accounts.add(null);
			notifyItemInserted(accounts.size() - 1);
			return;
		}
		if ((index = accounts.indexOf(data)) >= 0) {
			accounts.remove(index);
			accounts.add(index, data);
			notifyItemChanged(index);
		} else {
			addData(listener.getAccounts().indexOf(data), data);
		}
		listener.setLoading(false);
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
	public void removeData(AccountInfo data) {
		int index = accounts.indexOf(data);
		accounts.remove(data);
		if (data != null) data.setChild(null);
		notifyItemRemoved(index);
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		if (viewType == TYPE_CONTENT)
			return new AccountViewHolder(inflater.inflate(R.layout.list_storage_account_item, parent, false));
		else return new ProgressViewHolder(inflater.inflate(R.layout.progress_item, parent, false));
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		if (holder instanceof ProgressViewHolder) return;
		final AccountInfo account = accounts.get(position);
		((AccountViewHolder) holder).bind(account);
		if (listener.isMoreDataAvailable() && !listener.isLoading())
			listener.provideParentView().post(new Runnable() {
				@Override
				public void run() {
					listener.onLoadMore(account);//reached end of list try to get more
				}
			});
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
		@BindView(R.id.storage_account_name)
		TextView name;
		@BindView(R.id.storage_sublist)
		RecyclerView bankList;

		AccountViewHolder(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}

		@Override
		protected void bind(AccountInfo info) {
			data = info;
			data.setChild(bankList);
			String cappedName = data.getName().substring(0, 1).toUpperCase() + data.getName().substring(1);
			name.setText(cappedName);
			//set up bank list
			StorageGridAdapter adapter = new StorageGridAdapter(data.getBank());
			bankList.setLayoutManager(new GridLayoutManager(itemView.getContext(),
					Utility.calculateColumns(itemView)));
			bankList.setAdapter(adapter);
		}
	}
}
