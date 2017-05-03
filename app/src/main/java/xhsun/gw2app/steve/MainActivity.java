package xhsun.gw2app.steve;

import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;
import xhsun.gw2app.steve.view.fragment.AccountFragment;
import xhsun.gw2app.steve.view.fragment.InventoryFragment;
import xhsun.gw2app.steve.view.fragment.WalletFragment;
import xhsun.gw2app.steve.view.fragment.WikiFragment;
import xhsun.gw2app.steve.view.fragment.storage.StorageFragment;

/**
 * main activity
 * TODO might want to use sync adapter to update data
 * @author xhsun
 * @since 2017-02-03
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
	@BindView(R.id.toolbar)
	Toolbar toolbar;
	@BindView(R.id.drawer_layout)
	DrawerLayout drawer;
	@BindView(R.id.nav_view)
	NavigationView navigation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		ButterKnife.bind(this);
		toolbar.setTitle("");
		setSupportActionBar(toolbar);

		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
				R.string.navigation_drawer_open, R.string.navigation_drawer_close);
		drawer.addDrawerListener(toggle);
		toggle.syncState();

		navigation.setNavigationItemSelectedListener(this);
		setHeaderButtons();
		Timber.i("Initialization complete");
	}

	@Override
	public void onBackPressed() {
		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		if (drawer.isDrawerOpen(GravityCompat.START)) {
			closeDrawer();
		} else {
			super.onBackPressed();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			deleteCache(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onNavigationItemSelected(@NonNull MenuItem item) {
		switch (item.getItemId()) {
			case R.id.nav_bank:
				transferFragment("Storage", new StorageFragment());
				break;
			case R.id.nav_wallet:
				transferFragment("Wallet", new WalletFragment());
				break;
			case R.id.nav_inventory:
				transferFragment("Inventory", new InventoryFragment());
				break;
			default:
		}
		return true;
	}

	//remove all file in cache to save space
	private static void deleteCache(Context context) throws Exception {
		Timber.i("Delete cache file done");
		File dir = context.getCacheDir();
		deleteDir(dir);
	}

	//actual deleting the file
	private static boolean deleteDir(File dir) {
		if (dir != null && dir.isDirectory()) {
			String[] children = dir.list();
			for (String aChildren : children)
				if (!deleteDir(new File(dir, aChildren))) return false;
			return dir.delete();
		} else return dir != null && dir.isFile() && dir.delete();
	}

	//set buttons in the header
	private void setHeaderButtons() {
		navigation.getHeaderView(0).findViewById(R.id.nav_search).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				transferFragment("Wiki", new WikiFragment());
			}
		});
		navigation.getHeaderView(0).findViewById(R.id.nav_account_wrapper).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				transferFragment("Accounts", new AccountFragment());
			}
		});
	}

	//transfer view to given fragment
	private void transferFragment(String name, Fragment fragment) {
		Timber.i("Transfer to %s fragment", name);
		toolbar.setTitle(name);
		closeKeyboard();
//		getSupportFragmentManager().popBackStack();
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.main_fragment, fragment);
		transaction.addToBackStack(name);
		transaction.commit();

		closeDrawer();
	}

	@SuppressWarnings("ConstantConditions")
	//hide any keyboard that is somehow still open
	private void closeKeyboard() {
		Timber.i("Close keyboard on transfer");
		IBinder token;
		if ((token = getCurrentFocus().getWindowToken()) != null) {
			InputMethodManager input = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			input.hideSoftInputFromWindow(token, 0);
		}
	}

	private void closeDrawer() {
		Timber.i("Close drawer");
		drawer.closeDrawer(GravityCompat.START);
	}
}
