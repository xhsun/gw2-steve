package xhsun.gw2app.steve.backend.database.common;

import java.util.List;

/**
 * For manipulate currency
 *
 * @author xhsun
 * @since 2017-03-30
 */

public class CurrencyWrapper {
	private CurrencyDB currencyDB;

	public CurrencyWrapper(CurrencyDB currencyDB) {
		this.currencyDB = currencyDB;
	}

	/**
	 * get all currency info that is in the database
	 *
	 * @return list of all currency info
	 */
	public List<CurrencyInfo> getAll() {
		return currencyDB.getAll();
	}

	/**
	 * remove given currency from database
	 *
	 * @param id currency id
	 */
	public void delete(long id) {
		currencyDB.delete(id);
	}

	/**
	 * insert or replace given currency
	 *
	 * @param id   currency id
	 * @param name currency name
	 * @param icon currency icon
	 */
	public void replace(long id, String name, String icon) {
		currencyDB.replace(id, name, icon);
	}
}
