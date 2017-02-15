package xhsun.gw2app.steve.view.storage.bank;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import xhsun.gw2api.guildwars2.GuildWars2;
import xhsun.gw2api.guildwars2.model.account.Bank;
import xhsun.gw2api.guildwars2.util.GuildWars2Exception;
import xhsun.gw2app.steve.R;
import xhsun.gw2app.steve.database.account.AccountAPI;
import xhsun.gw2app.steve.util.constant.RequestCode;
import xhsun.gw2app.steve.util.listener.EndlessRecyclerViewScrollListener;
import xhsun.gw2app.steve.util.model.AccountInfo;
import xhsun.gw2app.steve.util.model.InventoryItem;
import xhsun.gw2app.steve.view.storage.StorageGridAdapter;


/**
 * @author xhsun
 * @since 2017-02-13
 */
public class BankFragment extends Fragment {
	private static final int SIZE = 64;
	private static final int TAB_SIZE = 30;
	private List<AccountInfo> accounts;
	private List<Bank> bank;
	private List<InventoryItem> storages;

	public BankFragment() {
		accounts = new ArrayList<>();
		bank = new ArrayList<>();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_storage, container, false);

		storages = new ArrayList<>();//TODO empty for now
//		storages.removeAll(Collections.singleton(null)); to remove null item in the list

		//TODO asynctask to get bank, on pre show load and hide grid&fab, on post exe add on scroll listener and show
		Context context = view.getContext();
		RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.storage_list);
		GridLayoutManager manager = new GridLayoutManager(context, calculateColumns());
		recyclerView.setLayoutManager(manager);
		recyclerView.setAdapter(new StorageGridAdapter(storages, getContext()));
		recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(manager) {
			@Override
			public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
				//TODO getIteminfo(int[page] of ids) and construct inventoryItem class
				//need a way to get thing from list that we haven't got before
				//pop as we get x number of item?
			}
		});
		//TODO fab: if there is no account, when click prompt add account; else, list of account to choose (default: all chosen)
		return view;
	}

	private int calculateColumns() {
		DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
		float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
		return (int) (dpWidth / SIZE);
	}

	private AccountInfo findFirstAccessible(List<AccountInfo> accounts) {
		for (AccountInfo account : accounts) {
			if (account.isAccessible() && !account.isSearched()) return account;
		}
		return null;
	}

	private void showMoreItem(int size) {
		if (bank.isEmpty()) return;
		//TODO get size amount of item info
	}

	class BankFragmentInitTask extends AsyncTask<Void, Void, List<Bank>> {
		private AccountAPI accountAPI;
		private BankFragment fragment;
		private CoordinatorLayout layout;
		private ProgressBar progress;

		BankFragmentInitTask(AccountAPI api, BankFragment fragment, CoordinatorLayout layout, ProgressBar progress) {
			accountAPI = api;
			this.fragment = fragment;
			this.layout = layout;
			this.progress = progress;
		}

		@Override
		public void onPreExecute() {
			layout.setVisibility(View.GONE);
			progress.setVisibility(View.VISIBLE);
		}

		@Override
		protected List<Bank> doInBackground(Void... params) {
			AccountInfo account;
			GuildWars2 api = GuildWars2.getInstance();
			accounts.addAll(accountAPI.getAll(null));
			if (accounts.isEmpty()) return null;
			if ((account = findFirstAccessible(accounts)) == null) return null;
			try {
				List<Bank> bank = api.getBank(account.getAPI());
				account.setSearched(true);
				return bank;
			} catch (GuildWars2Exception | IOException e) {
				return new ArrayList<>();
			}
		}

		@SuppressWarnings("SuspiciousMethodCalls")
		@Override
		public void onPostExecute(List<Bank> result) {
			progress.setVisibility(View.GONE);
			layout.setVisibility(View.VISIBLE);
			if (result == null) {
				RequestCode.showCreateAccount(fragment, getFragmentManager());
				return;
			}
			if (result.isEmpty()) {
				//TODO show error: something's wrong when trying to connect to server, please try again
				return;
			}
			result.removeAll(Collections.singleton(null));
			bank = result;
			showMoreItem(TAB_SIZE);
		}
	}
}
