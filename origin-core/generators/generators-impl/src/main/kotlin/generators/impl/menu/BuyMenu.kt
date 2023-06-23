package generators.impl.menu

import generators.impl.GensPlugin
import generators.impl.conf.Tiers
import generators.wrapper.Tier
import me.vadim.util.menu.MenuList
import me.vadim.util.menu.toList
import org.bukkit.entity.Player

/**
 * @author vadim
 */
class BuyMenu(plugin: GensPlugin) : GenMenu<Tier>(plugin) {

	@Suppress("UNUSED_ANONYMOUS_PARAMETER")
	override val menu: MenuList<Tier> = template.toList(queryItems(), transformer = { it.menuItem }) {
		title = config().buyMenuTitle

		next = buttons[NEXT_SLOT]!! to NEXT_SLOT
		back = buttons[BACK_SLOT]!! to BACK_SLOT

		select = { event, button, tier ->
			val player = event.whoClicked as Player
			player.inventory.addItem(tier.getGeneratorItem(player))
			//todo: econ
		}
	}

	override fun queryItems(): MutableList<Tier> = conf.open(Tiers::class.java).allTiers()

}