package xhsun.gw2app.steve.backend.util.storage;

import java.util.Comparator;

import xhsun.gw2app.steve.backend.database.character.StorageInfo;

/**
 * Created by hannah on 27/04/17.
 */

public class ItemNameComparator implements Comparator<StorageInfo> {
	@Override
	public int compare(StorageInfo o1, StorageInfo o2) {
		return o1.getItemInfo().getName().compareTo(o2.getItemInfo().getName());
	}
}
