package xhsun.gw2app.steve.backend.util.account;

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
import xhsun.gw2app.steve.backend.database.account.AccountWrapper;
import xhsun.gw2app.steve.backend.util.Utility;
import xhsun.gw2app.steve.backend.util.dialog.CustomAlertDialogListener;
import xhsun.gw2app.steve.backend.util.dialog.DialogManager;
import xhsun.gw2app.steve.view.fragment.AccountFragment;

/**
 * account list adapter
 *
 * @author xhsun
 * @since 2017-02-05
 */

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {
	private AccountWrapper wrapper;
	private ListOnClickListener listener;
	private List<AccountInfo> accounts;

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
		this.accounts = accounts;
	}

	/**
	 * add data to list
	 *
	 * @param account account info
	 */
	public void addData(AccountInfo account) {
		accounts.add(account);
	}

	/**
	 * remove data from the list and database
	 *
	 * @param account account info
	 */
	public void removeData(AccountInfo account) {
		int index = accounts.indexOf(account);
		if (index == -1) return;
		remove(index);
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_account_item, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		holder.account = accounts.get(position);
		holder.setInfo();

		//mark out account with invalid api key
		if (!holder.account.isValid() || holder.account.isClosed()) {
			holder.setInvalid();
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
	class ViewHolder extends RecyclerView.ViewHolder {
		AccountInfo account;
		//display
		@BindView(R.id.account_name)
		TextView name;
		@BindView(R.id.account_world)
		TextView world;
		@BindView(R.id.account_access)
		TextView access;

		ViewHolder(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}

		//set all text fields
		private void setInfo() {
			this.name.setText(account.getName());
			this.world.setText(account.getWorld());
			this.access.setText(account.getAccess());
		}

		//show invalid masks
		private void setInvalid() {
			itemView.setBackgroundColor(Utility.UNDO_BKG);
			name.setTextColor(Utility.UNDO_TITLE);
			world.setTextColor(Utility.UNDO_SUBTITLE);
			access.setTextColor(Utility.UNDO_SUBTITLE);
		}
	}
}
