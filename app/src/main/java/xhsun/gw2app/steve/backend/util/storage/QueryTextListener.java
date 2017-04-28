package xhsun.gw2app.steve.backend.util.storage;

import android.support.v7.widget.SearchView;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;
import xhsun.gw2app.steve.backend.database.account.AccountInfo;
import xhsun.gw2app.steve.backend.database.character.CharacterInfo;
import xhsun.gw2app.steve.backend.database.character.StorageInfo;
import xhsun.gw2app.steve.backend.util.inventory.CharacterListAdapter;

/**
 * Created by hannah on 27/04/17.
 */

public class QueryTextListener implements SearchView.OnQueryTextListener {
	private QueryStorageInfoProvider provider;

	public QueryTextListener(QueryStorageInfoProvider provider) {
		this.provider = provider;
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		return false;
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		Timber.i("New query text: %s", newText);
		//if no query given, restore view back to what it was
		if (newText.trim().equals("")) {
			if (provider.isBank()) restoreBank();
			else restoreInventory();
			return false;
		}
		//start filter
		String query = newText.trim().toLowerCase();
		if (provider.isBank()) filterBank(query);
		else filterInventory(query);
		//show first
//		provider.provideParentView().scrollToPosition(0);
		return false;
	}

	//restore content of inventory
	private void restoreInventory() {
		Timber.i("Search view start restore inventories");
		for (final AccountInfo a : provider.provideAccounts()) {
			if (a.getChild() == null) continue;//not in display
			//restore all that should be displaying
			for (String name : a.getAllCharacterNames()) {
				//don't bother if this char shouldn't be showing
				CharacterInfo temp = new CharacterInfo(name);
				if (!a.getAllCharacters().contains(temp)) continue;
				//restore this char
				final CharacterInfo info = a.getAllCharacters().get(a.getAllCharacters().indexOf(temp));
				if (info.getChild() != null) {
					info.getChild().post(new Runnable() {
						@Override
						public void run() {
							((StorageGridAdapter) info.getChild().getAdapter()).keepProvided(info.getInventory());
						}
					});
				} else {
					a.getChild().post(new Runnable() {
						@Override
						public void run() {
							((CharacterListAdapter) a.getChild().getAdapter())
									.addDataWithoutLoad(a.getAllCharacterNames().indexOf(info.getName()), info);
						}
					});
				}

			}
		}
	}

	//hide any that doesn't match the query
	private void filterInventory(String query) {
		Timber.i("Search view start filter inventories");
		for (final AccountInfo a : provider.provideAccounts()) {
			//skip any that is not showing
			if (a.getAllCharacterNames().size() == 0 || a.getChild() == null) continue;
			//filter each char
			final CharacterListAdapter adapter = ((CharacterListAdapter) a.getChild().getAdapter());
			for (String name : a.getAllCharacterNames()) {
				//get info for char, if not find, continue to next one
				CharacterInfo temp = new CharacterInfo(name);
				if (!a.getAllCharacters().contains(temp)) continue;

				final CharacterInfo c = a.getAllCharacters().get(a.getAllCharacters().indexOf(temp));
				if (c.getInventory().size() == 0) continue; //nothing for this char

				//find any that match
				final List<StorageInfo> filtered = filter(query, c.getInventory());
				if (filtered.size() == 0) {
					Timber.i("No match find for %s, remove it from view", name);
					//no match, remove child from display, if it is displaying
					if (adapter.containData(c)) {
						a.getChild().post(new Runnable() {
							@Override
							public void run() {
								adapter.notifyItemRemoved(adapter.removeDataWithoutModify(c));
							}
						});
					}
				} else {//find match, only show match item
					Timber.i("%d match find for %s (%s)", filtered.size(), name, c.getChild() != null);
//					if (adapter.containData(c) && c.getChild()!=null) {
					if (c.getChild() != null) {
						((StorageGridAdapter) c.getChild().getAdapter()).keepProvided(filtered);
//						c.getChild().post(new Runnable() {
//							@Override
//							public void run() {
//								Timber.i("start display filtered list for %s", c.getName());
//								((StorageGridAdapter) c.getChild().getAdapter()).keepProvided(filtered);
//							}
//						});
					} else {
						temp = c;
						temp.setFiltered(filtered);
						final CharacterInfo newChar = temp;
						//if char not showing, show it first
						a.getChild().post(new Runnable() {
							@Override
							public void run() {
								adapter.addDataWithoutLoad(a.getAllCharacters().indexOf(newChar), newChar);
							}
						});
					}

				}
			}
		}
	}

	private void restoreBank() {
		for (AccountInfo a : provider.provideAccounts()) {
			if (a.getBank().size() == 0) continue;//skip any that is not showing
			//TODO restore any back to display
		}
	}

	private void filterBank(String query) {
		for (AccountInfo a : provider.provideAccounts()) {
			if (a.getBank().size() == 0) continue;//skip any that is not showing
			List<StorageInfo> filtered = filter(query, a.getBank());
			if (filtered.size() == 0) {//no match, remove child from display
				//TODO the actual stuff
			} else {//find match, only show match item
				//TODO the actual stuff
			}
		}
	}

	//find all that match the query in the given items
	private List<StorageInfo> filter(String query, List<StorageInfo> items) {
		List<StorageInfo> filtered = new ArrayList<>();
		for (StorageInfo i : items) {
			String name = i.getItemInfo().getName().toLowerCase();
			//TODO String skinName
			if (name.contains(query)) filtered.add(i);
		}
		return filtered;
	}
}
