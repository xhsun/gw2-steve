package xhsun.gw2app.steve.backend.util.items.vault;

import android.animation.Animator;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.helpers.AnimatorHelper;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.flexibleadapter.items.IFilterable;
import eu.davidea.flexibleadapter.items.IHeader;
import eu.davidea.flexibleadapter.items.ISectionable;
import eu.davidea.viewholders.FlexibleViewHolder;
import me.xhsun.guildwars2wrapper.model.v2.Item;
import timber.log.Timber;
import xhsun.gw2app.steve.R;
import xhsun.gw2app.steve.backend.data.model.vault.item.Countable;
import xhsun.gw2app.steve.backend.data.model.vault.item.VaultItemModel;
import xhsun.gw2app.steve.backend.util.Utility;
import xhsun.gw2app.steve.backend.util.support.vault.load.ShouldLoadCheckHelper;

/**
 * Simple {@link AbstractFlexibleItem} that represent an item
 *
 * @author xhsun
 * @since 2017-05-09
 */

public class VaultItem extends AbstractFlexibleItem<VaultItem.StorageViewHolder>
		implements ISectionable<VaultItem.StorageViewHolder, IHeader>, IFilterable {
	private VaultItemModel data;
	private IHeader header;
	private ShouldLoadCheckHelper helper;

//	public VaultItem(VaultItemModel data) {
//		this.data = data;
//	}

	public VaultItem(VaultItemModel data, ShouldLoadCheckHelper helper) {
		this.data = data;
		this.helper = helper;
	}

	public VaultItemModel getData() {
		return data;
	}

	public void setData(VaultItemModel data) {
		this.data = data;
	}

	@Override
	public IHeader getHeader() {
		return header;
	}

	@Override
	public void setHeader(IHeader header) {
		this.header = header;
	}

	@Override
	public int getLayoutRes() {
		return R.layout.item_grid_item;
	}

	@Override
	public StorageViewHolder createViewHolder(View view, FlexibleAdapter flexibleAdapter) {
		return new StorageViewHolder(view, flexibleAdapter);
	}

	@Override
	public void bindViewHolder(FlexibleAdapter adapter, StorageViewHolder holder, int position, List payloads) {
		String icon;

		if (data.getSkinModel() != null) {
			icon = data.getSkinModel().getIcon();
			if (data.getSkinModel().isOverride() || (data.getItemModel() == null && data.getItemModel() == null)) {
				setRarity(data.getSkinModel().getRarity(), holder.rarity);
			} else if (data.getItemModel() != null)
				setRarity(data.getItemModel().getRarity(), holder.rarity);
		} else if (data.getMiscItem() != null) {
			icon = data.getMiscItem().getIcon();
			setRarity(Item.Rarity.Basic, holder.rarity);
		} else {
			icon = data.getItemModel().getIcon();
			setRarity(data.getItemModel().getRarity(), holder.rarity);
		}

		Picasso.with(holder.itemView.getContext()).load(icon).into(holder.image);

		if (!(data instanceof Countable) || ((Countable) data).getCount() < 2) {
			holder.count.setVisibility(View.GONE);
		} else {
			holder.count.setVisibility(View.VISIBLE);
			holder.count.setText(NumberFormat.getIntegerInstance().format(((Countable) data).getCount()));
		}

		holder.data = data;

		if (helper.getColumns() < 0) return;
		//noinspection unchecked
		FlexibleAdapter<AbstractFlexibleItem> temp = (FlexibleAdapter<AbstractFlexibleItem>) adapter;
		int limit = temp.getItemCount() - (helper.getColumns() * 2);
		if (!temp.contains(helper.getProgressItem()) && !temp.isEndlessScrollEnabled()
				&& position >= ((limit < 0) ? 0 : limit) && helper.shouldLoad()) {
			Timber.i("There is more to load, start loading again");
			temp.setEndlessScrollThreshold(1);
			temp.setEndlessProgressItem(helper.getProgressItem());
		}
	}

	@Override
	public boolean filter(String constraint) {
		if (constraint == null || constraint.equals("")) return true;
		String itemName = (data.getItemModel() != null) ? data.getItemModel().getName().toLowerCase() : "";
		String skinName = (data.getSkinModel() != null) ? data.getSkinModel().getName().toLowerCase() : "";
		String miscName = (data.getMiscItem() != null) ? data.getMiscItem().getName().toLowerCase() : "";
		return itemName.contains(constraint) || skinName.contains(constraint) || miscName.contains(constraint);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		VaultItem that = (VaultItem) o;

		return data.equals(that.data);
	}

	@Override
	public int hashCode() {
		return data.hashCode();
	}

	private void setRarity(Item.Rarity rarity, FrameLayout layout) {
		switch (rarity) {
			case Junk:
				layout.setBackgroundColor(Utility.Junk);
				break;
			case Fine:
				layout.setBackgroundColor(Utility.Fine);
				break;
			case Masterwork:
				layout.setBackgroundColor(Utility.Masterwork);
				break;
			case Rare:
				layout.setBackgroundColor(Utility.Rare);
				break;
			case Exotic:
				layout.setBackgroundColor(Utility.Exotic);
				break;
			case Ascended:
				layout.setBackgroundColor(Utility.Ascended);
				break;
			case Legendary:
				layout.setBackgroundColor(Utility.Legendary);
				break;
			case Basic:
			default:
				layout.setBackgroundColor(Utility.Basic);
				break;
		}
	}

	class StorageViewHolder extends FlexibleViewHolder {
		VaultItemModel data;
		FrameLayout rarity;
		ImageView image;
		TextView count;

		StorageViewHolder(View view, FlexibleAdapter adapter) {
			super(view, adapter);
			this.rarity = (FrameLayout) view.findViewById(R.id.item_rarity);
			this.image = (ImageView) view.findViewById(R.id.item_img);
			this.count = (TextView) view.findViewById(R.id.item_size);
		}

		@Override
		public void onClick(View view) {
			//TODO use proper dialog to show detailed info
			String name;
			if (data.getSkinModel() != null) name = data.getSkinModel().getName();
			else if (data.getMiscItem() != null) name = data.getMiscItem().getName();
			else name = data.getItemModel().getName();
			Toast.makeText(getContentView().getContext(), name, Toast.LENGTH_LONG).show();
			super.onClick(view);
		}

		@Override
		public void scrollAnimators(@NonNull List<Animator> animators, int position, boolean isForward) {
			//TODO might cause item to delay display, which will make it seems like missing item
			if (image.getDrawable() == null) AnimatorHelper.flipAnimator(animators, getContentView());
		}
	}
}
