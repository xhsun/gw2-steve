package xhsun.gw2app.steve.backend.util.account;

import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import xhsun.gw2app.steve.R;
import xhsun.gw2app.steve.backend.database.account.AccountInfo;
import xhsun.gw2app.steve.backend.database.account.AccountWrapper;
import xhsun.gw2app.steve.backend.util.Utility;

/**
 * account list adapter
 *
 * @author xhsun
 * @since 2017-02-05
 */

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {
	private static final int TIMEOUT = 2000;
	private AccountWrapper wrapper;
	private ListOnClickListener listener;
	private List<Account> accounts;

	//for pending remove accounts
	private Handler handler = new Handler();
	private HashMap<AccountInfo, Runnable> pendingRunnables = new HashMap<>();

	public ListAdapter(ListOnClickListener listener, List<AccountInfo> accounts, AccountWrapper wrapper) {
		this.listener = listener;
		this.wrapper = wrapper;
		setData(accounts);
	}

	/**
	 * override data in the list
	 *
	 * @param accounts list of account info
	 */
	public void setData(List<AccountInfo> accounts) {
		this.accounts = new ArrayList<>();
		for (AccountInfo a : accounts) this.accounts.add(new Account(a));
	}

	/**
	 * add data to list
	 *
	 * @param account account info
	 */
	public void addData(AccountInfo account) {
		accounts.add(new Account(account));
	}

	/**
	 * remove data from the list and database
	 *
	 * @param account account info
	 */
	public void removeData(AccountInfo account) {
		int index = accounts.indexOf(new Account(account));
		if (index == -1) return;
		remove(index);
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_account_item, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		holder.account = accounts.get(position).info;
		holder.setInfo();

		//mark out account with invalid api key
		if (!holder.account.isValid() || holder.account.isClosed()) {
			holder.setInvalid();
		}

		if (accounts.get(position).isPending) holder.showUndo();
		else holder.hideUndo();
	}

	@Override
	public int getItemCount() {
		return accounts.size();
	}

	/**
	 * move account to pending remove list and set timeout
	 *
	 * @param position of the account
	 */
	void pendingRemoval(final int position) {
		Account account = accounts.get(position);
		if (account == null || account.isPending) return;
		account.isPending = true;
		// this will redraw row in "undo" state
		notifyItemChanged(position);
		// let's create, store and post a runnable to remove the item
		Runnable pendingRemovalRunnable = new Runnable() {
			@Override
			public void run() {
				remove(position);
			}
		};
		handler.postDelayed(pendingRemovalRunnable, TIMEOUT);
		pendingRunnables.put(account.info, pendingRemovalRunnable);
	}

	/**
	 * officially remove the account from the list
	 *
	 * @param position of the account
	 */
	private void remove(int position) {
		Account account = accounts.get(position);
		if (account == null) return;
		wrapper.removeAccount(account.info);
		accounts.remove(position);
		notifyItemRemoved(position);
	}

	/**
	 * is the account pending to be removed
	 *
	 * @param position of the account
	 * @return true | false
	 */
	boolean isPendingRemoval(int position) {
		return accounts.get(position).isPending;
	}

	//view holder for account list view
	class ViewHolder extends RecyclerView.ViewHolder {
		private View view;
		AccountInfo account;
		//display
		@BindView(R.id.account_name)
		TextView name;
		@BindView(R.id.account_world)
		TextView world;
		@BindView(R.id.account_access)
		TextView access;
		//undo
		@BindView(R.id.account_layout_undo)
		FrameLayout undoLayout;
		@BindView(R.id.account_btn_undo)
		ImageButton undo;
		@BindView(R.id.account_name_undo)
		TextView undoName;
		@BindView(R.id.account_world_undo)
		TextView undoWorld;
		@BindView(R.id.account_access_undo)
		TextView undoAccess;

		ViewHolder(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);

			view = itemView;
		}

		//show undo layout
		private void showUndo() {
			//hide "normal" content
			view.setOnClickListener(null);
			name.setVisibility(View.GONE);
			world.setVisibility(View.GONE);
			access.setVisibility(View.GONE);
			//show undo layout
			undoLayout.setVisibility(View.VISIBLE);
			undo.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					//cancel pending task
					Runnable pendingRemovalRunnable = pendingRunnables.get(account);
					pendingRunnables.remove(account);
					if (pendingRemovalRunnable != null) handler.removeCallbacks(pendingRemovalRunnable);

					int index = accounts.indexOf(new Account(account));
					if (index == -1) return;//well... it doesn't exist anymore
					accounts.get(index).isPending = false;
					// this will rebind the row in "normal" state
					notifyItemChanged(index);
				}
			});
		}

		//hide undo layout
		private void hideUndo() {
			//hide undo layout
			undo.setOnClickListener(null);
			undoLayout.setVisibility(View.GONE);
			//show normal content
			name.setVisibility(View.VISIBLE);
			world.setVisibility(View.VISIBLE);
			access.setVisibility(View.VISIBLE);
			view.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (null != accounts) {
						listener.onListItemClick(account);
					}
				}
			});
		}

		//set all text fields
		private void setInfo() {
			this.name.setText(account.getName());
			undoName.setText(account.getName());
			this.world.setText(account.getWorld());
			undoWorld.setText(account.getWorld());
			this.access.setText(account.getAccess());
			undoAccess.setText(account.getAccess());
		}

		//show invalid masks
		private void setInvalid() {
			itemView.setBackgroundColor(Utility.UNDO_BKG);
			name.setTextColor(Utility.UNDO_TITLE);
			world.setTextColor(Utility.UNDO_SUBTITLE);
			access.setTextColor(Utility.UNDO_SUBTITLE);
		}
	}

	//class to hold account info and pending status
	private class Account {
		private AccountInfo info;
		private boolean isPending = false;

		Account(AccountInfo accountInfo) {
			info = accountInfo;
		}

		@Override
		public int hashCode() {
			return info.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			return this == obj || obj != null && getClass() == obj.getClass() && ((Account) obj).info.getAPI().equals(info.getAPI());
		}
	}
}
