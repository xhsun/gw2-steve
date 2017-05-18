package xhsun.gw2app.steve.backend.util.dialog.select.selectCharacter;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import xhsun.gw2app.steve.backend.data.AccountInfo;
import xhsun.gw2app.steve.backend.util.dialog.select.Holder;
import xhsun.gw2app.steve.view.dialog.fragment.SelectCharacters;

/**
 * data holder that group character selection info by account for {@link SelectCharacters}
 *
 * @author xhsun
 * @since 2017-04-03
 */

public class SelectCharAccountHolder extends Holder {
	private String api;
	private List<SelectCharCharacterHolder> characters;

	public SelectCharAccountHolder(AccountInfo info, Set<String> prefer) {
		super(info.getName(), (prefer.size() == 0));
		name = info.getName();
		api = info.getAPI();
		characters = new ArrayList<>();
	}

	public void setCharacters(List<SelectCharCharacterHolder> characters) {
		this.characters = characters;
	}

	public List<SelectCharCharacterHolder> getCharacters() {
		return characters;
	}

	public List<String> getShouldHideCharacters() {
		return Stream.of(characters)
				.filter(c -> !c.isSelected()).map(Holder::getName).collect(Collectors.toList());
	}

	public String getApi() {
		return api;
	}

	public void setApi(String api) {
		this.api = api;
	}
}

