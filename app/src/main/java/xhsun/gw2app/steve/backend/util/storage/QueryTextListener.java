package xhsun.gw2app.steve.backend.util.storage;

import android.support.v7.widget.SearchView;

import timber.log.Timber;

/**
 * Capture user input and send it to {@link StorageSearchListener} to do filter/restore
 *
 * @author xhsun
 * @since 2017-04-17
 */

public class QueryTextListener implements SearchView.OnQueryTextListener {
	private StorageSearchListener provider;

	public QueryTextListener(StorageSearchListener provider) {
		this.provider = provider;
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		return false;
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		Timber.i("New query text: %s", newText);
		if (newText.trim().equals("")) {
			provider.restore();
			return false;
		}
		provider.filter(newText.trim().toLowerCase());
//		//if no query given, restore view back to what it was
//		if (newText.trim().equals("")) {
//			if (provider.isBank()) restoreBank();
//			else restoreInventory();
//			return false;
//		}
//		//start filter
//		String query = newText.trim().toLowerCase();
//		if (provider.isBank()) filterBank(query);
//		else filterInventory(query);
//		//show first
////		provider.provideParentView().scrollToPosition(0);
		return false;
	}

//	//restore content of inventory
//	private void restoreInventory() {
//		Timber.i("Search view start restore inventories");
//		for (final AccountInfo a : provider.provideAccounts()) {
//			if (a.getChild() == null) continue;//not in display
//			//restore all that should be displaying
//			for (String name : a.getAllCharacterNames()) {
//				//don't bother if this char shouldn't be showing
//				CharacterInfo temp = new CharacterInfo(name);
//				if (!a.getAllCharacters().contains(temp)) continue;
//				//restore this char
//				final CharacterInfo info = a.getAllCharacters().get(a.getAllCharacters().indexOf(temp));
//				if (info.getChild() != null) {
//					info.getChild().post(new Runnable() {
//						@Override
//						public void run() {
//							((StorageGridAdapter) info.getChild().getAdapter()).setData(info.getInventory());
//						}
//					});
//				} else {
//					a.getChild().post(new Runnable() {
//						@Override
//						public void run() {
//							((CharacterListAdapter) a.getChild().getAdapter())
//									.addDataWithoutLoad(a.getAllCharacterNames().indexOf(info.getName()), info);
//						}
//					});
//				}
//
//			}
//		}
//	}
//
//	//TODO either make inventory not a three level deep nest or change how the search works
//	//TODO probably should only change how the search works, ie similar to show/hide inventory
//	//hide any that doesn't match the query
//	private void filterInventory(String query) {
//		Timber.i("Search view start filter inventories");
//		for (final AccountInfo a : provider.provideAccounts()) {
//			//skip any that is not showing
//			if (a.getAllCharacterNames().size() == 0 || a.getChild() == null) continue;
//			//filter each char
//			final CharacterListAdapter adapter = ((CharacterListAdapter) a.getChild().getAdapter());
//			for (String name : a.getAllCharacterNames()) {
//				//get info for char, if not find, continue to next one
//				CharacterInfo temp = new CharacterInfo(name);
//				if (!a.getAllCharacters().contains(temp)) continue;
//
//				final CharacterInfo c = a.getAllCharacters().get(a.getAllCharacters().indexOf(temp));
//				if (c.getInventory().size() == 0) continue; //nothing for this char
//
//				//find any that match
//				final List<StorageInfo> filtered = filter(query, c.getInventory());
//				if (filtered.size() == 0) {
//					Timber.i("No match find for %s, remove it from view", name);
//					//no match, remove child from display, if it is displaying
//					if (adapter.containData(c)) {
//						a.getChild().post(new Runnable() {
//							@Override
//							public void run() {
//								adapter.notifyItemRemoved(adapter.removeDataWithoutModify(c));
//							}
//						});
//					}
//				} else {//find match, only show match item
//					Timber.i("%d match find for %s (%s)", filtered.size(), name, c.getChild() != null);
////					if (adapter.containData(c) && c.getChild()!=null) {
//					if (c.getChild() != null) {
//						((StorageGridAdapter) c.getChild().getAdapter()).setData(filtered);
////						c.getChild().post(new Runnable() {
////							@Override
////							public void run() {
////								Timber.i("start display filtered list for %s", c.getName());
////								((StorageGridAdapter) c.getChild().getAdapter()).keepProvided(filtered);
////							}
////						});
//					} else {
//						temp = c;
//						temp.setFiltered(filtered);
//						final CharacterInfo newChar = temp;
//						//if char not showing, show it first
//						a.getChild().post(new Runnable() {
//							@Override
//							public void run() {
//								adapter.addDataWithoutLoad(a.getAllCharacters().indexOf(newChar), newChar);
//							}
//						});
//					}
//
//				}
//			}
//		}
//	}
//
//
}
