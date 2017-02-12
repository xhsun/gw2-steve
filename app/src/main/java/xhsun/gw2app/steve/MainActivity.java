package xhsun.gw2app.steve;

import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import java.io.File;

import xhsun.gw2app.steve.view.account.AccountFragment;
import xhsun.gw2app.steve.view.wiki.WikiFragment;

/**
 * main activity
 *
 * @author xhsun
 * @since 2017-02-03
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
	private Toolbar toolbar;
	private DrawerLayout drawer;
	private FragmentManager manager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		manager = getSupportFragmentManager();

		toolbar = (Toolbar) findViewById(R.id.toolbar);
		toolbar.setTitle("");
		setSupportActionBar(toolbar);

		initNavigation();
	}

	@Override
	public void onBackPressed() {
		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		if (drawer.isDrawerOpen(GravityCompat.START)) {
			drawer.closeDrawer(GravityCompat.START);
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public boolean onNavigationItemSelected(@NonNull MenuItem item) {
		FragmentTransaction transaction;
		switch (item.getItemId()) {
			default:
		}

		closeDrawer();
		return true;
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

	public static void deleteCache(Context context) throws Exception {
		File dir = context.getCacheDir();
		deleteDir(dir);
	}

	public static boolean deleteDir(File dir) {
		if (dir != null && dir.isDirectory()) {
			String[] children = dir.list();
			for (String aChildren : children)
				if (!deleteDir(new File(dir, aChildren))) return false;
			return dir.delete();
		} else return dir != null && dir.isFile() && dir.delete();
	}

	private void initNavigation() {
		//init drawer
		drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
		drawer.addDrawerListener(toggle);
		toggle.syncState();

		//init navigation
		NavigationView navigation = (NavigationView) findViewById(R.id.nav_view);
		navigation.setNavigationItemSelectedListener(this);

		//init header buttons
		initSearchButton(navigation);
		initAccountButton(navigation);
	}

	//init search wiki button in the nav header
	private void initSearchButton(NavigationView navigation) {
		Button search_btn = (Button) navigation.getHeaderView(0).findViewById(R.id.nav_search);
		search_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				FragmentTransaction transaction = manager.beginTransaction();
				transaction.replace(R.id.main_fragment, new WikiFragment());
				transaction.addToBackStack("wiki");
				transaction.commit();
				closeDrawer();
			}
		});
	}

	@SuppressWarnings("Null")
	//init open account fragment button in the nav header
	private void initAccountButton(NavigationView navigation) {
		Button account_btn = (Button) navigation.getHeaderView(0).findViewById(R.id.nav_account);
		account_btn.setOnClickListener(new View.OnClickListener() {
			@SuppressWarnings("ConstantConditions")
			@Override
			public void onClick(View v) {
				//hide any keyboard that is somehow still open
				IBinder token;
				if ((token = getCurrentFocus().getWindowToken()) != null) {
					InputMethodManager input = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					input.hideSoftInputFromWindow(token, 0);
				}
				FragmentTransaction transaction = manager.beginTransaction();
				transaction.replace(R.id.main_fragment, new AccountFragment());
				transaction.addToBackStack("account");
				transaction.commit();
				closeDrawer();
			}
		});
	}

	//close drawer
	private void closeDrawer() {
		drawer.closeDrawer(GravityCompat.START);
	}
}
