package xhsun.gw2app.steve.backend.util.inventory;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ChildViewHolder;
import com.bignerdranch.expandablerecyclerview.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.ParentViewHolder;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import xhsun.gw2app.steve.R;
import xhsun.gw2app.steve.backend.database.account.AccountInfo;
import xhsun.gw2app.steve.backend.database.character.CharacterInfo;
import xhsun.gw2app.steve.backend.util.storage.StorageGridAdapter;

/**
 * List adapter for character inventory
 *
 * @author xhsun
 * @since 2017-03-31
 */

public class AccountListAdapter extends ExpandableRecyclerAdapter<AccountInfo, CharacterInfo, AccountListAdapter.AccountViewHolder, AccountListAdapter.CharacterViewHolder> {
	private LayoutInflater inflater;

	public AccountListAdapter(Context context, @NonNull List<AccountInfo> parentList) {
		super(parentList);
		inflater = LayoutInflater.from(context);
	}

	@NonNull
	@Override
	public AccountViewHolder onCreateParentViewHolder(@NonNull ViewGroup parentViewGroup, int viewType) {
		return new AccountViewHolder(inflater.inflate(R.layout.list_inventory_account_item, parentViewGroup, false));
	}

	@NonNull
	@Override
	public CharacterViewHolder onCreateChildViewHolder(@NonNull ViewGroup childViewGroup, int viewType) {
		return new CharacterViewHolder(inflater.inflate(R.layout.list_inventory_character_item, childViewGroup, false));
	}

	@Override
	public void onBindParentViewHolder(@NonNull AccountViewHolder parentViewHolder, int parentPosition, @NonNull AccountInfo parent) {
		parentViewHolder.bind(parent);
	}

	@Override
	public void onBindChildViewHolder(@NonNull CharacterViewHolder childViewHolder, int parentPosition, int childPosition, @NonNull CharacterInfo child) {
		childViewHolder.bind(child);
	}

	class AccountViewHolder extends ParentViewHolder {
		private AccountInfo account;
		@BindView(R.id.inventory_account_name)
		TextView name;

		AccountViewHolder(@NonNull View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}

		private void bind(AccountInfo info) {
			account = info;
			name.setText(account.getName());
		}
	}

	class CharacterViewHolder extends ChildViewHolder {
		private static final int SIZE = 51;
		private CharacterInfo character;
		@BindView(R.id.inventory_character_name)
		TextView name;
		@BindView(R.id.inventory_content_list)
		RecyclerView content;


		CharacterViewHolder(@NonNull View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}

		private void bind(CharacterInfo info) {
			character = info;
			name.setText(character.getName());
			name.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					//TODO hide content
				}
			});
			content.setLayoutManager(new GridLayoutManager(itemView.getContext(), calculateColumns()));
			content.setAdapter(new StorageGridAdapter(character.getInventory()));
		}

		private int calculateColumns() {
			DisplayMetrics displayMetrics = itemView.getContext().getResources().getDisplayMetrics();
			float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
			return (int) (dpWidth / SIZE);
		}
	}
}


