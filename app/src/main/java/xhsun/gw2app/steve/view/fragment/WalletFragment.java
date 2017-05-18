package xhsun.gw2app.steve.view.fragment;


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
import android.widget.ProgressBar;

import com.annimon.stream.Stream;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;
import xhsun.gw2app.steve.MainApplication;
import xhsun.gw2app.steve.R;
import xhsun.gw2app.steve.backend.data.AccountInfo;
import xhsun.gw2app.steve.backend.database.wallet.WalletWrapper;
import xhsun.gw2app.steve.backend.util.CancellableAsyncTask;
import xhsun.gw2app.steve.backend.util.dialog.AddAccountListener;
import xhsun.gw2app.steve.backend.util.wallet.CurrencyListAdapter;
import xhsun.gw2app.steve.backend.util.wallet.FragmentInfoProvider;
import xhsun.gw2app.steve.backend.util.wallet.GetWalletInfo;
import xhsun.gw2app.steve.backend.util.wallet.UpdateWalletInfo;

/**
 * WalletFragment is a subclass of {@link Fragment}<br/>
 * TODO rework get wallet info and update wallet info
 * @author xhsun
 * @since 2017-03-26
 */
public class WalletFragment extends Fragment implements AddAccountListener, FragmentInfoProvider {
	private Set<CancellableAsyncTask> tasks;
	private CurrencyListAdapter adapter;
	@BindView(R.id.wallet_list)
	RecyclerView list;
	@BindView(R.id.wallet_refresh)
	SwipeRefreshLayout refresh;
	@BindView(R.id.wallet_progress)
	ProgressBar progressBar;
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

		tasks = new HashSet<>();
		adapter = new CurrencyListAdapter();

		list.setLayoutManager(new LinearLayoutManager(view.getContext()));
		list.addItemDecoration(new DividerItemDecoration(list.getContext(), LinearLayoutManager.VERTICAL));
		list.setAdapter(adapter);

		refresh.setOnRefreshListener(this::onListRefresh);

		new GetWalletInfo(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		Timber.i("Initialization complete");
		return view;
	}

	@Override
	public void onPause() {
		super.onPause();
		Timber.i("Paused Wallet fragment");
		cancelTasks();
	}

	//the only instance this will callback is when there is no account
	//better start refreshing again
	@Override
	public void addAccountCallback(AccountInfo account) {
		onListRefresh();
	}

	@Override
	public void showContent(boolean hideEverything) {
		refresh.setRefreshing(false);
		if (!hideEverything) return;
		refresh.setVisibility(View.VISIBLE);
		progressBar.setVisibility(View.GONE);
	}

	@Override
	public void hideContent(boolean hideEverything) {
		if (!hideEverything) {
			refresh.post(() -> refresh.setRefreshing(true));
			return;
		}
		refresh.setVisibility(View.GONE);
		progressBar.setVisibility(View.VISIBLE);
	}

	@Override
	public CurrencyListAdapter getAdapter() {
		return adapter;
	}

	@Override
	public Set<CancellableAsyncTask> getTasks() {
		return tasks;
	}

	@Override
	public void displayError() {
		AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
		alertDialog.setTitle("Server Unavailable");
		alertDialog.setMessage("Unable to update wallet information");
		alertDialog.setButton(android.app.AlertDialog.BUTTON_NEUTRAL, "OK",
				(dialog, which) -> dialog.dismiss());
		alertDialog.show();
	}

	//start refreshing wallet info
	private void onListRefresh() {
		cancelTasks();
		new UpdateWalletInfo(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	private void cancelTasks() {
		Stream.of(tasks).filter(t -> t.getStatus() != AsyncTask.Status.FINISHED)
				.forEach(e -> {
					e.cancel(true);
					e.setCancelled();
				});
	}
}
