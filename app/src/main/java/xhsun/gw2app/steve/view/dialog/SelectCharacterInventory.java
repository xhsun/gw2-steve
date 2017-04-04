package xhsun.gw2app.steve.view.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import xhsun.gw2app.steve.R;
import xhsun.gw2app.steve.backend.database.account.AccountInfo;
import xhsun.gw2app.steve.backend.util.dialog.AccountHolder;
import xhsun.gw2app.steve.backend.util.dialog.SelectCharacterListAdapter;
import xhsun.gw2app.steve.backend.util.inventory.OnLoadMoreListener;

/**
 * Created by hannah on 03/04/17.
 */

public class SelectCharacterInventory extends DialogFragment {
	private List<AccountHolder> accounts;
	private OnLoadMoreListener listener;
	@BindView(R.id.dialog_storage_select_list)
	RecyclerView list;

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		View view = View.inflate(getContext(), R.layout.dialog_storage_select, null);
		ButterKnife.bind(this, view);

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setView(view);

		SelectCharacterListAdapter adapter = new SelectCharacterListAdapter(getContext(), accounts);

		list.setLayoutManager(new LinearLayoutManager(view.getContext()));
		list.setAdapter(adapter);

		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				SelectCharacterInventory.this.dismiss();
				setPreference();
			}
		})
				.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						SelectCharacterInventory.this.dismiss();
					}
				});
		return builder.create();
	}

	public void setAccounts(OnLoadMoreListener listener, List<AccountInfo> infos) {
		this.listener = listener;
		accounts = new ArrayList<>();
		for (AccountInfo a : infos) accounts.add(new AccountHolder(a, listener));
	}

	private void setPreference() {
		for (AccountHolder a : accounts)
			listener.setPreference(a.getName(), a.getSelectedCharacterNames());
	}
}
