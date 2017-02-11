package xhsun.gw2app.steve.view.account;

import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import xhsun.gw2app.steve.R;
import xhsun.gw2app.steve.database.account.AccountAPI;
import xhsun.gw2app.steve.database.account.AccountInfo;

/**
 * @author xhsun
 * @since 2017-02-05
 */
class AccountListAdapter extends RecyclerView.Adapter<AccountListAdapter.ViewHolder> {
	private static final int TIMEOUT = 2000;
	private AccountAPI accountAPI;
	private List<AccountInfo> accounts;
	private List<AccountInfo> pending;
	private AccountListListener listener;

	//for pending remove accounts
	private Handler handler = new Handler();
	private HashMap<AccountInfo, Runnable> pendingRunnables = new HashMap<>();

	AccountListAdapter(List<AccountInfo> items, AccountAPI api, AccountListListener listener) {
		accounts = items;
		pending = new ArrayList<>();
		this.listener = listener;
		accountAPI = api;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_account_item, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(final ViewHolder holder, int position) {
		holder.account = accounts.get(position);
		holder.name.setText(holder.account.getName());
		holder.world.setText(holder.account.getWorld());
		holder.access.setText(holder.account.getAccess());

		//mark out account with invalid api key
		if (!holder.account.isAccessible()) {
			holder.itemView.setBackgroundColor(0xFFBDBDBD);
			holder.name.setTextColor(0xFF757575);
			holder.world.setTextColor(0xFF9e9e9e);
			holder.access.setTextColor(0xFF9e9e9e);
			holder.invalid.setVisibility(View.VISIBLE);
		}

		if (pending.contains(holder.account)) {
			//enable and show undo button
			holder.undo.setVisibility(View.VISIBLE);
			holder.undo.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					//cancel pending task
					Runnable pendingRemovalRunnable = pendingRunnables.get(holder.account);
					pendingRunnables.remove(holder.account);
					if (pendingRemovalRunnable != null) handler.removeCallbacks(pendingRemovalRunnable);
					pending.remove(holder.account);
					// this will rebind the row in "normal" state
					notifyItemChanged(accounts.indexOf(holder.account));
				}
			});
			//disable click on list item
			holder.view.setOnClickListener(null);
		} else {
			//disable undo button
			holder.undo.setVisibility(View.GONE);
			holder.undo.setOnClickListener(null);

			//enable click on list item
			holder.view.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (null != accounts) {
						listener.onClick(holder.account);
					}
				}
			});
		}
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
	void pendingRemoval(int position) {
		final AccountInfo account = accounts.get(position);
		if (!pending.contains(account)) {
			pending.add(account);
			// this will redraw row in "undo" state
			notifyItemChanged(position);
			// let's create, store and post a runnable to remove the item
			Runnable pendingRemovalRunnable = new Runnable() {
				@Override
				public void run() {
					remove(accounts.indexOf(account));
				}
			};
			handler.postDelayed(pendingRemovalRunnable, TIMEOUT);
			pendingRunnables.put(account, pendingRemovalRunnable);
		}
	}

	/**
	 * is the account pending to be removed
	 *
	 * @param position of the account
	 * @return true | false
	 */
	boolean isPendingRemoval(int position) {
		AccountInfo account = accounts.get(position);
		return pending.contains(account);
	}

	/**
	 * officially remove the account from the list
	 *
	 * @param position of the account
	 */
	private void remove(int position) {
		AccountInfo account = accounts.get(position);
		if (pending.contains(account)) pending.remove(account);
		if (accounts.contains(account)) {
			accountAPI.removeAccount(account);
			accounts.remove(position);
			notifyItemRemoved(position);
		}
	}

	class ViewHolder extends RecyclerView.ViewHolder {
		View view;
		Button undo;
		AccountInfo account;
		TextView name;
		TextView world;
		TextView access;
		TextView invalid;

		ViewHolder(View view) {
			super(view);
			this.view = view;
			undo = (Button) view.findViewById(R.id.account_btn_undo);
			name = (TextView) view.findViewById(R.id.account_name);
			world = (TextView) view.findViewById(R.id.account_world);
			access = (TextView) view.findViewById(R.id.account_access);
			invalid = (TextView) view.findViewById(R.id.account_invalid);
		}
	}
}
