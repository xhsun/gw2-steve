package xhsun.gw2app.steve.backend.database.storage;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.util.ArrayList;
import java.util.List;

import me.xhsun.guildwars2wrapper.GuildWars2;
import me.xhsun.guildwars2wrapper.SynchronousRequest;
import me.xhsun.guildwars2wrapper.error.GuildWars2Exception;
import me.xhsun.guildwars2wrapper.model.account.Bank;
import timber.log.Timber;
import xhsun.gw2app.steve.backend.data.AccountData;
import xhsun.gw2app.steve.backend.data.vault.item.BankItemData;
import xhsun.gw2app.steve.backend.data.vault.item.Countable;
import xhsun.gw2app.steve.backend.database.account.AccountWrapper;
import xhsun.gw2app.steve.backend.database.common.ItemWrapper;
import xhsun.gw2app.steve.backend.database.common.SkinWrapper;

/**
 * for manipulate bank item
 *
 * @author xhsun
 * @since 2017-05-04
 */

public class BankWrapper extends StorageWrapper<BankItemData, BankItemData> {
	private SynchronousRequest request;
	private ItemWrapper itemWrapper;
	private SkinWrapper skinWrapper;
	private AccountWrapper accountWrapper;

	public BankWrapper(GuildWars2 wrapper, BankDB bankDB, AccountWrapper accountWrapper,
	                   ItemWrapper itemWrapper, SkinWrapper skinWrapper) {
		super(bankDB);
		request = wrapper.getSynchronous();
		this.accountWrapper = accountWrapper;
		this.itemWrapper = itemWrapper;
		this.skinWrapper = skinWrapper;
	}

	/**
	 * update bank info for given account
	 *
	 * @param api API key
	 * @return updated list of banks for this account
	 * @throws GuildWars2Exception error when interacting with server
	 */
	public List<BankItemData> update(String api) throws GuildWars2Exception {
		Timber.i("Start updating bank info for %s", api);
		try {
			startUpdate(api,
					Stream.of(request.getBank(api)).filterNot(s -> s == null).collect(Collectors.toList()),
					get(api));
		} catch (GuildWars2Exception e) {
			Timber.e(e, "Error occurred when trying to get bank information for %s", api);
			switch (e.getErrorCode()) {
				case Server:
				case Limit:
				case Network:
					throw e;
				case Key://mark account invalid
					accountWrapper.markInvalid(new AccountData(api));
			}
		}

		return get(api);
	}

	private void startUpdate(String api, List<Bank> bank, List<BankItemData> original) {
		List<Countable> known = new ArrayList<>(original);
		List<Countable> seen = new ArrayList<>();
		for (Bank b : bank) {
			if (isCancelled) return;
			if (b.getCount() == 0) continue;//nothing here, move on
			updateRecord(known, seen, new BankItemData(api, b));
		}
		//remove all outdated storage item from database
		for (Countable i : known) {
			if (isCancelled) return;
			delete((BankItemData) i);
		}
	}

	@Override
	protected void updateDatabase(BankItemData info, boolean isItemSeen) {
		if (isCancelled) return;
		//insert item if needed
		if (!isItemSeen && itemWrapper.get(info.getItemData().getId()) == null)
			itemWrapper.update(info.getItemData().getId());
		//insert skin if needed
		if (!isItemSeen && info.getSkinData() != null &&
				info.getSkinData().getId() != 0 && skinWrapper.get(info.getSkinData().getId()) == null)
			skinWrapper.update(info.getSkinData().getId());
		replace(info);//update
	}
}
