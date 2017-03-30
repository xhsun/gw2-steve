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
	public WalletWrapper(WalletDB wallet, CurrencyDB currency, GuildWars2 wrapper, AccountWrapper account) {
		this.wrapper = wrapper;
		this.currency = currency;
		this.wallet = wallet;
		this.account = account;
	}

	/**
	 * get all wallet info
	 *
	 * @return list of all wallet info | empty if there is nothing
	 */
	public List<CurrencyInfo> getAll() {
		List<CurrencyInfo> currencies = currency.getAll();
		List<CurrencyInfo> result = new ArrayList<>();
		for (CurrencyInfo info : currencies) {
			List<WalletInfo> wallets = wallet.getAllByCurrency(info.getId());
			if (wallets.size() == 0) {
				//this currency don't have any value, delete it
				currency.delete(info.getId());
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
				try {
					List<Wallet> items = wrapper.getWallet(a.getAPI());
					List<WalletInfo> existed = wallet.getAllByAPI(a.getAPI());
					//update all wallet info
					for (Wallet i : items) {
						if (wallet.replace(i.getId(), a.getAPI(), a.getName(), i.getValue()) == 787) {
							addNewCurrency(i, a);
							existed.remove(new WalletInfo(i.getId(), a.getAPI()));
						}
					}
					removeOutdated(existed);//remove all outdated wallet info
				} catch (GuildWars2Exception e) {
					Timber.e(e, "GW2 error when trying to update wallet info");
					switch (e.getErrorCode()) {
						case Key://key is no longer valid, mark it as invalid
							account.markInvalid(a);
							removeOutdated(wallet.getAllByAPI(a.getAPI()));
							break;
						case Server:
						case Limit:
							throw e;
					}
				}
			}

		return getAll();
	}

	private void removeOutdated(List<WalletInfo> outdated) {
		for (WalletInfo e : outdated) wallet.delete(e.getCurrencyID(), e.getApi());
	}

	//add new currency and insert wallet info
	private void addNewCurrency(Wallet wallet, AccountInfo account) throws GuildWars2Exception {
		List<Currency> currencies = wrapper.getCurrencyInfo(new long[]{wallet.getId()});
		if (currencies.size() == 0) return;
		Currency c = currencies.get(0);
		currency.replace(c.getId(), c.getName(), c.getIcon());
		this.wallet.replace(wallet.getId(), account.getAPI(), account.getName(), wallet.getValue());
	}
}
