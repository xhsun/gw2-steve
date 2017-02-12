package xhsun.gw2app.steve.view.account;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import xhsun.gw2app.steve.R;
import xhsun.gw2app.steve.database.account.AccountAPI;
import xhsun.gw2app.steve.database.account.AccountInfo;
import xhsun.gw2app.steve.util.constant.Color;
import xhsun.gw2app.steve.util.listener.AccountListListener;

/**
 * account list adapter
 * @author xhsun
 * @since 2017-02-05
 */
class AccountListAdapter extends RecyclerView.Adapter<AccountListAdapter.ViewHolder> {
	private final String TAG = this.getClass().getSimpleName();
	private static final int TIMEOUT = 2000;
	private float density;
	private AccountAPI accountAPI;
	private List<AccountInfo> accounts;
	private List<AccountInfo> pending;
	private AccountListListener listener;

	//for pending remove accounts
	private Handler handler = new Handler();
	private HashMap<AccountInfo, Runnable> pendingRunnables = new HashMap<>();

	AccountListAdapter(List<AccountInfo> items, AccountAPI api, Context context, AccountListListener listener) {
		this.density = context.getResources().getDisplayMetrics().density;
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
		holder.setInfo();

		//mark out account with invalid api key
		if (!holder.account.isAccessible() || holder.account.isClosed()) {
			Log.w(TAG, "Account " + holder.account.getAPI() + " is inaccessible");
			holder.setInvalid();
		}

		if (pending.contains(holder.account)) holder.showUndo();
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

	private int calculateDP(int input) {
		return (int) (input * density); // margin in pixels
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
			Log.d(TAG, "remove: Remove account (" + account.getAPI() + ")");
			accountAPI.removeAccount(account);
			accounts.remove(position);
			notifyItemRemoved(position);
		}
	}

	class ViewHolder extends RecyclerView.ViewHolder {
		private View view;
		AccountInfo account;
		//error
		private TextView invalid;
		//display
		private TextView name;
		private TextView world;
		private TextView access;
		//undo
		private FrameLayout undoLayout;
		private Button undo;
		private TextView undoName;
		private TextView undoWorld;
		private TextView undoAccess;

		ViewHolder(View view) {
			super(view);
			this.view = view;
			invalid = (TextView) view.findViewById(R.id.account_invalid);

			name = (TextView) view.findViewById(R.id.account_name);
			world = (TextView) view.findViewById(R.id.account_world);
			access = (TextView) view.findViewById(R.id.account_access);

			undoLayout = (FrameLayout) view.findViewById(R.id.account_layout_undo);
			undo = (Button) view.findViewById(R.id.account_btn_undo);
			undoName = (TextView) view.findViewById(R.id.account_name_undo);
			undoWorld = (TextView) view.findViewById(R.id.account_world_undo);
			undoAccess = (TextView) view.findViewById(R.id.account_access_undo);
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
					pending.remove(account);
					// this will rebind the row in "normal" state
					notifyItemChanged(accounts.indexOf(account));
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
						listener.onClick(account);
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
			itemView.setBackgroundColor(Color.LightGrey);
			name.setTextColor(Color.DarkGrey);
			undoName.setTextColor(Color.DarkGrey);
			world.setTextColor(Color.Grey);
			undoWorld.setTextColor(Color.Grey);
			access.setTextColor(Color.Grey);
			undoAccess.setTextColor(Color.Grey);
			invalid.setVisibility(View.VISIBLE);
		}
	}
}
