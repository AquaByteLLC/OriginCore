package enderchests.impl

import commons.data.account.AccountProvider
import commons.menu.MenuAdapter
import commons.util.StringUtil
import enderchests.ChestRegistry
import enderchests.NetworkColor
import enderchests.impl.conf.Config
import enderchests.impl.data.EnderChestAccount
import me.vadim.util.conf.wrapper.impl.StringPlaceholder
import me.vadim.util.menu.*
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import java.util.*

/**
 * @author vadim
 */
@Suppress("UNUSED_ANONYMOUS_PARAMETER")
class EnderChestMenu(private val plugin: EnderChestsPlugin, private val player: UUID) : MenuAdapter<NetworkColor>() {

	override val MENU_SIZE = 9 * 3
	override val BACK_SLOT = 45
	override val DONE_SLOT = 49
	override val NEXT_SLOT = 53

	private val registry: ChestRegistry = plugin.chestRegistry
	private val accounts: AccountProvider<EnderChestAccount> = plugin.accounts

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

	companion object {

		val colors: Map<NetworkColor, Material>

		init {
			val map = mutableMapOf<NetworkColor, Material>()

			map[NetworkColor.RED] = Material.RED_CONCRETE
			map[NetworkColor.GOLD] = Material.YELLOW_CONCRETE
			map[NetworkColor.LIME] = Material.LIME_CONCRETE
			map[NetworkColor.AQUA] = Material.CYAN_CONCRETE
			map[NetworkColor.PINK] = Material.PINK_CONCRETE
			map[NetworkColor.PURPLE] = Material.PURPLE_CONCRETE

			colors = map.toMap()
		}
	}

	override val menu: MenuList<NetworkColor> = template.toList(queryItems(), transformer = {
		val color = it.chatColor.toString() + StringUtil.convertToUserFriendlyCase(it.chatColor.name) + ChatColor.WHITE
		val builder = config().selectColorItem.format(StringPlaceholder.of("color", color))
		val account = accounts.getAccount(player)

		if(account.atSlotLimit(it)) { // the code your eyes are about to witness is unholy to the Lord
			val pl = StringPlaceholder.builder().set("color", color).set("limit", account.slotLimit.toString()).build()
			builder.allFlags().enchantment(Enchantment.MENDING, 1).editMeta { meta ->
				var list = meta.lore
				if(list == null) list = mutableListOf()
				list.addAll(config().slotLimitLore.map { L -> L.format(pl) }.toList())
				meta.lore = list
			}
		}

		builder.build()
	}) {
		title = config().selectColorMenuTitle.format(StringPlaceholder.EMPTY)

		frameWith(blank())

		// blank out buttons since we know this will be one page
		next = blank() to NEXT_SLOT
		back = blank() to BACK_SLOT

		select = { event, button, color ->
			val player = event.whoClicked as Player

			if (!accounts.getAccount(player).atSlotLimit(color)) {

			}
		}

		close = {
//			chest.close(it.player as Player)
		}
	}

	override fun queryItems() = NetworkColor.values().toMutableList()
}