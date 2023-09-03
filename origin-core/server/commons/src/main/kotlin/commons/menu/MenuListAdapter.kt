package commons.menu

import me.vadim.util.menu.*
import me.vadim.util.menu.builder.MenuBuilder
import org.bukkit.event.inventory.InventoryClickEvent

/**
 * @author vadim
 */
abstract class MenuListAdapter<T> : MenuAdapter() {

	abstract val BACK_SLOT: Int
	abstract val NEXT_SLOT: Int

	protected open val template: Menu
		get() = menu(MENU_SIZE) {
			frameWith(blank())
			previous()

			button(global().menuNext) {} into NEXT_SLOT
			button(global().menuBack) {} into BACK_SLOT

			close = {

			}
		}

	public abstract override val menu: MenuList<T>

	protected abstract fun queryItems(): MutableList<T>

	override fun refresh() {
		menu.items = queryItems()
		menu.regen()
	}

}