package xhsun.gw2app.steve.backend.util.storage;

import android.support.v7.widget.RecyclerView;

import java.util.List;

import xhsun.gw2app.steve.backend.database.account.AccountInfo;

/**
 * Created by hannah on 27/04/17.
 */

public interface QueryStorageInfoProvider {
	List<AccountInfo> provideAccounts();

	RecyclerView provideParentView();

	boolean isBank();
}
