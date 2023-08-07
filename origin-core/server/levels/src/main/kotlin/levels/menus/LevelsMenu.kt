package levels.menus

import commons.menu.MenuAdapter
import levels.Level
import levels.LevelsPlugin
import levels.conf.LevelsConfig
import levels.menu.MenuSorter
import levels.registry.LevelRegistry
import me.vadim.util.conf.ConfigurationProvider
import me.vadim.util.menu.*
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

/**
 * @author vadim
 */
@Suppress("UNUSED_ANONYMOUS_PARAMETER")
class LevelsMenu(private val plugin: LevelsPlugin) : MenuAdapter<Level>() {

	override val MENU_SIZE = 9 * 4
	override val BACK_SLOT = 30
	override val DONE_SLOT = 31
	override val NEXT_SLOT = 32

	protected val reg: LevelRegistry = plugin.levelRegistry
	protected fun config(): LevelsConfig = plugin.config()

	override val menu: MenuList<Level> = template.toList(queryItems(), transformer = {
		ItemStack(Material.NETHER_STAR)
	}) {
		title = config().getTitle(false).raw()

		next = buttons[NEXT_SLOT]!! to NEXT_SLOT
		back = buttons[BACK_SLOT]!! to BACK_SLOT

		frameWith(blank())

		select = { event, button, lvl ->

		}
	}.toProgressionYX(MenuSorter.ProgressionOptions(1, 3, 1, 1, 7))

	override fun queryItems(): MutableList<Level> = reg.levels
}