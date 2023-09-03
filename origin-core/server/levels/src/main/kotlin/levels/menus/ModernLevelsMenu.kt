package levels.menus

import commons.menu.MenuListAdapter
import commons.menu.toProgressionYX
import levels.Level
import levels.LevelsPlugin
import levels.conf.LevelsConfig
import commons.menu.MenuSorter
import levels.registry.LevelRegistry
import me.vadim.util.item.ItemBuilder
import me.vadim.util.item.createItem
import me.vadim.util.menu.*
import org.bukkit.Material
import org.bukkit.entity.Player

/**
 * @author vadim
 */
@Suppress("UNUSED_ANONYMOUS_PARAMETER")
class ModernLevelsMenu(private val plugin: LevelsPlugin, private val owner: Player) : MenuListAdapter<Level>() {

	override val MENU_SIZE = 9 * 6
	override val BACK_SLOT = 48
	override val DONE_SLOT = 49
	override val NEXT_SLOT = 50

	protected val reg: LevelRegistry = plugin.levelRegistry
	protected fun config(): LevelsConfig = plugin.config()

	override val menu: MenuList<Level> = template.toList(queryItems(), transformer = {
		val lvl = plugin.accounts.getAccount(owner).level

		var btn: ItemBuilder = createItem(Material.NETHER_STAR)

		if (it.levelNumber() > lvl)
			btn = config().getLockedLevelButton(false)
		if (it.levelNumber() == lvl + 1)
			btn = config().getNextLevelButton(false)
		if (it.levelNumber() <= lvl)
			btn = config().getUnlockedLevelButton(false)

		btn
			.displayName(it.levelNumber().toString())
			.build()
	}) {
		title = config().getTitle(false).raw()

		next = buttons[NEXT_SLOT]!! to NEXT_SLOT
		back = buttons[BACK_SLOT]!! to BACK_SLOT

		blank() into (0 until size.slots)

		select = { event, button, lvl ->
			println(event.whoClicked.name + " clickled " + lvl.levelNumber())
		}
	}.toProgressionYX(MenuSorter.ProgressionOptions(1, 4, 1, 1, 7))

	override fun queryItems(): MutableList<Level> = reg.levels
}