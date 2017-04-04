package xhsun.gw2app.steve.backend.util.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bignerdranch.expandablerecyclerview.ChildViewHolder;
import com.bignerdranch.expandablerecyclerview.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.ParentViewHolder;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import xhsun.gw2app.steve.R;

/**
 * Created by hannah on 03/04/17.
 */

public class SelectCharacterListAdapter extends ExpandableRecyclerAdapter<AccountHolder, AccountHolder.CharacterHolder,
		SelectCharacterListAdapter.AccountViewHolder, SelectCharacterListAdapter.CharacterViewHolder> {
	private LayoutInflater inflater;

	public SelectCharacterListAdapter(Context context, @NonNull List<AccountHolder> parentList) {
		super(parentList);
		inflater = LayoutInflater.from(context);
	}

	@NonNull
	@Override
	public AccountViewHolder onCreateParentViewHolder(@NonNull ViewGroup parentViewGroup, int viewType) {
		return new AccountViewHolder(inflater.inflate(R.layout.list_dialog_select_parent_item, parentViewGroup, false));
	}

	@NonNull
	@Override
	public CharacterViewHolder onCreateChildViewHolder(@NonNull ViewGroup childViewGroup, int viewType) {
		return new CharacterViewHolder(inflater.inflate(R.layout.list_dialog_select_child_item, childViewGroup, false));
	}

	@Override
	public void onBindParentViewHolder(@NonNull AccountViewHolder parentViewHolder, int parentPosition, @NonNull AccountHolder parent) {
		parent.setView(parentViewHolder);
		parentViewHolder.bind(parent);
	}

	@Override
	public void onBindChildViewHolder(@NonNull CharacterViewHolder childViewHolder, int parentPosition, int childPosition, @NonNull AccountHolder.CharacterHolder child) {
		childViewHolder.bind(child);//TODO child share view holder... better create my own expandable list I guess
	}

	class AccountViewHolder extends ParentViewHolder {
		private AccountHolder account;
		private ParentOnCheckedListener listener;
		@BindView(R.id.dialog_storage_select_parent_wrapper)
		RelativeLayout wrapper;
		@BindView(R.id.dialog_select_arrow)
		ImageView image;
		@BindView(R.id.dialog_storage_select_parent)
		CheckBox check;

		AccountViewHolder(@NonNull View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}

		private void bind(AccountHolder data) {
			account = data;
			account.setView(this);
			check.setText(account.getName());
			listener = new ParentOnCheckedListener(account);
			check.setOnCheckedChangeListener(listener);
			wrapper.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (isExpanded()) {
						collapseView();
						image.setImageResource(R.drawable.ic_arrow_down);
					} else {
						expandView();
						image.setImageResource(R.drawable.ic_arrow_up);
					}
				}
			});
		}

		void deselect() {
			if (check.isChecked()) {
				listener.isChild = true;
				check.setChecked(false);
			}
		}

		@Override
		public boolean shouldItemViewClickToggleExpansion() {
			return false;
		}
	}

	class CharacterViewHolder extends ChildViewHolder {
		AccountHolder.CharacterHolder character;
		@BindView(R.id.dialog_storage_select_child)
		CheckBox check;

		CharacterViewHolder(@NonNull View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}

		private void bind(AccountHolder.CharacterHolder data) {
			character = data;
			character.setChildView(this);
			check.setText(character.getName());
			check.setChecked(character.isSelected());
			check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					character.setSelected(isChecked);
				}
			});
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
			if (!isChild) accounts.setAllSelected(isChecked);
			if (!isChecked) accounts.resetIsParent();
			isChild = false;
		}
	}
}
