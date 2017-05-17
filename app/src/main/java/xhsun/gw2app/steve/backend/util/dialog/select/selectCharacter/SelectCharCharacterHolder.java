package xhsun.gw2app.steve.backend.util.dialog.select.selectCharacter;

import java.util.Set;

import xhsun.gw2app.steve.backend.util.dialog.select.Holder;

/**
 * {@link Holder} for character selection state
 *
 * @author xhsun
 * @since 2017-05-16
 */

public class SelectCharCharacterHolder extends Holder {

	public SelectCharCharacterHolder(String name, Set<String> prefer) {
		super(name, (!prefer.contains(name)));
	}
}
