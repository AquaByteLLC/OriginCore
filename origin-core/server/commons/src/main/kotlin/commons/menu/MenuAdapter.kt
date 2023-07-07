package commons.menu

import commons.Commons
import commons.conf.CommonsConfig
import me.vadim.util.item.createItem
import me.vadim.util.menu.*
import me.vadim.util.menu.builder.MenuBuilder
import me.vadim.util.menu.button.Button
import org.bukkit.Material
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemFlag

/**
 * @author vadim
 */
abstract class MenuAdapter<T> {

	abstract val MENU_SIZE: Int
	abstract val BACK_SLOT: Int
	abstract val DONE_SLOT: Int
	abstract val NEXT_SLOT: Int

	protected fun global(): CommonsConfig = Commons.config()

	protected open val template: Menu
		get() = menu(MENU_SIZE) {
			frameWith(blank())
			previous()

			button(global().menuNext) {} into NEXT_SLOT
			button(global().menuBack) {} into BACK_SLOT
		}

	public abstract val menu: MenuList<T>

	protected abstract fun queryItems(): MutableList<T>

	fun refresh() {
		menu.items = queryItems()
		menu.regen()
	}

	protected fun MenuBuilder.blank(): Button =
		button(createItem(Material.GRAY_STAINED_GLASS_PANE) {
			displayName = " "
			lore = emptyList()
			flags(*ItemFlag.values())
		}) {
			protect = true
		}

	protected fun MenuBuilder.previous(slot: Int? = null, action: (InventoryClickEvent) -> Unit = {}) {
		previousMenuButton = button(global().menuDone) {
			click = { event, _ ->
				action(event)
				if (parent == null)
					event.whoClicked.closeInventory()
				else
					refresh()
			}
		} to (slot ?: DONE_SLOT)
	}
}