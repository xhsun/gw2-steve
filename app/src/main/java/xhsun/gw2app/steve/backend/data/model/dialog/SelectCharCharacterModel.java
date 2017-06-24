package xhsun.gw2app.steve.backend.data.model.dialog;

import java.util.Set;

/**
 * {@link AbstractSelectModel} for character selection state
 *
 * @author xhsun
 * @since 2017-05-16
 */

public class SelectCharCharacterModel extends AbstractSelectModel {

	public SelectCharCharacterModel(String name, Set<String> prefer) {
		super(name, (!prefer.contains(name)));
	}
}
