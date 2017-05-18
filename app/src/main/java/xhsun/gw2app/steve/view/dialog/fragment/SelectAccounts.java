package xhsun.gw2app.steve.view.dialog.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import xhsun.gw2app.steve.R;
import xhsun.gw2app.steve.backend.data.AccountData;
import xhsun.gw2app.steve.backend.util.dialog.select.selectAccount.SelectAccAccountHolder;
import xhsun.gw2app.steve.backend.util.items.checkbox.CheckBoxHeaderItem;
import xhsun.gw2app.steve.backend.util.vault.OnPreferenceChangeListener;
import xhsun.gw2app.steve.backend.util.vault.VaultType;

/**
 * Dialog for selecting accounts to hide/show
 *
 * @author xhsun
 * @since 2017-05-15
 */

public class SelectAccounts extends DialogFragment {
	private VaultType type;
	private List<SelectAccAccountHolder> accounts;
	private List<CheckBoxHeaderItem> content;
	private OnPreferenceChangeListener<SelectAccAccountHolder> listener;

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

		title.setText(R.string.dialog_select_account_title);
		FlexibleAdapter<CheckBoxHeaderItem> adapter = new FlexibleAdapter<>(content);
		adapter.setAutoScrollOnExpand(true)
				.setNotifyChangeOfUnfilteredItems(true)
				.setAnimationOnScrolling(true)
				.setAnimationOnReverseScrolling(true);

		list.setLayoutManager(new LinearLayoutManager(view.getContext()));
		list.setHasFixedSize(true);
		list.setAdapter(adapter);
		list.setItemAnimator(new DefaultItemAnimator());

		builder.setPositiveButton("OK", (dialog, which) -> {
			SelectAccounts.this.dismiss();
			setPreference();
		})
				.setNegativeButton("Cancel", (dialog, which) -> SelectAccounts.this.dismiss());
		return builder.create();
	}

	/**
	 * list of account that can be selected from
	 *
	 * @param listener for set preference call back
	 */
	public void setAccounts(OnPreferenceChangeListener<SelectAccAccountHolder> listener,
	                        List<AccountData> accounts, VaultType type, Set<String> preference) {
		this.type = type;
		this.listener = listener;
		this.content = new ArrayList<>();
		this.accounts = new ArrayList<>();
		for (AccountData a : accounts) {
			SelectAccAccountHolder holder = new SelectAccAccountHolder(a.getName(), a.getAPI(), !preference.contains(a.getAPI()));
			this.accounts.add(holder);
			this.content.add(new CheckBoxHeaderItem<>(holder));
		}
	}

	//set preference
	private void setPreference() {
		listener.notifyPreferenceChange(type, new HashSet<>(accounts));
	}
}
