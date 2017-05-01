package xhsun.gw2app.steve.backend.util.dialog;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import xhsun.gw2app.steve.R;
import xhsun.gw2app.steve.backend.util.ViewHolder;

/**
 * Recycler view adapter for select character inventory
 *
 * @author xhsun
 * @since 2017-04-03
 */

public class SelectCharacterListAdapter extends RecyclerView.Adapter<SelectCharacterListAdapter.AccountViewHolder> {
	private List<AccountHolder> accounts;

	public SelectCharacterListAdapter(@NonNull List<AccountHolder> accounts) {
		this.accounts = accounts;
	}

	@Override
	public SelectCharacterListAdapter.AccountViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new AccountViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_dialog_select_parent_item, parent, false));
	}

	@Override
	public void onBindViewHolder(SelectCharacterListAdapter.AccountViewHolder holder, int position) {
		holder.bind(accounts.get(position));
	}

	@Override
	public int getItemCount() {
		return accounts.size();
	}

	class AccountViewHolder extends ViewHolder<AccountHolder> {
		private ParentOnCheckedListener listener;
		@BindView(R.id.dialog_storage_select_child_list)
		RecyclerView child;
		@BindView(R.id.dialog_select_arrow)
		ImageView image;
		@BindView(R.id.dialog_storage_select_parent)
		CheckBox check;

		AccountViewHolder(@NonNull View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}

		protected void bind(AccountHolder info) {
			data = info;
			data.setView(this);
			listener = new ParentOnCheckedListener(data);
			//init check box
			String cappedName = data.getName().substring(0, 1).toUpperCase() + data.getName().substring(1);
			check.setText(cappedName);
			check.setOnCheckedChangeListener(listener);

			//init child recycler view
			child.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
			child.setAdapter(new ChildListAdapter(data, data.getChildList()));
			child.setVisibility(View.GONE);

			//init expandable
			itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (child.getVisibility() == View.VISIBLE) {
						child.setVisibility(View.GONE);
						image.setImageResource(R.drawable.ic_arrow_down);
					} else {
						child.setVisibility(View.VISIBLE);
						image.setImageResource(R.drawable.ic_arrow_up);
					}
				}
			});
		}

		//deselect check box from child
		private void deselect() {
			if (!check.isChecked()) return;
			listener.isChild = true;
			check.setChecked(false);
		}
	}

	class ChildListAdapter extends RecyclerView.Adapter<ChildListAdapter.CharacterViewHolder> {
		private AccountHolder account;
		private List<AccountHolder.CharacterHolder> child;

		private ChildListAdapter(@NonNull AccountHolder account, @NonNull List<AccountHolder.CharacterHolder> child) {
			this.account = account;
			this.child = child;
		}


		@Override
		public CharacterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			return new CharacterViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_dialog_select_child_item, parent, false));
		}

		@Override
		public void onBindViewHolder(CharacterViewHolder holder, int position) {
			holder.bind(child.get(position));
		}

		@Override
		public int getItemCount() {
			return child.size();
		}

		class CharacterViewHolder extends ViewHolder<AccountHolder.CharacterHolder> {
			@BindView(R.id.dialog_storage_select_child)
			CheckBox check;

			CharacterViewHolder(@NonNull View itemView) {
				super(itemView);
				ButterKnife.bind(this, itemView);
			}

			protected void bind(AccountHolder.CharacterHolder info) {
				data = info;
				data.setChildView(this);
				//setup check box
				check.setText(data.getName());
				check.setChecked(data.isSelected());
				check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						data.setSelected(isChecked);
						//deselect parent, only if check box is deselect
						if (!isChecked) account.getHolder().deselect();
					}
				});
			}
		}
	}

	private class ParentOnCheckedListener implements CompoundButton.OnCheckedChangeListener {
		private AccountHolder accounts;
		private boolean isChild = false;

		private ParentOnCheckedListener(AccountHolder accounts) {
			this.accounts = accounts;
		}

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			//don't propagate if selection is init by child
			if (!isChild) accounts.setAllSelected(isChecked);
			isChild = false;
		}
	}
}
