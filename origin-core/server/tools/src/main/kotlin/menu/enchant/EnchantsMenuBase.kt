package menu.enchant

import me.vadim.util.conf.wrapper.impl.StringPlaceholder
import me.vadim.util.menu.*
import menu.AttributeMenu
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import tools.impl.ToolsPlugin
import tools.impl.attribute.enchants.Enchant
import tools.impl.tool.impl.EnchantedTool
import tools.impl.tool.type.IEnchantedTool

class EnchantMenuBase(plugin: ToolsPlugin, item: IEnchantedTool) : AttributeMenu<Enchant, IEnchantedTool>(plugin, plugin.enchantRegistry, item) {


    private fun Menu.getSubMenu(enchant: Enchant): Menu = menu(9 * 3) {
        title = config.enchantMenuTitle.format(StringPlaceholder.of("name", item.itemStack.itemMeta.displayName))
        parent = this@getSubMenu

        blank() into (0 until size.slots)

        frameWith(blank())
        previous(0)

        val icon = button(item.itemStack) {}
        icon into 4

        val lvl_ct = intArrayOf(1, 5, 10, 50, 100, 500, 1000)
        val pl = StringPlaceholder.builder()
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

            /* Feel free to use this as a replacement. Can't be asked to modify it, but this should be a lot better.
            AreaUnderCurve { x ->
                x.plus(EnchantedTool.calc(enchant, enchant.costType, lvl_current, enchant.startCost, enchant.maxCost).toDouble())
            }.apply {
                price.plus(IntSimpson(lvl_current.toDouble(), (lvl_new + 1).toDouble(), (lvl_new + 1).toInt()));
            }
             */

            for (l in lvl_current until (lvl_new + 1))
                price += (
                    EnchantedTool.calc(enchant, enchant.costType, l, enchant.startCost, enchant.maxCost).toDouble()
                )

            pl
                .set("levels", n_lvls.toString())
                .set("netCost", price.toString())
                .set(
                    "newChance",
                    EnchantedTool.calc(enchant, enchant.chanceType, lvl_new, enchant.startChance, enchant.maxChance)
                        .toString()
                )
                .set("newLevel", lvl_new.toString())
                .set("currentLevel", lvl_current.toString())
                .set("currentChance", item.getChance(enchant.key).toString())
                .set("currentCost", item.getCost(enchant.key).toString())

            if (doUpgrade) {
                println("Item $lvl_current -> $lvl_new")
                println("Item Before ${item.getLevel(enchant.key)}")
                item.addEnchant(enchant.key, lvl_new)
                println("Item After ${item.getLevel(enchant.key)}")
                // todo: econ
            }

            return config.enchantMenuUpgrade.format(enchant.menuItem.type, pl.build()).amount(n_lvls).build()
        }

        for (i in 1..7)
            button(the(i)) {
                click = { _, button ->
                    button.item = the(i, doUpgrade = true)
                    icon.item = this@EnchantMenuBase.item.itemStack
                    regen()
                }
            } into (i + 9)
    }.apply { generate() }

    override val menu: MenuList<Enchant> = template.toList(queryItems(), transformer = {
        item.formatMenuItemFor(it.key).apply {
            editMeta { meta ->
                if (item.hasEnchant(it.key))
                    meta.addEnchant(Enchantment.MENDING, 1, true)
            }
        }
    }) {
        title = config.enchantMenuTitle.format(StringPlaceholder.of("name", item.itemStack.itemMeta.displayName))

        next = buttons[NEXT_SLOT]!! to NEXT_SLOT
        back = buttons[BACK_SLOT]!! to BACK_SLOT

        fill = exclude(*frame())
        item.itemStack.itemMeta.persistentDataContainer

        frameWith(blank())

        select = { event, button, enchant ->
            getSubMenu(enchant).open(event.whoClicked)
        }
    }.apply { generate() }

    override fun test(e: Enchant, item: IEnchantedTool): Boolean = true

//    override fun queryItems(): MutableList<Enchant> = registry.allAttributes

}