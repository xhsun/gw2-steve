package xhsun.gw2app.steve.backend.util.dialog.select.selectCharacter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import xhsun.gw2app.steve.backend.data.AccountInfo;
import xhsun.gw2app.steve.backend.util.dialog.select.Holder;
import xhsun.gw2app.steve.view.dialog.SelectCharacters;

/**
 * data holder that group character selection info by account for {@link SelectCharacters}
 *
 * @author xhsun
 * @since 2017-04-03
 */

public class AccountHolder extends Holder {
	private String api;
	private List<CharacterHolder> characters;

	public AccountHolder(AccountInfo info, Set<String> prefer) {
		super(info.getName(), (prefer.size() == 0));
		name = info.getName();
		api = info.getAPI();
		characters = new ArrayList<>();
	}

	public void setCharacters(List<CharacterHolder> characters) {
		this.characters = characters;
	}

	public List<CharacterHolder> getCharacters() {
		return characters;
	}

	public List<String> getShouldHideCharacters() {
		List<String> result = new ArrayList<>();
		for (CharacterHolder c : characters)
			if (!c.isSelected()) result.add(c.getName());
		return result;
	}

	public String getApi() {
		return api;
	}

	public void setApi(String api) {
		this.api = api;
	}
}

