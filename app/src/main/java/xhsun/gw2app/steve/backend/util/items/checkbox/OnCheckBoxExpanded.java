package xhsun.gw2app.steve.backend.util.items.checkbox;

/**
 * for notifying listener {@link CheckBoxHeaderItem} were expanded
 *
 * @author xhsun
 * @since 2017-05-16
 */

public interface OnCheckBoxExpanded {
	/**
	 * notify {@link CheckBoxHeaderItem}'s new state
	 *
	 * @param isExpanded true on expanded | false on collapsed
	 */
	void notifyExpanded(boolean isExpanded);
}
