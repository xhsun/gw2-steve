package xhsun.gw2app.steve.util.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import xhsun.gw2app.steve.MainActivity;

/**
 * Splash screen
 * This will switch to main activity once the app finished loading
 *
 * @author xhsun
 * @since 2017-02-03
 */

public class SplashActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		Intent intent=new Intent(this, MainActivity.class);
		startActivity(intent);
		finish();
	}

}
