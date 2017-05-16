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
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import xhsun.gw2app.steve.R;
import xhsun.gw2app.steve.backend.data.AccountInfo;
import xhsun.gw2app.steve.backend.util.dialog.selectCharacter.AccountHolder;
import xhsun.gw2app.steve.backend.util.dialog.selectCharacter.SelectCharacterListAdapter;
import xhsun.gw2app.steve.backend.util.vault.OnPreferenceChangeListener;
import xhsun.gw2app.steve.backend.util.vault.VaultType;

/**
 * Select character dialog
 *
 * @author xhsun
 * @since 2017-04-03
 */

public class SelectCharacters extends DialogFragment {
	private List<AccountHolder> accounts;
	private OnPreferenceChangeListener<AccountHolder> listener;
	@BindView(R.id.dialog_select_list)
	RecyclerView list;
	@BindView(R.id.dialog_select_title)
	TextView title;

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		View view = View.inflate(getContext(), R.layout.dialog_pref_setter, null);
		ButterKnife.bind(this, view);

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setView(view);

		title.setText(R.string.dialog_select_character_title);

		SelectCharacterListAdapter adapter = new SelectCharacterListAdapter(accounts);

		list.setLayoutManager(new LinearLayoutManager(view.getContext()));
		list.setAdapter(adapter);

		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				SelectCharacters.this.dismiss();
				setPreference();
			}
		})
				.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						SelectCharacters.this.dismiss();
					}
				});
		return builder.create();
	}

	/**
	 * list of account that can be selected from
	 *
	 * @param listener for set preference call back
	 */
	public void setAccounts(OnPreferenceChangeListener<AccountHolder> listener, List<AccountInfo> accounts,
	                        Map<AccountInfo, Set<String>> preference) {
		this.listener = listener;
		this.accounts = new ArrayList<>();
		for (AccountInfo a : accounts) {
			if (a.getAllCharacterNames().size() > 0)
				this.accounts.add(new AccountHolder(a, preference.get(a)));
		}
	}

	//set preference
	private void setPreference() {
		listener.notifyPreferenceChange(VaultType.INVENTORY, new HashSet<>(accounts));
	}
}
