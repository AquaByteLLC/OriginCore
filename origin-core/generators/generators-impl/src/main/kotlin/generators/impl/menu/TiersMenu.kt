package generators.impl.menu

import generators.impl.conf.Tiers
import generators.wrapper.Tier
import me.vadim.util.conf.ConfigurationProvider
import me.vadim.util.menu.MenuList
import me.vadim.util.menu.toList

/**
 * @author vadim
 */
class TiersMenu(conf: ConfigurationProvider) : GenMenu<Tier>(conf) {

	override val menu: MenuList<Tier> = template.toList(conf.open(Tiers::class.java).allTiers(), transformer = { it.menuItem }) {
		next = buttons[NEXT_SLOT]!! to NEXT_SLOT
		back = buttons[BACK_SLOT]!! to BACK_SLOT
	}

}