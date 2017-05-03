package xhsun.gw2app.steve.backend.util.storage;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import xhsun.gw2app.steve.view.fragment.storage.BankFragment;

/**
 * pager adapter for storage tab
 *
 * @author xhsun
 * @since 2017-05-03
 */

public class StoragePagerAdapter extends FragmentPagerAdapter {
	private String titles[] = new String[]{"Bank", "Material", "Wardrobe"};
	private List<Fragment> fragments;

	public StoragePagerAdapter(FragmentManager fm) {
		super(fm);
		fragments = new ArrayList<>();
		fragments.add(new BankFragment());
		//TODO add all fragments to list
		fragments.add(new BankFragment());
		fragments.add(new BankFragment());
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
