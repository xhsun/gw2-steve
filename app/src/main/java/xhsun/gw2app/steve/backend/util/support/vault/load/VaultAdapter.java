package xhsun.gw2app.steve.backend.util.support.vault.load;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.annimon.stream.Stream;

import java.util.Collections;
import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.flexibleadapter.items.IExpandable;
import eu.davidea.flexibleadapter.items.IFilterable;
import xhsun.gw2app.steve.backend.data.model.AbstractModel;
import xhsun.gw2app.steve.backend.util.items.vault.VaultItem;
import xhsun.gw2app.steve.backend.util.items.vault.VaultSubHeader;

/**
 * Overrides various methods in {@link FlexibleAdapter} to make it more suitable
 *
 * @author xhsun
 * @since 2017-05-10
 */

class VaultAdapter extends FlexibleAdapter<AbstractFlexibleItem> {

//	public VaultAdapter(@Nullable List<AbstractFlexibleItem> items, boolean stableIds) {
//		super(items, null, stableIds);
//	}

	VaultAdapter(@Nullable List<AbstractFlexibleItem> items, @Nullable Object listeners, boolean stableIds) {
		super(items, listeners, stableIds);
	}

	@SuppressWarnings("unchecked")
	protected boolean filterObject(AbstractFlexibleItem item, String constraint) {
		if (item instanceof VaultSubHeader)
			return Stream.of(((VaultSubHeader<AbstractModel>) item).getSubItems())
					.anyMatch(i -> i.filter(constraint));
		else if (item instanceof VaultItem) return ((IFilterable) item).filter(constraint);

		return item instanceof IFilterable && ((IFilterable) item).filter(constraint);
	}

	@Override
	public boolean addSubItem(@IntRange(from = 0) int parentPosition,
	                          @IntRange(from = 0) int subPosition, @NonNull AbstractFlexibleItem item) {
		AbstractFlexibleItem parent = getItem(parentPosition);
		return isExpandable(parent) && ((IExpandable) parent).isExpanded()
				&& addItems(parentPosition + 1 + Math.max(0, subPosition), Collections.singletonList(item));
	}
}
