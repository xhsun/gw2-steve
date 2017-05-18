package xhsun.gw2app.steve.backend.util.account;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import xhsun.gw2app.steve.R;
import xhsun.gw2app.steve.backend.data.AccountInfo;
import xhsun.gw2app.steve.backend.database.account.AccountWrapper;
import xhsun.gw2app.steve.backend.util.Utility;
import xhsun.gw2app.steve.backend.util.ViewHolder;
import xhsun.gw2app.steve.backend.util.dialog.CustomAlertDialogListener;
import xhsun.gw2app.steve.view.dialog.DialogManager;
import xhsun.gw2app.steve.view.fragment.AccountFragment;

/**
 * account list adapter
 *
 * @author xhsun
 * @since 2017-02-05
 */

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.AccountViewHolder> {
	private AccountWrapper wrapper;
	private ListOnClickListener listener;
	private List<AccountInfo> accounts;

	public ListAdapter(ListOnClickListener listener, @NonNull List<AccountInfo> accounts, AccountWrapper wrapper) {
		this.listener = listener;
		this.wrapper = wrapper;
		this.accounts = accounts;
	}

	/**
	 * override data in the list
	 *
	 * @param data list of account info
	 */
	public void setData(@NonNull List<AccountInfo> data) {
		accounts = data;
		notifyDataSetChanged();
	}

	/**
	 * add data to list
	 *
	 * @param data account info
	 */
	public void addData(@NonNull AccountInfo data) {
		accounts.add(data);
		notifyItemInserted(accounts.size() - 1);
	}

	/**
	 * remove data from the list and database
	 *
	 * @param data account info
	 */
	public void removeData(@NonNull AccountInfo data) {
		int index;
		if ((index = accounts.indexOf(data)) == -1) return;
		remove(index);
	}

	@Override
	public AccountViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_account, parent, false);
		return new AccountViewHolder(view);
	}

	@Override
	public void onBindViewHolder(AccountViewHolder holder, int position) {
		holder.setPosition(position);
		holder.bind(accounts.get(position));
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
	void initRemoval(final int position) {
		new DialogManager(((AccountFragment) listener).getFragmentManager())
				.customAlert("Remove Account", "Do you want to remove this account?", new CustomAlertDialogListener() {
					@Override
					public void onPositiveClick() {
						remove(position);
					}

					@Override
					public void onNegativeClick() {
						notifyItemChanged(position);
					}
				});
	}

	/**
	 * officially remove the account from the list
	 *
	 * @param position of the account
	 */
	private void remove(int position) {
		AccountInfo account = accounts.get(position);
		if (account == null) return;
		wrapper.removeAccount(account);
		accounts.remove(position);
		notifyItemRemoved(position);
	}

	//view holder for account list view
	class AccountViewHolder extends ViewHolder<AccountInfo> {
		private int position;
		//display
		@BindView(R.id.account_name)
		TextView name;
		@BindView(R.id.account_world)
		TextView world;
		@BindView(R.id.account_access)
		TextView access;

		AccountViewHolder(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}

		private void setPosition(int position) {
			this.position = position;
		}

		@Override
		protected void bind(AccountInfo info) {
			data = info;
			String cappedName = data.getName().substring(0, 1).toUpperCase() + data.getName().substring(1);
			name.setText(cappedName);
			world.setText(data.getWorld());
			access.setText(data.getAccess());
			itemView.setOnClickListener(v -> listener.onListItemClick(data));

			if (!data.isValid() || data.isClosed()) setInvalid(position);
		}

		//show invalid masks
		private void setInvalid(final int position) {
			itemView.setBackgroundColor(Utility.UNDO_BKG);
			name.setTextColor(Utility.UNDO_TITLE);
			world.setTextColor(Utility.UNDO_SUBTITLE);
			access.setTextColor(Utility.UNDO_SUBTITLE);
			itemView.setOnClickListener(v -> new DialogManager(((AccountFragment) listener).getFragmentManager())
					.customAlert("Remove Account", "Do you want to remove this account?",
							new CustomAlertDialogListener() {
								@Override
								public void onPositiveClick() {
									remove(position);
								}

								@Override
								public void onNegativeClick() {
								}
							}));
		}
	}
}
