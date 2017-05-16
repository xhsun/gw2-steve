package xhsun.gw2app.steve.backend.util.dialog.selectCharacter;

import com.bignerdranch.expandablerecyclerview.model.Parent;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import xhsun.gw2app.steve.backend.data.AccountInfo;
import xhsun.gw2app.steve.view.dialog.SelectCharacters;

/**
 * data holder that group character selection info by account for {@link SelectCharacters}
 *
 * @author xhsun
 * @since 2017-04-03
 */

public class AccountHolder implements Parent<AccountHolder.CharacterHolder> {
	private String name, api;
	private SelectCharacterListAdapter.AccountViewHolder holder;
	private List<CharacterHolder> characters;

	public AccountHolder(AccountInfo info, Set<String> prefer) {
		name = info.getName();
		api = info.getAPI();
		characters = new ArrayList<>();
		for (String name : info.getAllCharacterNames())
			characters.add(new CharacterHolder(name, prefer));
	}

	@Override
	public List<CharacterHolder> getChildList() {
		return characters;
	}

	@Override
	public boolean isInitiallyExpanded() {
		return false;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setCharacters(List<CharacterHolder> characters) {
		this.characters = characters;
	}

	public List<String> getShouldHideCharacters() {
		List<String> result = new ArrayList<>();
		for (CharacterHolder c : characters)
			if (!c.isSelected()) result.add(c.getName());
		return result;
	}

	public void setView(SelectCharacterListAdapter.AccountViewHolder view) {
		holder = view;
	}

	void setAllSelected(boolean isSelected) {
		for (CharacterHolder c : characters) {
			c.isSelected = isSelected;
			if (c.childView != null) c.childView.check.setChecked(isSelected);
		}
	}

	SelectCharacterListAdapter.AccountViewHolder getHolder() {
		return holder;
	}

	public String getApi() {
		return api;
	}

	public void setApi(String api) {
		this.api = api;
	}

	/**
	 * data holder to holder character selection info for {@link SelectCharacters}
	 */
	class CharacterHolder {
		private String name;
		private boolean isSelected = false;
		private SelectCharacterListAdapter.ChildListAdapter.CharacterViewHolder childView;

		private CharacterHolder(String name, Set<String> prefer) {
			this.name = name;
			if (!prefer.contains(name)) isSelected = true;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		boolean isSelected() {
			return isSelected;
		}

		void setSelected(boolean selected) {
			isSelected = selected;
		}

		void setChildView(SelectCharacterListAdapter.ChildListAdapter.CharacterViewHolder childView) {
			this.childView = childView;
		}
	}
}

