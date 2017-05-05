package xhsun.gw2app.steve.backend.database.storage;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;
import xhsun.gw2api.guildwars2.GuildWars2;
import xhsun.gw2api.guildwars2.err.GuildWars2Exception;
import xhsun.gw2api.guildwars2.model.account.Bank;
import xhsun.gw2app.steve.backend.database.account.AccountInfo;
import xhsun.gw2app.steve.backend.database.account.AccountWrapper;
import xhsun.gw2app.steve.backend.database.common.ItemWrapper;
import xhsun.gw2app.steve.backend.database.common.SkinWrapper;
import xhsun.gw2app.steve.backend.util.items.StorageType;

/**
 * for manipulate bank item
 *
 * @author xhsun
 * @since 2017-05-04
 */

public class BankWrapper extends StorageWrapper {
	private GuildWars2 wrapper;
	private BankDB bankDB;
	private AccountWrapper accountWrapper;

	public BankWrapper(GuildWars2 wrapper, BankDB bankDB, AccountWrapper accountWrapper,
	                   ItemWrapper itemWrapper, SkinWrapper skinWrapper) {
		super(itemWrapper, skinWrapper, bankDB, StorageType.BANK);
		this.wrapper = wrapper;
		this.bankDB = bankDB;
		this.accountWrapper = accountWrapper;
	}

	/**
	 * update bank info for given account
	 *
	 * @param api API key
	 * @return updated list of banks for this account
	 * @throws GuildWars2Exception error when interacting with server
	 */
	public List<StorageInfo> update(String api) throws GuildWars2Exception {
		Timber.i("Start updating bank info for %s", api);
		try {
			_update(wrapper.getBank(api), api);
		} catch (GuildWars2Exception e) {
			Timber.e(e, "Error occurred when trying to get bank information for %s", api);
			switch (e.getErrorCode()) {
				case Server:
				case Limit:
				case Network:
					throw e;
				case Key://mark account invalid
					accountWrapper.markInvalid(new AccountInfo(api));
			}
		}

		return get(api);
	}

	//update or add item to bank
	private void _update(List<Bank> bank, String api) {
		List<StorageInfo> known = get(api);
		List<StorageInfo> seen = new ArrayList<>();
		for (Bank b : bank) {
			if (isCancelled) return;
			if (b == null) continue;//nothing here, move on
			updateStorage(known, seen, new StorageInfo(b, api));
		}

		//remove all outdated storage item from database
		for (StorageInfo i : known) {
			if (isCancelled) return;
			bankDB.delete(i.getId());
		}
	}
}
