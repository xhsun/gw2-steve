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
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import xhsun.gw2app.steve.R;
import xhsun.gw2app.steve.backend.data.model.AccountModel;
import xhsun.gw2app.steve.backend.data.model.dialog.SelectCharAccountModel;
import xhsun.gw2app.steve.backend.data.model.dialog.SelectCharCharacterModel;
import xhsun.gw2app.steve.backend.util.items.checkbox.CheckBoxHeaderItem;
import xhsun.gw2app.steve.backend.util.items.checkbox.CheckBoxItem;
import xhsun.gw2app.steve.backend.util.items.checkbox.OnCheckBoxExpanded;
import xhsun.gw2app.steve.backend.util.support.vault.VaultType;
import xhsun.gw2app.steve.backend.util.support.vault.preference.OnPreferenceChangeListener;

/**
 * Select character dialog
 *
 * @author xhsun
 * @since 2017-04-03
 */

public class SelectCharacters extends DialogFragment implements OnCheckBoxExpanded {
	private List<SelectCharAccountModel> accounts;
	private List<CheckBoxHeaderItem> content;
	private OnPreferenceChangeListener<SelectCharAccountModel> listener;
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
			SelectCharacters.this.dismiss();
			setPreference();
		})
				.setNegativeButton("Cancel", (dialog, which) -> SelectCharacters.this.dismiss());

		return builder.create();
	}

	/**
	 * list of account that can be selected from
	 *
	 * @param listener for set preference call back
	 */
	public void setAccounts(OnPreferenceChangeListener<SelectCharAccountModel> listener, List<AccountModel> accounts,
	                        Map<AccountModel, Set<String>> preference) {
		this.listener = listener;
		this.accounts = new ArrayList<>();
		this.content = new ArrayList<>();
		for (AccountModel a : accounts) {
			if (a.getAllCharacterNames().size() > 0) {
				List<CheckBoxItem> subitems = new ArrayList<>();
				Set<String> pref = preference.get(a);

				SelectCharAccountModel holder = new SelectCharAccountModel(a, pref);
				CheckBoxHeaderItem<SelectCharAccountModel> header = new CheckBoxHeaderItem<>(holder, this, subitems);
				this.accounts.add(holder);
				this.content.add(header);

				for (String name : a.getAllCharacterNames()) {
					SelectCharCharacterModel c = new SelectCharCharacterModel(name, pref);
					holder.getCharacters().add(c);
					subitems.add(new CheckBoxItem<>(header, header, c));
				}
			}
		}
	}

	@Override
	public void notifyExpanded(boolean isExpanded) {
		//manually expanding view size
		list.postDelayed(() -> {
			RelativeLayout.LayoutParams lp =
					new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
			lp.topMargin = title.getHeight();
			list.setLayoutParams(lp);
		}, 370);
	}

	//set preference
	private void setPreference() {
		listener.notifyPreferenceChange(VaultType.INVENTORY, new HashSet<>(accounts));
	}
}
