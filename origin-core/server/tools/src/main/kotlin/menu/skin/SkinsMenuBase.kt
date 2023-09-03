package menu.skin

import me.vadim.util.conf.wrapper.impl.StringPlaceholder
import me.vadim.util.menu.*
import menu.AttributeMenu
import org.bukkit.enchantments.Enchantment
import tools.impl.ToolsPlugin
import tools.impl.attribute.skins.Skin
import tools.impl.tool.type.ISkinnedTool

class SkinsMenuBase(plugin: ToolsPlugin, item: ISkinnedTool) : AttributeMenu<Skin, ISkinnedTool>(plugin, plugin.skinRegistry, item) {


	override val menu: MenuList<Skin> = template.toList(queryItems(), transformer = {
		item.formatMenuItemFor(it.key).apply {
			editMeta { meta ->
				if (item.hasSkin(it.key))
					meta.addEnchant(Enchantment.MENDING, 1, true)
			}
		}
	}) {
		title = config.skinMenuTitle.format(StringPlaceholder.of("name", item.itemStack.itemMeta.displayName))

		next = buttons[NEXT_SLOT]!! to NEXT_SLOT
		back = buttons[BACK_SLOT]!! to BACK_SLOT

		fill = exclude(*frame())
		item.itemStack.itemMeta.persistentDataContainer

		frameWith(blank())

		select = { event, button, enchant ->
//			getSubMenu(enchant).open(event.whoClicked)
		}
	}.apply { generate() }

	override fun test(e: Skin, item: ISkinnedTool): Boolean {
		return true
	}
}