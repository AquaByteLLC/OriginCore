package generators.impl.menu

import generators.impl.conf.Config
import me.vadim.util.conf.ConfigurationProvider
import me.vadim.util.item.createItem
import me.vadim.util.menu.MenuList
import me.vadim.util.menu.button
import me.vadim.util.menu.menu
import org.bukkit.Material
import org.bukkit.inventory.ItemFlag

/**
 * @author vadim
 */
abstract class GenMenu<T>(protected val conf: ConfigurationProvider) {

	companion object {
		const val BACK_SLOT = 48
		const val DONE_SLOT = 49
		const val NEXT_SLOT = 50
	}

	abstract val menu: MenuList<T>

	protected fun config(): Config = conf.open(Config::class.java)

	protected val template = menu(6 * 9) {
		var frame = emptyArray<Int>()

		for (i in 0 until 6 * 9) {
			if (i < 9) frame += i // first row
			if ((i + 1) % 9 == 0 || i % 9 == 0) frame += i // sides (i or next multiple of 9)
			if (i >= size.slots - 9) frame += i // last row
		}

		button(createItem(Material.GRAY_STAINED_GLASS_PANE) {
			displayName = " "
			lore = emptyList()
			flags(*ItemFlag.values())
		}) {
			protect = true
		} into frame

		previousMenuButton = button(config().menuDone) {
			click = { event, _ ->
				if (parent == null) event.whoClicked.closeInventory()
			}
		} to DONE_SLOT
		button(config().menuNext) {} into NEXT_SLOT
		button(config().menuBack) {} into BACK_SLOT
	}

}