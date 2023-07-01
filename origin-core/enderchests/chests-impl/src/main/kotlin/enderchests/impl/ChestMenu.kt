package enderchests.impl

import enderchests.ChestRegistry
import enderchests.impl.conf.Config
import me.vadim.util.item.createItem
import me.vadim.util.menu.MenuList
import me.vadim.util.menu.builder.MenuBuilder
import me.vadim.util.menu.button
import me.vadim.util.menu.button.Button
import me.vadim.util.menu.menu
import org.bukkit.Material
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemFlag

/**
 * @author vadim
 */
abstract class ChestMenu<T>(protected val plugin: EnderChestsPlugin) {

	protected val registry: ChestRegistry = plugin.chestRegistry

	companion object {
		const val BACK_SLOT = 45
		const val DONE_SLOT = 49
		const val NEXT_SLOT = 53
	}

	abstract val menu: MenuList<T>

	protected abstract fun queryItems(): MutableList<T>

	fun refresh() {
		menu.items = queryItems()
		menu.regen()
	}

	protected fun config(): Config = plugin.config()

	protected fun MenuBuilder.blank(): Button =
		button(createItem(Material.GRAY_STAINED_GLASS_PANE) {
			displayName = " "
			lore = emptyList()
			flags(*ItemFlag.values())
		}) {
			protect = true
		}

	protected fun MenuBuilder.previous(slot: Int? = null, action: (InventoryClickEvent) -> Unit = {}) {
		previousMenuButton = button(config().menuDone) {
			click = { event, _ ->
				action(event)
				if (parent == null)
					event.whoClicked.closeInventory()
				else
					refresh()
			}
		} to (slot ?: DONE_SLOT)
	}

	protected val template = menu(Config.CHEST_SIZE + 9) {
		previous()

		button(config().menuNext) {
			protect = true
		} into NEXT_SLOT
		button(config().menuBack) {
			protect = true
		} into BACK_SLOT
	}
}