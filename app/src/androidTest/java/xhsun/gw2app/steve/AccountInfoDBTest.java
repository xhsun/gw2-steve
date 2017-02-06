package xhsun.gw2app.steve;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import me.nithanim.gw2api.v2.GuildWars2Api;
import me.nithanim.gw2api.v2.configs.GuildWars2ApiDefaultConfigWithGodaddyFix;
import xhsun.gw2app.steve.database.account.AccountAPI;
import xhsun.gw2app.steve.database.account.AccountInfo;

import static org.junit.Assert.assertEquals;

/**
 * Created by hannah on 05/02/17.
 */
@RunWith(AndroidJUnit4.class)
public class AccountInfoDBTest {
	private AccountAPI database;

	@Before
	public void setup() {
		database = new AccountAPI(InstrumentationRegistry.getTargetContext(), new GuildWars2Api(new GuildWars2ApiDefaultConfigWithGodaddyFix()));
	}

	@After
	public void teardown() {
		database.close();
		database = null;
	}

//	@Test
//	public void assert_create(){
//		//String api, String id, String usr, String name, String world, String access
//		assertEquals(true, database.createAccount("api3", "acc_id3", "acc_name3", null, "world3", "GuildWars2"));
//	}

//	@Test
//	public void assert_invalid(){
//		assertEquals(true, database.accountInvalid("api"));
//	}

//	@Test
//	public void assert_delete(){
//		assertEquals(true, database.deleteAccount("api2"));
//	}

	@Test
	public void assert_token() {
		database.getAll(null);
//		AccountInfo a=database.get(true, "59CD159F-53BA-8341-B85B-2D9FDB3B9D440F491B18-4C6D-47C6-A3BC-20049929312C");
//		assertEquals(true, database.markInvalid(a));
		assertEquals(AccountAPI.state.SUCCESS, database.addAccount(new AccountInfo("9B184F85-1A27-6341-9910-F3C7852AA010549E62DA-007F-470C-BD1A-867216579453")));

	}
}
