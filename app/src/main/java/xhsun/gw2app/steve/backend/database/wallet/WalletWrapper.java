package xhsun.gw2app.steve.backend.database.wallet;

import android.content.Context;

import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;
import xhsun.gw2api.guildwars2.GuildWars2;
import xhsun.gw2api.guildwars2.model.Currency;
import xhsun.gw2api.guildwars2.model.account.Wallet;
import xhsun.gw2api.guildwars2.util.GuildWars2Exception;
import xhsun.gw2app.steve.backend.database.account.AccountInfo;
import xhsun.gw2app.steve.backend.database.account.AccountWrapper;

/**
 * For manipulate wallet
 *
 * @author xhsun
 * @since 2017-03-27
 */

public class WalletWrapper {
	private GuildWars2 wrapper;
	private AccountWrapper account;
	private CurrencyDB currency;
	private WalletDB wallet;

	@Inject
	public WalletWrapper(Context context, GuildWars2 wrapper, AccountWrapper account) {
		this.wrapper = wrapper;
		currency = new CurrencyDB(context);
		wallet = new WalletDB(context);
		this.account = account;
	}

	/**
	 * get all wallet info
	 *
	 * @return list of all wallet info | empty if there is nothing
	 */
	public List<CurrencyInfo> getAll() {
		List<CurrencyInfo> result = currency.getAll();
		for (CurrencyInfo info : result) {
			List<WalletInfo> wallets = wallet.getAllByCurrency(info.getId());
			if (wallets.size() == 0) {
				//this currency don't have any value, delete it
				result.remove(info);
				currency.delete(info.getId());
				continue;
			}
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
		try {
			for (AccountInfo a : accounts) {
				List<Wallet> items = wrapper.getWallet(a.getAPI());
				List<WalletInfo> existed = wallet.getAllByAPI(a.getAPI());
				//update all wallet info
				for (Wallet i : items) {
					if (!wallet.replace(i.getId(), a.getAPI(), a.getName(), i.getValue()))
						addNewCurrency(i, a);//add new currency and try again
					existed.remove(new WalletInfo(i.getId(), a.getAPI()));
				}

				//remove all outdated wallet info
				for (WalletInfo e : existed) wallet.delete(e.getCurrencyID(), e.getApi());
			}
		} catch (GuildWars2Exception e) {
			Timber.e(e, "GW2 error when trying to update wallet info");
			switch (e.getErrorCode()) {
				case Server:
				case Limit:
					throw e;
			}
		}

		return getAll();
	}


	//add new currency and insert wallet info
	private boolean addNewCurrency(Wallet wallet, AccountInfo account) throws GuildWars2Exception {
		List<Currency> currencies = wrapper.getCurrencyInfo(new long[]{wallet.getId()});
		if (currencies.size() == 0) return false;
		Currency c = currencies.get(0);
		currency.replace(c.getId(), c.getName(), c.getIcon());
		return this.wallet.replace(wallet.getId(), account.getAPI(), account.getName(), wallet.getValue());
	}
}
