package enchants.impl.menu

import enchants.EnchantRegistry
import enchants.impl.EnchantPlugin
import enchants.impl.conf.GeneralConfig
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
abstract class EnchantMenu<T>(protected val plugin: EnchantPlugin) {

    protected val registry: EnchantRegistry = plugin.registry

    companion object {
        const val BACK_SLOT = 30
        const val DONE_SLOT = 31
        const val NEXT_SLOT = 32
    }

    abstract val menu: MenuList<T>

    protected abstract fun queryItems(): MutableList<T>

    fun refresh() {
        menu.items = queryItems()
        menu.regen()
    }

    protected fun config(): GeneralConfig = plugin.generalConfig

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

    protected val template = menu(9 * 4) {
        frameWith(blank())
        previous()

        button(config().menuNext) {} into NEXT_SLOT
        button(config().menuBack) {} into BACK_SLOT
    }
}