package xhsun.gw2app.steve.view.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
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
import xhsun.gw2app.steve.backend.data.AccountInfo;
import xhsun.gw2app.steve.backend.util.dialog.select.selectCharacter.AccountHolder;
import xhsun.gw2app.steve.backend.util.dialog.select.selectCharacter.CharacterHolder;
import xhsun.gw2app.steve.backend.util.items.checkbox.CheckBoxHeaderItem;
import xhsun.gw2app.steve.backend.util.items.checkbox.CheckBoxItem;
import xhsun.gw2app.steve.backend.util.items.checkbox.OnCheckBoxExpanded;
import xhsun.gw2app.steve.backend.util.vault.OnPreferenceChangeListener;
import xhsun.gw2app.steve.backend.util.vault.VaultType;

/**
 * Select character dialog
 *
 * @author xhsun
 * @since 2017-04-03
 */

public class SelectCharacters extends DialogFragment implements OnCheckBoxExpanded {
	private List<AccountHolder> accounts;
	private List<CheckBoxHeaderItem> content;
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

		FlexibleAdapter<CheckBoxHeaderItem> adapter = new FlexibleAdapter<>(content);
		adapter.setAutoScrollOnExpand(true)
				.setNotifyChangeOfUnfilteredItems(true)
				.setAnimationOnScrolling(true)
				.setAnimationOnReverseScrolling(true);

		list.setLayoutManager(new LinearLayoutManager(view.getContext()));
		list.setHasFixedSize(true);
		list.setAdapter(adapter);
		list.setItemAnimator(new DefaultItemAnimator());

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
		this.content = new ArrayList<>();
		for (AccountInfo a : accounts) {
			if (a.getAllCharacterNames().size() > 0) {
				List<CheckBoxItem> subitems = new ArrayList<>();
				Set<String> pref = preference.get(a);

				AccountHolder holder = new AccountHolder(a, pref);
				CheckBoxHeaderItem<AccountHolder> header = new CheckBoxHeaderItem<>(holder, this, subitems);
				this.accounts.add(holder);
				this.content.add(header);

				for (String name : a.getAllCharacterNames()) {
					CharacterHolder c = new CharacterHolder(name, pref);
					holder.getCharacters().add(c);
					subitems.add(new CheckBoxItem<>(header, header, c));
				}
			}
		}
	}

	@Override
	public void notifyExpanded(boolean isExpanded) {
		//manually expanding view size
		list.postDelayed(new Runnable() {
			@Override
			public void run() {
				RelativeLayout.LayoutParams lp =
						new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
				lp.topMargin = title.getHeight();
				list.setLayoutParams(lp);
			}
		}, 370);
	}

	//set preference
	private void setPreference() {
		listener.notifyPreferenceChange(VaultType.INVENTORY, new HashSet<>(accounts));
	}
}
