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

import me.nithanim.gw2api.v2.api.account.Account;
import xhsun.gw2app.steve.R;
import xhsun.gw2app.steve.database.account.AccountInfo;
import xhsun.gw2app.steve.misc.RequestCode;
import xhsun.gw2app.steve.view.dialog.AddAccountDialog;

/**
 * @author xhsun
 * @since 2017-02-05
 */
public class AccountFragment extends Fragment implements AccountListListener {
	public AccountFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_account, container, false);
		//setup action bar
		Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
		toolbar.setTitle("Accounts");

		//setup recycler view
		RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.account_list);
		recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
		recyclerView.setAdapter(new AccountListAdapter(testingInfo(), this));

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

	//String api, String id, String name, String world, Account.Access access, boolean state
	private List<AccountInfo> testingInfo() {
		ArrayList<AccountInfo> accounts = new ArrayList<>();
		AccountInfo a;
		a = new AccountInfo("api", "id", "example.1234", "[NA] Crystal Desert", Account.Access.GUILD_WARS_2, true);
		accounts.add(a);
		a = new AccountInfo("api1", "id1", "reallylongname.1234", "[NA] Northern Shiverpeaks", Account.Access.HEART_OF_THORNS, false);
		accounts.add(a);
		a = new AccountInfo("api2", "id2", "abc.1234", "[EU] Gandara", Account.Access.PLAY_FOR_FREE, true);
		accounts.add(a);
		return accounts;
	}

	@Override
	public void onClick(AccountInfo account) {
		//TODO depend on state of the account
		//either show account info or show prompt delete dialog
	}

	private void promptAddAccount() {
		//TODO show dialog to get api key
		//then add account to database
		AddAccountDialog add = new AddAccountDialog();
		add.setTargetFragment(this, RequestCode.ACCOUNT);
		add.show(getFragmentManager(), "AddAccount");
	}

	public void onPositiveClick() {

	}
}
