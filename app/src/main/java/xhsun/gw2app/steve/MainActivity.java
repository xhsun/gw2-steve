package xhsun.gw2app.steve;

import android.os.Bundle;
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
import android.widget.Button;

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
//	private GuildWars2Api api;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
//		api = new GuildWars2Api(new GuildWars2ApiDefaultConfigWithGodaddyFix());
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

	//init open account fragment button in the nav header
	private void initAccountButton(NavigationView navigation) {
		Button account_btn = (Button) navigation.getHeaderView(0).findViewById(R.id.nav_account);
		account_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
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
