package xhsun.gw2app.steve.backend.util.inventory;

import android.support.annotation.NonNull;
import android.support.v7.widget.DividerItemDecoration;
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
	private WrapperProvider provider;

	public AccountListAdapter(@NonNull WrapperProvider provider, @NonNull List<AccountInfo> accounts) {
		this.accounts = accounts;
		this.provider = provider;
	}

	/**
	 * replace data in the adapter and update view
	 *
	 * @param data list of item info
	 */
	public void setData(@NonNull List<AccountInfo> data) {
		accounts = data;
		notifyDataSetChanged();
	}

	@Override
	public AccountViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_inventory_account_item, parent, false);
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

	class AccountViewHolder extends ViewHolder<AccountInfo> {
		private int position;
		@BindView(R.id.inventory_account_name)
		TextView name;
		@BindView(R.id.inventory_character_list)
		RecyclerView characterList;

		private AccountViewHolder(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}

		private void setPosition(int position) {
			this.position = position;
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
			characterList.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
			characterList.addItemDecoration(new DividerItemDecoration(characterList.getContext(), LinearLayoutManager.VERTICAL));
			data.setAdapter(new CharacterListAdapter(data, ((accounts.size() - 1) != position) ? accounts.get(position + 1) : null, provider));
			characterList.setAdapter(data.getAdapter());
		}
	}
}


