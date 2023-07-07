package enderchests.impl

import commons.menu.MenuAdapter
import commons.util.StringUtil
import enderchests.ChestRegistry
import enderchests.LinkedChest
import enderchests.impl.conf.Config
import me.vadim.util.conf.wrapper.impl.StringPlaceholder
import me.vadim.util.item.createItem
import me.vadim.util.menu.MenuList
import me.vadim.util.menu.button
import me.vadim.util.menu.menu
import me.vadim.util.menu.toList
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

/**
 * @author vadim
 */
@Suppress("UNUSED_ANONYMOUS_PARAMETER")
class EnderChestMenu(private val plugin: EnderChestsPlugin, private val chest: LinkedEnderChest) : MenuAdapter<ItemStack>() {

	 override val MENU_SIZE = Config.CHEST_SIZE + 9
	 override val BACK_SLOT = 45
	 override val DONE_SLOT = 49
	 override val NEXT_SLOT = 53

	private val registry: ChestRegistry = plugin.chestRegistry

	private fun config(): Config = plugin.config()

	override val template = menu(MENU_SIZE) {
		previous()

		button(global().menuNext) {
			protect = true
		} into NEXT_SLOT
		button(global().menuBack) {
			protect = true
		} into BACK_SLOT
	}
	
	val items = mutableListOf<ItemStack>()

	override val menu: MenuList<ItemStack> = template.toList(items, transformer = { it }) {
		val color = chest.network.color
		title = config().chestMenuTitle.format(StringPlaceholder.of("color", color.chatColor.toString() + StringUtil.convertToUserFriendlyCase(color.name) + "&r"))

		button(createItem(Material.BLACK_STAINED_GLASS_PANE) {
			displayName = " "
			lore = emptyList()
			allFlags()
		}) {
			protect = true
		} into 45..53

		next = buttons[NEXT_SLOT]!! to NEXT_SLOT
		back = buttons[BACK_SLOT]!! to BACK_SLOT

		protectAll = false

		select = { event, button, item ->
			if(event.currentItem == null) // just picked up an item, mark the slot as a placeholder
				button.item = placeholder()
		}

		close = {
//			chest.close(it.player as Player)
		}
	}

	private fun placeholder(): ItemStack = ItemStack(Material.AIR).also {
		ItemUtil.mark(it)
	}

	override fun queryItems(): MutableList<ItemStack> = items.toMutableList().apply {
		while(size % (9*5) != 0)
			add(placeholder())
	}
}