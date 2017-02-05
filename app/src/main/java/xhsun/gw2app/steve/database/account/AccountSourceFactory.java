package xhsun.gw2app.steve.database.account;

import android.content.Context;

/**
 * Class will give appropriate account database class base on build version
 *
 * @author xhsun
 * @since 2017-02-05
 */

public class AccountSourceFactory {
	public static AccountDB getAccountDB(Context context) {
		if (android.os.Build.VERSION.SDK_INT >= 19) {
			return new AccountSource(context);
		} else {
			return new AccountSourceLegacy(context);
		}
	}
}
