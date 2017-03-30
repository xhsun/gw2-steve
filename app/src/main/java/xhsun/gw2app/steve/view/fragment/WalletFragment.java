package xhsun.gw2app.steve.view.fragment;


import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;
import xhsun.gw2api.guildwars2.err.GuildWars2Exception;
import xhsun.gw2app.steve.MainApplication;
import xhsun.gw2app.steve.R;
import xhsun.gw2app.steve.backend.database.account.AccountInfo;
import xhsun.gw2app.steve.backend.database.wallet.CurrencyInfo;
import xhsun.gw2app.steve.backend.database.wallet.WalletWrapper;
import xhsun.gw2app.steve.backend.util.AddAccountListener;
import xhsun.gw2app.steve.backend.util.AsyncTaskResult;
import xhsun.gw2app.steve.backend.util.dialog.DialogManager;
import xhsun.gw2app.steve.backend.util.wallet.ListAdapter;

/**
 * WalletFragment is a subclass of {@link Fragment}<br/>
 *
 * @author xhsun
 * @since 2017-03-26
 */
public class WalletFragment extends Fragment implements AddAccountListener {
	private RetrieveWalletInfo retrieveTask = null;
	private RefreshWalletInfo refreshTask = null;
	private ListAdapter adapter;
	@BindView(R.id.wallet_list)
	RecyclerView list;
	@BindView(R.id.wallet_refresh)
	SwipeRefreshLayout refresh;
	@Inject
	WalletWrapper walletWrapper;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		((MainApplication) getActivity().getApplication()).getServiceComponent().inject(this);//injection
		View view = inflater.inflate(R.layout.fragment_wallet, container, false);
		ButterKnife.bind(this, view);

		Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
		toolbar.setTitle("Wallet");

		adapter = new ListAdapter(getContext(), new ArrayList<CurrencyInfo>());

		list.setLayoutManager(new LinearLayoutManager(view.getContext()));
		list.addItemDecoration(new DividerItemDecoration(list.getContext(), LinearLayoutManager.VERTICAL));
		list.setAdapter(adapter);

		refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				onListRefresh();
			}
		});

		retrieveTask = new RetrieveWalletInfo();
		retrieveTask.execute();

		Timber.i("Initialization complete");
		return view;
	}

	@Override
	public void onPause() {
		super.onPause();
		if (retrieveTask != null && retrieveTask.getStatus() != AsyncTask.Status.FINISHED)
			retrieveTask.cancel(true);
		if (refreshTask != null && refreshTask.getStatus() != AsyncTask.Status.FINISHED)
			refreshTask.cancel(true);
	}

	//the only instance this will callback is when there is no account
	//better start refreshing again
	@Override
	public void addAccountCallback(AccountInfo account) {
		startRefresh();
	}

	//start refreshing wallet info
	private void onListRefresh() {
		refreshTask = new RefreshWalletInfo(this);
		refreshTask.execute();
	}

	//start refresh by code
	private void startRefresh() {
		refresh.post(new Runnable() {
			@Override
			public void run() {
				refresh.setRefreshing(true);
				onListRefresh();
			}
		});
	}

	//get all wallet information from database, maybe outdated (but fast)
	private class RetrieveWalletInfo extends AsyncTask<Void, Void, List<CurrencyInfo>> {

		@Override
		protected void onCancelled() {
			Timber.i("Retrieve wallet info task cancelled");
			showContent();
		}

		@Override
		protected List<CurrencyInfo> doInBackground(Void... params) {
			Timber.i("Start retrieve wallet info");
			return walletWrapper.getAll();
		}

		@Override
		protected void onPostExecute(List<CurrencyInfo> result) {
			if (result != null && result.size() > 0) {
				Timber.i("Start displaying wallet information");
				adapter.getParentList().addAll(result);
				adapter.notifyParentRangeInserted(0, result.size());
			}

			retrieveTask = null;
			showContent();
			startRefresh();
		}

		//hide progress bar and show content
		private void showContent() {
			refresh.setVisibility(View.VISIBLE);
		}
	}

	//refresh wallet information
	private class RefreshWalletInfo extends AsyncTask<Void, Void, AsyncTaskResult<List<CurrencyInfo>>> {
		private WalletFragment target;

		private RefreshWalletInfo(WalletFragment target) {
			this.target = target;
		}

		@Override
		protected void onCancelled() {
			Timber.i("Refresh wallet info cancelled");
			refresh.setRefreshing(false);
		}

		@Override
		protected AsyncTaskResult<List<CurrencyInfo>> doInBackground(Void... params) {
			Timber.i("Start refresh wallet info");
			try {
				return new AsyncTaskResult<>(walletWrapper.update());
			} catch (GuildWars2Exception e) {
				return new AsyncTaskResult<>(e);
			}
		}

		@Override
		protected void onPostExecute(AsyncTaskResult<List<CurrencyInfo>> result) {
			if (isCancelled()) return;//task cancelled, abort
			if (result.getError() != null) {
				switch (((GuildWars2Exception) result.getError()).getErrorCode()) {
					case Server:
					case Limit:
						displayError();
				}
			} else {
				if (result.getData() == null) {
					Timber.i("No accounts in record, prompt add account");
					new DialogManager(getFragmentManager()).promptAdd(target);
				} else {
					Timber.i("Start displaying wallet information");
					adapter.setParentList(result.getData(), true);
					adapter.notifyParentRangeChanged(0, result.getData().size());
				}
			}

			refreshTask = null;
			refresh.setRefreshing(false);
		}
	}

	private void displayError() {
		AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
		alertDialog.setTitle("Server Unavailable");
		alertDialog.setMessage("Unable to update wallet information");
		alertDialog.setButton(android.app.AlertDialog.BUTTON_NEUTRAL, "OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		alertDialog.show();
	}
}
