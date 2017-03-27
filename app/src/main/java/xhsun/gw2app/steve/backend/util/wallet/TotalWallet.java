package xhsun.gw2app.steve.backend.util.wallet;

import android.support.annotation.NonNull;

import com.bignerdranch.expandablerecyclerview.model.Parent;

import java.util.ArrayList;
import java.util.List;

/**
 * Representing total amount across all accounts
 *
 * @author xhsun
 * @since 2017-03-26
 */

public class TotalWallet implements Parent<IndividualWallet>, Comparable<TotalWallet> {
	private List<IndividualWallet> accounts;
	private long id;
	private String name;
	private String icon;
	private long value;

	public TotalWallet(long id) {
		this.id = id;
		accounts = new ArrayList<>();
	}

	@Override
	public List<IndividualWallet> getChildList() {
		return accounts;
	}

	@Override
	public boolean isInitiallyExpanded() {
		return false;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
		for (IndividualWallet i : accounts) i.setIcon(icon);
	}

	public long getValue() {
		return value;
	}

	public void setValue(long value) {
		this.value = value;
	}

	@Override
	public int hashCode() {
		return (int) id;
	}

	@Override
	public boolean equals(Object obj) {
		return this == obj || obj != null && getClass() == obj.getClass() && ((TotalWallet) obj).getId() == id;
	}

	@Override
	public int compareTo(@NonNull TotalWallet obj) {
		if (id < (obj.id)) return -1;
		if (id == obj.id) return 0;

		return 1;
	}
}
