package generators.impl.menu

import commons.Commons
import commons.econ.TransactionResponse
import commons.impl.econ.OriginCurrency
import generators.impl.GensPlugin
import generators.impl.conf.GensSettings
import generators.impl.conf.Tiers
import generators.wrapper.Tier
import me.vadim.util.menu.MenuList
import me.vadim.util.menu.frame
import me.vadim.util.menu.toList
import org.bukkit.entity.Player

/**
 * @author vadim
 */
@Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
class BuyMenu(plugin: GensPlugin) : GenMenu<Tier>(plugin) {

    @Suppress("UNUSED_ANONYMOUS_PARAMETER")
    override val menu: MenuList<Tier> = template.toList(queryItems(), transformer = { it.menuItem }) {
        title = config().buyMenuTitle

        next = buttons[NEXT_SLOT]!! to NEXT_SLOT
        back = buttons[BACK_SLOT]!! to BACK_SLOT

        fill = exclude(*frame())

        select = { event, button, tier ->
            val player = event.whoClicked as Player
            when(Commons.commons().accounts.getAccount(player).take(OriginCurrency.CURRENCY, tier.buyPrice.toBigDecimal()).result) {
                TransactionResponse.CONFIRMED -> {
                    config().buyEffect.sendToIf(player,
                                                  player.location, { GensSettings.SOUNDS.isEnabled(it) },
                                                  player.location, { false })
                    player.inventory.addItem(tier.getGeneratorItem(player))
                }
                TransactionResponse.REJECTED -> {
                    config().errorEffect.sendToIf(player,
                                                    player.location, { GensSettings.SOUNDS.isEnabled(it) },
                                                    player.location, { false })
                }
            }
        }
    }

    override fun queryItems(): MutableList<Tier> = conf.open(Tiers::class.java).allTiers()

}