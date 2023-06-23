package generators.impl.menu

import commons.CommonsPlugin
import commons.data.AccountStorage
import generators.GeneratorRegistry
import generators.impl.GensPlugin
import generators.impl.conf.Config
import generators.impl.data.GenAccount
import me.vadim.util.conf.ConfigurationProvider
import me.vadim.util.item.createItem
import me.vadim.util.menu.MenuList
import me.vadim.util.menu.builder.MenuBuilder
import me.vadim.util.menu.button
import me.vadim.util.menu.button.Button
import me.vadim.util.menu.frameWith
import me.vadim.util.menu.menu
import org.bukkit.Material
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemFlag

/**
 * @author vadim
 */
abstract class GenMenu<T>(plugin: GensPlugin) {

	protected val conf: ConfigurationProvider = plugin.configuration
	protected val accounts: AccountStorage<GenAccount> = plugin.accounts
	protected val reg: GeneratorRegistry = plugin.registry

	companion object {
		const val BACK_SLOT = 48
		const val DONE_SLOT = 49
		const val NEXT_SLOT = 50
	}

	abstract val menu: MenuList<T>

	protected abstract fun queryItems(): MutableList<T>

	fun refresh() {
		menu.items = queryItems()
		menu.regen()
	}

	protected fun config(): Config = conf.open(Config::class.java)

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

	protected val template = menu(6 * 9) {
		frameWith(blank())
		previous()

		button(config().menuNext) {} into NEXT_SLOT
		button(config().menuBack) {} into BACK_SLOT
	}
}