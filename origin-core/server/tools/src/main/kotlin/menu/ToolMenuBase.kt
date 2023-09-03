package menu

import commons.menu.MenuAdapter
import me.vadim.util.menu.Menu
import me.vadim.util.menu.button
import me.vadim.util.menu.menu
import menu.augment.AugmentsMenuBase
import menu.enchant.EnchantMenuBase
import menu.skin.SkinsMenuBase
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.ItemStack
import tools.impl.ToolsPlugin
import tools.impl.conf.Config
import tools.impl.tool.impl.AugmentedTool
import tools.impl.tool.impl.EnchantedTool
import tools.impl.tool.impl.SkinnedTool

@Suppress("UNUSED_ANONYMOUS_PARAMETER")
class ToolMenuBase(
	private val plugin: ToolsPlugin,
	private val item: ItemStack
				  ) : MenuAdapter() {

	private val isAugment = AugmentedTool.canAugment(item)
	private val isSkin = SkinnedTool.canSkin(item)
	private val isEnchant = EnchantedTool.canEnchant(item)
	private fun config(): Config = plugin.config()

	override val MENU_SIZE = 0
	override val DONE_SLOT = 0

	override val menu: Menu = menu(InventoryType.HOPPER) {
		title = "General Tools Menu"

		if(isAugment)
		button(config().augmentsItem) {
			click = { event, _ ->
				AugmentsMenuBase(plugin, plugin.augmentFactory.wrapItemStack(item)).menu.open(event.whoClicked)
			}
		} into 3

		if(isSkin)
		button(config().skinsItem) {
			click = { event, _ ->
				SkinsMenuBase(plugin, plugin.skinFactory.wrapItemStack(item)).menu.open(event.whoClicked)
			}
		} into 2

		if(isEnchant)
		button(config().enchantsItem) {
			click = { event, _ ->
				EnchantMenuBase(plugin, plugin.enchantFactory.wrapItemStack(item)).menu.open(event.whoClicked)
			}
		} into 1

	}.apply{ generate() }
}