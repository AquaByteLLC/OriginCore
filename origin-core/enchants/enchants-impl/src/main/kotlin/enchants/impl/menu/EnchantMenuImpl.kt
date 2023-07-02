package enchants.impl.menu

import enchants.impl.EnchantPlugin
import enchants.impl.conf.EnchantPlaceholder
import enchants.impl.item.EnchantedItemImpl
import enchants.item.Enchant
import enchants.item.EnchantedItem
import me.vadim.util.menu.*
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack

/**
 * @author vadim
 */
@Suppress("UNUSED_ANONYMOUS_PARAMETER")
class EnchantMenuImpl(plugin: EnchantPlugin, private val item: EnchantedItem) : EnchantMenu<Enchant>(plugin) {

    private fun Menu.getSubMenu(enchant: Enchant): Menu = menu(9 * 3) {
        title = config().enchantMenuTitle.format(EnchantPlaceholder.of("name", item.itemStack.itemMeta.displayName))
        parent = this@getSubMenu

        blank() into (0 until size.slots)

        frameWith(blank())
        previous(0)

        val icon = button(item.itemStack) {}
        icon into 4

        val lvl_ct = intArrayOf(1, 5, 10, 50, 100, 500, 1000)
        val pl = EnchantPlaceholder.builder()
            .set("maxLevel", enchant.maxLevel.toString())
            .set("maxChance", enchant.maxChance.toString())
            .set("maxCost", enchant.maxCost.toString())
            .set("name", enchant.key.name)

        fun the(i: Int, doUpgrade: Boolean = false): ItemStack {
            val lvl_current = item.getLevel(enchant.key)

            val n_lvls = lvl_ct[i - 1]
//			val lvl_delta = Math.min(n_lvls, enchant.maxLevel - lvl_current)
            val lvl_new = Math.min(lvl_current + n_lvls, enchant.maxLevel)

            var price = 0.0
            for (l in lvl_current until (lvl_new + 1))
                price += EnchantedItemImpl.calc(enchant, enchant.costType, l, enchant.startCost, enchant.maxCost)

            pl
                .set("levels", n_lvls.toString())
                .set("netCost", price.toString())
                .set("newChance",
                    EnchantedItemImpl.calc(enchant, enchant.chanceType, lvl_new, enchant.startChance, enchant.maxChance)
                        .toString()
                )
                .set("newLevel", lvl_new.toString())
                .set("currentLevel", lvl_current.toString())
                .set("currentChance", item.getChance(enchant.key).toString())
                .set("currentCost", item.getCost(enchant.key).toString())

            if (doUpgrade) {
                item.addEnchant(enchant.key, lvl_new)
                // todo: econ
            }

            return config().menuUpgrade.format(enchant.menuItem.type, pl.build()).amount(n_lvls).build()
        }

        close = {
            refresh()
            parent!!.open(it.player)
        }

        for (i in 1..7)
            button(the(i)) {
                click = { event, button ->
                    button.item = the(i, doUpgrade = true)
                    icon.item = this@EnchantMenuImpl.item.itemStack
                    regen()
                }
            } into (i + 9)
    }.apply { regen() }

    override val menu: MenuList<Enchant> = template.toList(queryItems(), transformer = {
        item.formatMenuItemFor(it.key).apply {
            editMeta { meta ->
                if (item.hasEnchant(it.key))
                    meta.addEnchant(Enchantment.MENDING, 1, true)
            }
        }
    }) {
        title = config().enchantMenuTitle.format(EnchantPlaceholder.of("name", item.itemStack.itemMeta.displayName))

        next = buttons[NEXT_SLOT]!! to NEXT_SLOT
        back = buttons[BACK_SLOT]!! to BACK_SLOT

        fill = exclude(*frame())
        item.itemStack.itemMeta.persistentDataContainer

        frameWith(blank())

        select = { event, button, enchant ->
            getSubMenu(enchant).open(event.whoClicked)
        }
    }

    override fun queryItems(): MutableList<Enchant> = registry.allEnchants
}