package xhsun.gw2app.steve.view.account;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import xhsun.gw2api.guildwars2.GuildWars2;
import xhsun.gw2app.steve.R;
import xhsun.gw2app.steve.database.account.AccountAPI;
import xhsun.gw2app.steve.database.account.AccountInfo;
import xhsun.gw2app.steve.misc.RequestCode;
import xhsun.gw2app.steve.view.dialog.AddAccountDialog;

/**
 * @author xhsun
 * @since 2017-02-05
 */
public class AccountFragment extends Fragment implements AccountListListener {
	private GuildWars2 wrapper;
	private List<AccountInfo> accounts;
	private AccountListAdapter adapter;

	public AccountFragment() {
		accounts = new ArrayList<>();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_account, container, false);
		//setup action bar
		Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
		toolbar.setTitle("Accounts");

		//setup api wrapper
		wrapper = new GuildWars2();

		//TODO populate list with accounts that is in the database, if there is none, prompt wanna add?

		//setup recycler view
		adapter = new AccountListAdapter(accounts, new AccountAPI(getContext(), wrapper), this);
		RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.account_list);
		recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
		recyclerView.setAdapter(adapter);

		//setup touch helper
		ItemTouchHelper.SimpleCallback callback = new AccountSwipeCallback(recyclerView);
		ItemTouchHelper helper = new ItemTouchHelper(callback);
		helper.attachToRecyclerView(recyclerView);

		//setup item decoration
		recyclerView.addItemDecoration(new AccountItemDecoration());
		recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), LinearLayoutManager.VERTICAL));

		FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.account_fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				promptAddAccount();
			}
		});
		return view;
	}

	@Override
	public void onClick(AccountInfo account) {
		//TODO depend on state of the account
		//either show account info or show prompt delete dialog
	}

	//idsplay add account dialog
	private void promptAddAccount() {
		AddAccountDialog add = new AddAccountDialog();
		add.setTargetFragment(this, RequestCode.ACCOUNT);
		add.setWrapper(wrapper);
		add.show(getFragmentManager(), "AddAccount");
	}

	public void createAccountResult(AccountInfo account) {
		accounts.add(account);
		adapter.notifyItemInserted(accounts.size() - 1);
	}
}
