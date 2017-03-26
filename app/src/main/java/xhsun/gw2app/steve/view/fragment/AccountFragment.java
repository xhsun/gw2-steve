package xhsun.gw2app.steve.view.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;
import xhsun.gw2app.steve.MainApplication;
import xhsun.gw2app.steve.R;
import xhsun.gw2app.steve.backend.database.account.AccountInfo;
import xhsun.gw2app.steve.backend.database.account.AccountWrapper;
import xhsun.gw2app.steve.backend.util.account.CustomItemDecoration;
import xhsun.gw2app.steve.backend.util.account.ListAdapter;
import xhsun.gw2app.steve.backend.util.account.ListOnClickListener;
import xhsun.gw2app.steve.backend.util.account.SwipeCallback;
import xhsun.gw2app.steve.backend.util.dialog.DialogManager;

/**
 * AccountFragment is a subclass of {@link Fragment}<br/>
 * &#32;- call by main when user click on the img button in sidebar<br/>
 * &#32;- display a list of gw2 account that is currently stored locally<br/>
 * &#32;- contain a fab for add new account<br/>
 * &#32;-will prompt add account if no account in database
 *
 * @author xhsun
 * @since 2017-02-05
 */
public class AccountFragment extends Fragment implements ListOnClickListener {
	@BindView(R.id.account_list)
	RecyclerView list;
	@BindView(R.id.account_fab)
	FloatingActionButton fab;
	@BindView(R.id.account_progress)
	RelativeLayout progress;
	@BindView(R.id.account_refresh)
	SwipeRefreshLayout refresh;
	@Inject
	AccountWrapper wrapper;
	private DialogManager dialogManager;
	private ListAdapter adapter;
	private RetrieveAccountInfo retrieveTask = null;
	private RefreshAccounts refreshTask = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		((MainApplication) getActivity().getApplication()).getServiceComponent().inject(this);//injection
		adapter = new ListAdapter(this, new ArrayList<AccountInfo>(), wrapper);

		View view = inflater.inflate(R.layout.fragment_account, container, false);
		ButterKnife.bind(this, view);

		Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
		toolbar.setTitle("Accounts");

		list.setLayoutManager(new LinearLayoutManager(view.getContext()));
		list.setAdapter(adapter);//to make recycler view shut up

		//setup touch helper (swipe to delete)
		ItemTouchHelper.SimpleCallback callback = new SwipeCallback(list);
		ItemTouchHelper helper = new ItemTouchHelper(callback);
		helper.attachToRecyclerView(list);

		//setup item decoration (red when remove and divider)
		list.addItemDecoration(new CustomItemDecoration());
		list.addItemDecoration(new DividerItemDecoration(list.getContext(), LinearLayoutManager.VERTICAL));

		//init fab
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startAddAccount();
			}
		});

		refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				onListRefresh();
			}
		});

		retrieveTask = new RetrieveAccountInfo(this);
		retrieveTask.execute();

		dialogManager = new DialogManager(getFragmentManager());
		Timber.i("Initialization complete");
		return view;
	}

	@Override
	public void onListItemClick(AccountInfo account) {
		//if account is still valid, show detail
		if (account.isValid() && !account.isClosed()) dialogManager.ShowAccount(account);
			//prompt remove account
		else promptRemove(account);
	}

	/**
	 * refresh account list
	 */
	public void onListRefresh() {
		refreshTask = new RefreshAccounts();
		refreshTask.execute();
	}

	/**
	 * add account to the accounts list
	 * and update view
	 *
	 * @param account account | null if nothing changed
	 */
	public void addAccountCallback(AccountInfo account) {
		Timber.i("New account (%s) added, display detail", account.getAPI());
		adapter.addData(account);
		adapter.notifyItemInserted(adapter.getItemCount() - 1);
	}

	@Override
	public void onPause() {
		super.onPause();
		if (retrieveTask != null && retrieveTask.getStatus() != AsyncTask.Status.FINISHED)
			retrieveTask.cancel(true);

		if (refreshTask != null && refreshTask.getStatus() != AsyncTask.Status.FINISHED)
			refreshTask.cancel(true);
	}

	//dialog to prompt remove account
	private void promptRemove(final AccountInfo account) {
		AlertDialog remove = new AlertDialog.Builder(getContext()).create();
		remove.setTitle("Invalid API Key");
		remove.setMessage("Do you want to remove it?");
		remove.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				adapter.removeData(account);
				dialog.dismiss();
			}
		});
		remove.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		remove.show();
	}

	//start add account process by show add account dialog
	private void startAddAccount() {
		dialogManager.addAccount(DialogManager.ACCOUNT, this);
	}

	//get all account information that is currently in the database
	private class RetrieveAccountInfo extends AsyncTask<Void, Void, List<AccountInfo>> {
		private Fragment target;

		RetrieveAccountInfo(Fragment fragment) {
			target = fragment;
		}

		@Override
		protected List<AccountInfo> doInBackground(Void... params) {
			Timber.i("Start retrieve all account info");
			return wrapper.getAll(null);
		}

		@Override
		protected void onCancelled() {
			Timber.i("Retrieve account task cancelled");
			showContent();
		}

		@Override
		protected void onPostExecute(List<AccountInfo> result) {
			if (isCancelled()) return;//retrieveTask cancelled, abort
			//if the account list is empty, prompt user for register account
			if (result.isEmpty()) {
				Timber.i("No accounts in record, prompt add account");
				dialogManager.promptAdd(DialogManager.ACCOUNT, target);
			} else {
				Timber.i("display all accounts");
				adapter.setData(result);
				adapter.notifyDataSetChanged();
			}
			retrieveTask = null;

			showContent();
		}

		//hide progress bar and show content
		private void showContent() {
			progress.setVisibility(View.GONE);
			fab.setVisibility(View.VISIBLE);
			list.setVisibility(View.VISIBLE);
		}
	}

	//refresh all accounts
	private class RefreshAccounts extends AsyncTask<Void, Void, List<AccountInfo>> {
		@Override
		protected List<AccountInfo> doInBackground(Void... params) {
			Timber.i("Start refresh accounts");
			wrapper.updateAccounts();//update accounts
			return wrapper.getAll(null);//get all accounts
		}

		@Override
		protected void onCancelled() {
			Timber.i("Refresh accounts cancelled");
			refresh.setRefreshing(false);
		}

		@Override
		protected void onPostExecute(List<AccountInfo> result) {
			if (isCancelled()) return;

			Timber.i("Update account list");
			//update list
			adapter.setData(result);
			adapter.notifyDataSetChanged();
			refresh.setRefreshing(false);

			refreshTask = null;
		}
	}
}