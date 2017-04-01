package xhsun.gw2app.steve.backend.database.wallet;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;
import xhsun.gw2api.guildwars2.GuildWars2;
import xhsun.gw2api.guildwars2.err.GuildWars2Exception;
import xhsun.gw2api.guildwars2.model.Currency;
import xhsun.gw2api.guildwars2.model.account.Wallet;
import xhsun.gw2app.steve.backend.database.account.AccountInfo;
import xhsun.gw2app.steve.backend.database.account.AccountWrapper;
import xhsun.gw2app.steve.backend.database.common.CurrencyInfo;
import xhsun.gw2app.steve.backend.database.common.CurrencyWrapper;

/**
 * For manipulate wallet
 *
 * @author xhsun
 * @since 2017-03-27
 */

public class WalletWrapper {
	private GuildWars2 wrapper;
	private AccountWrapper account;
	private CurrencyWrapper currencyWrapper;
	private WalletDB walletDB;
	private boolean isCancelled = false;

	@Inject
	public WalletWrapper(WalletDB wallet, CurrencyWrapper currency, GuildWars2 wrapper, AccountWrapper account) {
		this.wrapper = wrapper;
		this.currencyWrapper = currency;
		this.walletDB = wallet;
		this.account = account;
	}

	/**
	 * get all wallet info
	 *
	 * @return list of all wallet info | empty if there is nothing
	 */
	public List<CurrencyInfo> getAll() {
		List<CurrencyInfo> currencies = currencyWrapper.getAll();
		List<CurrencyInfo> result = new ArrayList<>();
		for (CurrencyInfo info : currencies) {
			if (isCancelled) break;//TODO remove once not deleting currency
			List<WalletInfo> wallets = walletDB.getAllByCurrency(info.getId());
			if (wallets.size() == 0) {//TODO remove this once introduce TP, might accidentally remove coin
				//this currency don't have any value, delete it
				currencyWrapper.delete(info.getId());
				continue;
			}
			result.add(info);
			for (WalletInfo w : wallets) w.setIcon(info.getIcon());
			info.setTotal(wallets);
		}
		return result;
	}

	/**
	 * Update wallet info base on account info<br/>
	 * If there are nothing, add new wallet info
	 *
	 * @return list of all wallet info | empty if there is nothing | null if there is no account
	 * @throws GuildWars2Exception error when interacting with server
	 */
	public List<CurrencyInfo> update() throws GuildWars2Exception {
		List<AccountInfo> accounts = account.getAll(true);
		if (accounts.size() == 0) return null;

			for (AccountInfo a : accounts) {
				if (isCancelled) break;
				try {
					List<Wallet> items = wrapper.getWallet(a.getAPI());
					List<WalletInfo> existed = walletDB.getAllByAPI(a.getAPI());
					//update all wallet info
					for (Wallet i : items) {
						if (isCancelled) break;
						if (currencyWrapper.get(i.getId()) == null) addNewCurrency(i);
						addOrReplace(existed, i, a);
					}
					if (!isCancelled) removeOutdated(existed);//remove all outdated wallet info
					else break;
				} catch (GuildWars2Exception e) {
					Timber.e(e, "GW2 error when trying to update wallet info");
					switch (e.getErrorCode()) {
						case Key://key is no longer valid, mark it as invalid
							account.markInvalid(a);
							removeOutdated(walletDB.getAllByAPI(a.getAPI()));
							break;
						case Server:
						case Network:
						case Limit:
							throw e;
					}
				}
			}

		return getAll();
	}

	public void setCancelled(boolean cancelled) {
		isCancelled = cancelled;
	}

	private void removeOutdated(List<WalletInfo> outdated) {
		for (WalletInfo e : outdated) walletDB.delete(e.getCurrencyID(), e.getApi());
	}

	private void addOrReplace(List<WalletInfo> existed, Wallet wallet, AccountInfo account) {
		if (walletDB.replace(wallet.getId(), account.getAPI(), account.getName(), wallet.getValue()) == 0)
			existed.remove(new WalletInfo(wallet.getId(), account.getAPI()));
	}

	//add new currency and insert wallet info
	private void addNewCurrency(Wallet wallet) throws GuildWars2Exception {
		if (isCancelled) return;
		List<Currency> currencies = wrapper.getCurrencyInfo(new long[]{wallet.getId()});
		if (currencies.size() == 0) return;
		Currency c = currencies.get(0);
		currencyWrapper.replace(c.getId(), c.getName(), c.getIcon());
	}
}
