package levels.menus

import levels.menu.MenuSorter
import me.vadim.util.menu.MenuList
import me.vadim.util.menu.button
import me.vadim.util.menu.impl.ListMenu
import me.vadim.util.menu.makesMeCry
import org.bukkit.plugin.Plugin

fun <T> MenuList<T>.toProgressionYX(opts: MenuSorter.ProgressionOptions) = ProgressionMenuYX(makesMeCry, this, opts)

/**
 * @author vadim
 */
class ProgressionMenuYX<T>(plugin: Plugin, menu: MenuList<T>, private val opts: MenuSorter.ProgressionOptions) :
	ListMenu<T>(plugin, menu.template, menu.transformer, menu.fill, menu.next, menu.back, menu.select, menu.page, menu.items) {

	override fun generate() {
		this.fill = include(*MenuSorter.slotsYX(opts))
		super.generate()

		for ((i, chunk) in items.chunked(MenuSorter.ctYX(opts)).withIndex()) {
			val sort = MenuSorter.sortYX(chunk, opts)

			sort.forEach { (slot, t) ->
				pages[i].apply {
					button(transformer(t)) {
						click = { event, button ->
							sort.getOrDefault(slot, null)?.apply { select(event, button, this) }
						}
					} into slot
				}
			}
		}
	}

}