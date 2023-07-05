package enderchests.impl

import commons.util.StringUtil
import enderchests.LinkedChest
import me.vadim.util.conf.wrapper.impl.StringPlaceholder
import me.vadim.util.item.createItem
import me.vadim.util.menu.MenuList
import me.vadim.util.menu.button
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
class EnderChestMenu(plugin: EnderChestsPlugin, private val chest: LinkedEnderChest) : ChestMenu<ItemStack>(plugin) {

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