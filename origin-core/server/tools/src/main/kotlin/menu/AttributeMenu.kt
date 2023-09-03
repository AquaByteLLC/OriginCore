package menu

import commons.menu.MenuListAdapter
import tools.impl.ToolsPlugin
import tools.impl.attribute.augments.Augment
import tools.impl.conf.Config
import tools.impl.registry.AttributeRegistry
import tools.impl.tool.IBaseTool

/**
 * @author vadim
 */
abstract class AttributeMenu<T, I: IBaseTool>(protected val plugin: ToolsPlugin,
					   protected val registry: AttributeRegistry<T>,
					   protected val item: I) : MenuListAdapter<T>() {

	override val MENU_SIZE = 9 * 4
	override val BACK_SLOT = 30
	override val DONE_SLOT = 31
	override val NEXT_SLOT = 32

	protected val config: Config
		get() = plugin.config()

	abstract fun test(e: T, item: I): Boolean

	override fun queryItems(): MutableList<T> {
		val list = mutableListOf<T>()

		for (e in registry.allAttributes)
			if (e != null) {
				if (test(e, item))
					list.add(e)
			}

		return list
	}

}