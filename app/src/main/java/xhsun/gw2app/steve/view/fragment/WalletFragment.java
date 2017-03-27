package xhsun.gw2app.steve.view.fragment;


import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;
import xhsun.gw2api.guildwars2.GuildWars2;
import xhsun.gw2api.guildwars2.model.Currency;
import xhsun.gw2api.guildwars2.model.account.Wallet;
import xhsun.gw2api.guildwars2.util.GuildWars2Exception;
import xhsun.gw2app.steve.MainApplication;
import xhsun.gw2app.steve.R;
import xhsun.gw2app.steve.backend.database.account.AccountInfo;
import xhsun.gw2app.steve.backend.database.account.AccountWrapper;
import xhsun.gw2app.steve.backend.util.wallet.IndividualWallet;
import xhsun.gw2app.steve.backend.util.wallet.ListAdapter;
import xhsun.gw2app.steve.backend.util.wallet.TotalWallet;

/**
 * WalletFragment is a subclass of {@link Fragment}<br/>
 *
 * @author xhsun
 * @since 2017-03-26
 */
public class WalletFragment extends Fragment {
	private RetrieveWalletInfo task = null;
	@BindView(R.id.wallet_list)
	RecyclerView list;
	@BindView(R.id.wallet_progress)
	RelativeLayout progress;
	@Inject
	AccountWrapper accountWrapper;
	@Inject
	GuildWars2 gw2Wrapper;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		((MainApplication) getActivity().getApplication()).getServiceComponent().inject(this);//injection
		View view = inflater.inflate(R.layout.fragment_wallet, container, false);
		ButterKnife.bind(this, view);

		Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
		toolbar.setTitle("Wallet");

		list.setLayoutManager(new LinearLayoutManager(view.getContext()));
		list.addItemDecoration(new DividerItemDecoration(list.getContext(), LinearLayoutManager.VERTICAL));

		task = new RetrieveWalletInfo();
		task.execute();
		Timber.i("Initialization complete");
		return view;
	}

	@Override
	public void onPause() {
		super.onPause();
		if (task != null && task.getStatus() != AsyncTask.Status.FINISHED)
			task.cancel(true);
	}

	private class RetrieveWalletInfo extends AsyncTask<Void, Void, List<TotalWallet>> {

		@Override
		protected void onCancelled() {
			Timber.i("Retrieve wallet info task cancelled");
			showContent();
		}

		@Override
		protected List<TotalWallet> doInBackground(Void... params) {
			Timber.i("Start retrieve wallet info");
			@SuppressLint("UseSparseArrays") HashMap<Long, TotalWallet> result = new HashMap<>();
			//get all active account
			List<AccountInfo> accounts = accountWrapper.getAll(true);
			Timber.i("Done retrieve all active accounts");
			for (AccountInfo account : accounts) {
				try {
					List<Wallet> items = gw2Wrapper.getWallet(account.getAPI());
					for (Wallet item : items) {
						TotalWallet current = result.get(item.getId());
						if (current == null) {
							current = new TotalWallet(item.getId());
							result.put(item.getId(), current);
							//update value
							current.setValue(item.getValue());
						} else {
							current.setValue(current.getValue() + item.getValue());
						}

						//add self to the list of individuals
						IndividualWallet self = new IndividualWallet(item.getId());
						self.setAccount(account.getName());
						self.setValue(item.getValue());
						current.getChildList().add(self);
					}
				} catch (GuildWars2Exception e) {
					Timber.e(e, "Encountered error when trying to get wallet info");
					switch (e.getErrorCode()) {
						case Server:
						case Limit:
							displayError();
							return null;
					}
				}
			}
			Timber.i("Done populate list of total wallet with basic information");

			Long[] keys = result.keySet().toArray(new Long[result.size()]);
			long[] ids = new long[keys.length];
			for (int i = 0; i < keys.length; i++) ids[i] = keys[i];

			try {
				List<Currency> currencies = gw2Wrapper.getCurrencyInfo(ids);
				for (Currency currency : currencies) {
					TotalWallet current = result.get(currency.getId());
					if (current == null) continue;
					current.setName(currency.getName());
					current.setIcon(currency.getIcon());
				}
			} catch (GuildWars2Exception e) {
				Timber.e(e, "Encountered error when trying to get currency info");
				displayError();
				return null;
			}
			Timber.i("Done give name and icon for each wallet item");
			List<TotalWallet> wallets = new ArrayList<>(result.values());
			Collections.sort(wallets);
			return wallets;
		}

		@Override
		protected void onPostExecute(List<TotalWallet> result) {
			if (result == null) {
				ended();
				return;
			}

			Timber.i("Start displaying wallet information");
			ListAdapter adapter = new ListAdapter(getContext(), result);
			list.setAdapter(adapter);
			ended();
		}

		private void ended() {
			task = null;
			showContent();
		}

		//hide progress bar and show content
		private void showContent() {
			progress.setVisibility(View.GONE);
			list.setVisibility(View.VISIBLE);
		}

		private void displayError() {
			AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
			alertDialog.setTitle("Server Unavailable");
			alertDialog.setMessage("Unable to retrieve wallet information");
			alertDialog.setButton(android.app.AlertDialog.BUTTON_NEUTRAL, "OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
			alertDialog.show();
		}
	}
}
