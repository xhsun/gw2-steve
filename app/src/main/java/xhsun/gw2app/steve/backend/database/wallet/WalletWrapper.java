package xhsun.gw2app.steve.backend.database.wallet;

import android.support.annotation.NonNull;

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
	private AccountWrapper accountWrapper;
	private CurrencyWrapper currencyWrapper;
	private WalletDB walletDB;
	private boolean isCancelled = false;

	@Inject
	public WalletWrapper(WalletDB wallet, CurrencyWrapper currency, GuildWars2 wrapper, AccountWrapper account) {
		this.wrapper = wrapper;
		this.currencyWrapper = currency;
		this.walletDB = wallet;
		this.accountWrapper = account;
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
			if (isCancelled) break;
			List<WalletInfo> wallets = walletDB.getAllByCurrency(info.getId());
			if (wallets.size() == 0) continue;
			result.add(info);
			for (WalletInfo w : wallets) w.setIcon(info.getIcon());
			info.setTotal(wallets);
		}
		return result;
	}

	public boolean isWalletCached(@NonNull AccountInfo account) {
		return walletDB.getAllByAPI(account.getAPI()).size() > 0;
	}

	/**
	 * update wallet info for given account
	 * @param account account info
	 * @return true on success | false on server error | null on invalid account
	 */
	public Boolean update(@NonNull AccountInfo account) {
		List<CurrencyInfo> currencies = currencyWrapper.getAll();
		List<WalletInfo> existed = walletDB.getAllByAPI(account.getAPI());
		return __update(account, existed, currencies, true);
	}

	/**
	 * Update wallet info for all accounts<br/>
	 * If there are nothing, add new wallet info
	 *
	 * @return true on success | false on server error | null on invalid account
	 */
	public Boolean update() {
		Boolean result = true;
		List<AccountInfo> accounts = accountWrapper.getAll(true);
		if (accounts.size() == 0) return null;
		List<CurrencyInfo> currencies = currencyWrapper.getAll();
		List<WalletInfo> existed = walletDB.getAll();

		for (AccountInfo a : accounts) {
			if (isCancelled) break;
			if ((result = __update(a, existed, currencies, false)) == null) result = false;
		}
		//remove all that is no long applicable
		if (result) removeOutdated(existed);
		return result;
	}

	private Boolean __update(@NonNull AccountInfo account, List<WalletInfo> existed,
	                         List<CurrencyInfo> currencies, boolean removeInvalid) {
		try {
			List<Wallet> items = wrapper.getWallet(account.getAPI());
			for (Wallet i : items) {
				int index;
				WalletInfo wallet;
				if (isCancelled) break;
				//check if database contain this currency
				if (!currencies.contains(new CurrencyInfo(i.getId()))) addNewCurrency(i);

				//check if database contain currency info for this accountWrapper
				if ((index = existed.indexOf(new WalletInfo(i.getId(), account.getAPI()))) >= 0) {
					if ((wallet = existed.get(index)).getValue() != i.getValue())
						add(i, account);
					existed.remove(wallet);
				} else add(i, account);
			}
		} catch (GuildWars2Exception e) {
			Timber.e(e, "GW2 error when trying to update wallet info");
			switch (e.getErrorCode()) {
				case Key://key is no longer valid, mark it as invalid
					accountWrapper.markInvalid(account);
					if (removeInvalid) removeOutdated(existed);
					return null;
				case Server:
				case Network:
				case Limit:
					//error, use cached info
					return false;
			}
		}
		return true;
	}

	public void setCancelled(boolean cancelled) {
		isCancelled = cancelled;
	}

	private void removeOutdated(List<WalletInfo> outdated) {
		for (WalletInfo e : outdated) {
			if (isCancelled) break;
			walletDB.delete(e.getCurrencyID(), e.getApi());
		}
	}

	private boolean add(Wallet wallet, AccountInfo account) {
		return walletDB.replace(wallet.getId(), account.getAPI(), account.getName(), wallet.getValue()) == 0;
	}

	//add new currency and insert wallet info
	private CurrencyInfo addNewCurrency(Wallet wallet) throws GuildWars2Exception {
		if (isCancelled) return null;
		List<Currency> currencies = wrapper.getCurrencyInfo(new long[]{wallet.getId()});
		if (currencies.size() == 0) return null;
		Currency c = currencies.get(0);
		currencyWrapper.replace(c.getId(), c.getName(), c.getIcon());

		CurrencyInfo info = new CurrencyInfo(c.getId());
		info.setName(c.getName());
		info.setIcon(c.getIcon());
		return info;
	}
}
