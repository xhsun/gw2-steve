package xhsun.gw2app.steve.backend.util.inventory;

import android.content.SharedPreferences;

import java.util.List;

import xhsun.gw2app.steve.backend.database.account.AccountInfo;
import xhsun.gw2app.steve.backend.database.character.CharacterWrapper;
import xhsun.gw2app.steve.backend.database.character.StorageWrapper;
import xhsun.gw2app.steve.backend.util.storage.StorageTask;

/**
 * Created by hannah on 01/04/17.
 */

public interface WrapperProvider {
	CharacterWrapper getCharacterWrapper();

	StorageWrapper getStorageWrapper();

	SharedPreferences getPreferences();

	List<StorageTask> getUpdates();

	void onLoad(AccountInfo account);
}
