package xhsun.gw2app.steve.backend.data.wrapper.wallet;

import android.support.annotation.NonNull;

import com.annimon.stream.Stream;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import me.xhsun.guildwars2wrapper.GuildWars2;
import me.xhsun.guildwars2wrapper.SynchronousRequest;
import me.xhsun.guildwars2wrapper.error.GuildWars2Exception;
import me.xhsun.guildwars2wrapper.model.v2.Currency;
import me.xhsun.guildwars2wrapper.model.v2.account.Wallet;
import timber.log.Timber;
import xhsun.gw2app.steve.backend.data.model.AccountModel;
import xhsun.gw2app.steve.backend.data.model.CurrencyModel;
import xhsun.gw2app.steve.backend.data.model.WalletModel;
import xhsun.gw2app.steve.backend.data.wrapper.account.AccountWrapper;
import xhsun.gw2app.steve.backend.data.wrapper.common.CurrencyWrapper;

/**
 * For manipulate wallet
 *
 * @author xhsun
 * @since 2017-03-27
 */

public class WalletWrapper {
	private SynchronousRequest request;
	private AccountWrapper accountWrapper;
	private CurrencyWrapper currencyWrapper;
	private WalletDB walletDB;
	private boolean isCancelled = false;

	@Inject
	public WalletWrapper(WalletDB wallet, CurrencyWrapper currency, GuildWars2 wrapper, AccountWrapper account) {
		request = wrapper.getSynchronous();
		this.currencyWrapper = currency;
		this.walletDB = wallet;
		this.accountWrapper = account;
	}

	/**
	 * get all wallet info
	 *
	 * @return list of all wallet info | empty if there is nothing
	 */
	public List<CurrencyModel> getAll() {
		List<CurrencyModel> currencies = currencyWrapper.getAll();
		List<CurrencyModel> result = new ArrayList<>();
		for (CurrencyModel info : currencies) {
			if (isCancelled) break;
			List<WalletModel> wallets = walletDB.getAllByCurrency(info.getId());
			if (wallets.size() == 0) continue;
			result.add(info);
			Stream.of(wallets).forEach(w -> w.setIcon(info.getIcon()));
			info.setTotal(wallets);
		}
		return result;
	}

	public boolean isWalletCached(@NonNull AccountModel account) {
		return walletDB.getAllByAPI(account.getAPI()).size() > 0;
	}

	/**
	 * update wallet info for given account
	 * @param account account info
	 * @return true on success | false on server error | null on invalid account
	 */
	public Boolean update(@NonNull AccountModel account) {
		List<CurrencyModel> currencies = currencyWrapper.getAll();
		List<WalletModel> existed = walletDB.getAllByAPI(account.getAPI());
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
		List<AccountModel> accounts = accountWrapper.getAll(true);
		if (accounts.size() == 0) return null;
		List<CurrencyModel> currencies = currencyWrapper.getAll();
		List<WalletModel> existed = walletDB.getAll();

		for (AccountModel a : accounts) {
			if (isCancelled) break;
			if ((result = __update(a, existed, currencies, false)) == null) result = false;
		}
		//remove all that is no long applicable
		if (result) removeOutdated(existed);
		return result;
	}

	private Boolean __update(@NonNull AccountModel account, List<WalletModel> existed,
	                         List<CurrencyModel> currencies, boolean removeInvalid) {
		try {
			List<Wallet> items = request.getWallet(account.getAPI());
			for (Wallet i : items) {
				int index;
				WalletModel wallet;
				if (isCancelled) break;
				//check if database contain this currency
				if (!currencies.contains(new CurrencyModel(i.getCurrencyId()))) addNewCurrency(i);

				//check if database contain currency info for this accountWrapper
				if ((index = existed.indexOf(new WalletModel(i.getCurrencyId(), account.getAPI()))) >= 0) {
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

	private void removeOutdated(List<WalletModel> outdated) {
		for (WalletModel e : outdated) {
			if (isCancelled) break;
			walletDB.delete(e.getCurrencyID(), e.getApi());
		}
	}

	private boolean add(Wallet wallet, AccountModel account) {
		return walletDB.replace(wallet.getCurrencyId(), account.getAPI(), account.getName(), wallet.getValue()) == 0;
	}

	//add new currency and insert wallet info
	private CurrencyModel addNewCurrency(Wallet wallet) throws GuildWars2Exception {
		if (isCancelled) return null;
		List<Currency> currencies = request.getCurrencyInfo(new int[]{wallet.getCurrencyId()});
		if (currencies.size() == 0) return null;
		Currency c = currencies.get(0);
		currencyWrapper.replace(c.getId(), c.getName(), c.getIcon());

		CurrencyModel info = new CurrencyModel(c.getId());
		info.setName(c.getName());
		info.setIcon(c.getIcon());
		return info;
	}
}
