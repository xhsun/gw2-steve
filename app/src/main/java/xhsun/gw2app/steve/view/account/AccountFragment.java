package xhsun.gw2app.steve.view.account;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
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

import xhsun.gw2app.steve.R;
import xhsun.gw2app.steve.database.account.AccountAPI;
import xhsun.gw2app.steve.util.constant.RequestCode;
import xhsun.gw2app.steve.util.listener.AccountListListener;
import xhsun.gw2app.steve.util.model.AccountInfo;
import xhsun.gw2app.steve.view.dialog.AccountDetailDialog;
import xhsun.gw2app.steve.view.dialog.AskCreateAccountDialog;

/**
 * AccountFragment is a subclass of {@link Fragment}.
 *   - call by main when user click on the img button in sidebar
 *   - display a list of gw2 account that is currently stored locally
 *   - contain a fab for add new account
 *   -will prompt add account if no account in database
 *
 * @author xhsun
 * @since 2017-02-05
 */
public class AccountFragment extends Fragment implements AccountListListener {
	private AccountAPI api;
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
		toolbar.setTitle("Guild Wars 2 Accounts");

		if (api == null) api = new AccountAPI(getContext());
		accounts = new ArrayList<>();

		//setup recycler view
		RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.account_list);
		recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
		//those are here so that recycleview wouldn't complain
		adapter = new AccountListAdapter(accounts, api, getContext(), this);
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

		new AccountFragmentInitTask(this, recyclerView).execute();

		return view;
	}

	@Override
	public void onClick(AccountInfo account) {
		if (account.isAccessible() && !account.isClosed()) {
			//show account detail
			AccountDetailDialog detail = new AccountDetailDialog();
			detail.accountDetail(account);
			detail.show(getFragmentManager(), "AccountDetailDialog");
		} else {//prompt to remove account
			final AccountInfo info = account;
			AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
			alertDialog.setTitle("Remove Account");
			alertDialog.setMessage("Account is no longer usable due to invalid API key.\n" +
					"Do you want to remove it?");
			alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					int position = accounts.indexOf(info);
					accounts.remove(info);
					adapter.notifyItemRemoved(position);
					api.removeAccount(info);
				}
			});
			alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			alertDialog.show();
		}
	}

	@Override
	public void onDetach() {
		api.close();
		super.onDetach();
	}

	//display add account dialog
	private void promptAddAccount() {
		RequestCode.showCreateAccount(this, getFragmentManager());
	}

	/**
	 * add account to the accounts list
	 * and update view
	 *
	 * @param account account | null if error
	 */
	public void createAccountResult(AccountInfo account) {
		if (account == null) return;//cannot create account
		accounts.add(account);
		adapter.notifyItemInserted(accounts.size() - 1);
	}

	class AccountFragmentInitTask extends AsyncTask<Void, Void, List<AccountInfo>> {
		private Fragment target;
		private RecyclerView view;

		AccountFragmentInitTask(Fragment target, RecyclerView view) {
			this.target = target;
			this.view = view;
		}

		@Override
		protected List<AccountInfo> doInBackground(Void... params) {
			return api.getAll(null);
		}

		@Override
		protected void onPostExecute(List<AccountInfo> result) {
			//if the account list is empty, prompt user for register account
			if (result.isEmpty()) {
				AskCreateAccountDialog ask = new AskCreateAccountDialog();
				ask.setTarget(target);
				ask.show(getFragmentManager(), "AskCreateAccountDialog");
				return;
			}
			accounts = result;
			adapter = new AccountListAdapter(accounts, api, getContext(), (AccountListListener) target);
			view.setAdapter(adapter);
		}
	}
}
