package xhsun.gw2app.steve.backend.util.dialog;

import com.bignerdranch.expandablerecyclerview.model.Parent;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import xhsun.gw2app.steve.backend.database.account.AccountInfo;
import xhsun.gw2app.steve.backend.util.inventory.OnLoadMoreListener;

/**
 * Created by hannah on 03/04/17.
 */

public class AccountHolder implements Parent<AccountHolder.CharacterHolder> {
	private String name;
	private List<CharacterHolder> characters;

	public AccountHolder(AccountInfo info, OnLoadMoreListener listener) {
		name = info.getName();
		characters = new ArrayList<>();
		Set<String> prefer = listener.getPreferences(info);
		for (String name : info.getCharacterNames()) characters.add(new CharacterHolder(name, prefer));
	}

	@Override
	public List<AccountHolder.CharacterHolder> getChildList() {
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

	public List<String> getSelectedCharacterNames() {
		List<String> result = new ArrayList<>();
		for (CharacterHolder c : characters)
			if (c.isSelected()) result.add(c.getName());
		return result;
	}

	public void setView(SelectCharacterListAdapter.AccountViewHolder view) {
		for (CharacterHolder c : characters) c.setParentView(view);
	}

	void setAllSelected(boolean isSelected) {
		for (CharacterHolder c : characters) {
			c.isSelected = isSelected;
			if (!isSelected) c.isParent = true;
			if (c.childView != null) c.childView.check.setChecked(isSelected);
		}
	}

	void resetIsParent() {
		for (CharacterHolder c : characters) c.isParent = false;
	}

	class CharacterHolder {
		private String name;
		private boolean isSelected = false, isParent = false;
		private SelectCharacterListAdapter.AccountViewHolder parentView;
		private SelectCharacterListAdapter.CharacterViewHolder childView;

		private CharacterHolder(String name, Set<String> prefer) {
			this.name = name;
			if (prefer.contains(name)) isSelected = true;
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
			if (!selected && !isParent) parentView.deselect();
			isSelected = selected;
		}

		public SelectCharacterListAdapter.CharacterViewHolder getChildView() {
			return childView;
		}

		void setChildView(SelectCharacterListAdapter.CharacterViewHolder childView) {
			this.childView = childView;
		}

		void setParentView(SelectCharacterListAdapter.AccountViewHolder parentView) {
			this.parentView = parentView;
		}
	}
}

