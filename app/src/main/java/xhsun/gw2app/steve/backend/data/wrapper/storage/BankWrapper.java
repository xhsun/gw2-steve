package xhsun.gw2app.steve.backend.data.wrapper.storage;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import me.xhsun.guildwars2wrapper.GuildWars2;
import me.xhsun.guildwars2wrapper.SynchronousRequest;
import me.xhsun.guildwars2wrapper.error.GuildWars2Exception;
import timber.log.Timber;
import xhsun.gw2app.steve.backend.data.model.AccountModel;
import xhsun.gw2app.steve.backend.data.model.ItemModel;
import xhsun.gw2app.steve.backend.data.model.SkinModel;
import xhsun.gw2app.steve.backend.data.model.vault.item.BankItemModel;
import xhsun.gw2app.steve.backend.data.wrapper.account.AccountWrapper;
import xhsun.gw2app.steve.backend.data.wrapper.common.ItemWrapper;
import xhsun.gw2app.steve.backend.data.wrapper.common.SkinWrapper;

/**
 * for manipulate bank item
 *
 * @author xhsun
 * @since 2017-05-04
 */

public class BankWrapper extends StorageWrapper<BankItemModel, BankItemModel> {
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
	public List<BankItemModel> update(String api) throws GuildWars2Exception {
		Timber.i("Start updating bank info for %s", api);
		try {
			List<BankItemModel> original = get(api), bank = new ArrayList<>();
			Set<BankItemModel> seen = new HashSet<>();
			Stream.of(request.getBank(api)).withoutNulls().filterNot(i -> i.getCount() < 1)
					.forEach(b -> {
						BankItemModel current = new BankItemModel(api, b);
						if (!seen.contains(current)) {
							bank.add(current);
							seen.add(current);
						} else {
							BankItemModel old = bank.get(bank.indexOf(current));
							old.setCount(old.getCount() + current.getCount());
						}
					});

			if (original.size() < 1) startInsert(bank);
			else startUpdate(original, bank);
		} catch (GuildWars2Exception e) {
			Timber.e(e, "Error occurred when trying to get bank information for %s", api);
			switch (e.getErrorCode()) {
				case Server:
				case Limit:
				case Network:
					throw e;
				case Key://mark account invalid
					accountWrapper.markInvalid(new AccountModel(api));
			}
		}

		return get(api);
	}

	@Override
	protected void checkBaseItem(List<BankItemModel> data) {
		List<Integer> oItem = Stream.of(itemWrapper.getAll()).map(ItemModel::getId).collect(Collectors.toList()),
				oSkin = Stream.of(skinWrapper.getAll()).map(SkinModel::getId).collect(Collectors.toList());

		itemWrapper.bulkInsert(Stream.of(data).filterNot(i -> oItem.contains(i.getItemModel().getId()))
				.map(i -> i.getItemModel().getId()).mapToInt(Integer::intValue).toArray());

		skinWrapper.bulkInsert(Stream.of(data).filter(i -> i.getSkinModel() != null)
				.filterNot(i -> oSkin.contains(i.getSkinModel().getId()))
				.map(i -> i.getSkinModel().getId()).mapToInt(Integer::intValue).toArray());
	}

	@Override
	protected void checkOriginal(BankItemModel old, BankItemModel current) {
		updateIfDifferent(old, current);
	}
}
