package xhsun.gw2app.steve.backend.util.storage;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * pager adapter for storage tab
 *
 * @author xhsun
 * @since 2017-05-03
 */

public class StoragePagerAdapter extends FragmentPagerAdapter {
	private String titles[] = new String[]{"Bank", "Material", "Wardrobe"};
	private List<StorageTabFragment> fragments;

	public StoragePagerAdapter(FragmentManager fm, List<StorageTabFragment> fragments) {
		super(fm);
		this.fragments = fragments;
	}

	@Override
	public Fragment getItem(int position) {
		return fragments.get(position);
	}

	@Override
	public CharSequence getPageTitle(int position) {
		// Generate title based on item position
		return titles[position];
	}

	@Override
	public int getCount() {
		return fragments.size();
	}
}
