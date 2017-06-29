package xhsun.gw2app.steve.backend.util.support.vault;

/**
 * Receive query text from {@link QueryTextCallback} and send it to apporiate {@link xhsun.gw2app.steve.backend.util.support.SearchCallback}
 *
 * @author xhsun
 * @since 2017-06-28
 */
public interface QueryTextListener {
	void notifyQueryTest(String query);
}
