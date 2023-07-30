package commons.menu

import commons.CommonsPlugin
import commons.levels.Level
import commons.util.StringUtil
import me.lucko.helper.item.ItemStackBuilder
import me.vadim.util.conf.wrapper.Placeholder
import me.vadim.util.item.ItemBuilderKt
import me.vadim.util.item.mutateItem
import me.vadim.util.menu.*
import net.minecraft.world.item.Items
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.OfflinePlayer
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Item
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

class LevelMenuLegacy(plugin: CommonsPlugin, private val owner: OfflinePlayer) : LevelMenu<Level>(plugin) {

    override val menu: MenuList<Level> = template.toList(queryItems(), transformer = { query(it) }) {
        title = "test title"

        next = buttons[NEXT_SLOT]!! to NEXT_SLOT
        back = buttons[BACK_SLOT]!! to BACK_SLOT

        fill = exclude(*frame())

        val account = accounts.getAccount(owner)
    }
    /*
    override val men1u: MenuList<Level> = template.toList(queryItems(), transformer = { it.asItem() }) {
        title = config().manageMenuTitle

        next = buttons[NEXT_SLOT]!! to NEXT_SLOT
        back = buttons[BACK_SLOT]!! to BACK_SLOT

        fill = exclude(*frame())

        val account = accounts.getAccount(owner)


        fun ItemBuilderKt.selfPlug() {
            if (!account.canBulkUpgrade()) {
                enchantment(Enchantment.MENDING, 1)
                flags(ItemFlag.HIDE_ENCHANTS)
                editMeta {
                    val L = it.lore ?: mutableListOf()
                    L += config().bulkUpgradeAd.map { x -> StringUtil.colorize(x) }
                    it.lore = L
                }
            }
        }

        button(mutateItem(config().bulkUpgradePlus1) {
            selfPlug()
        }) {
            click = { event, button ->
                val player = event.whoClicked as Player
                var uct = 0
                if (account.canBulkUpgrade())
                    for (loc in items.map { it.blockLocation })
                        if (reg.getGenAt(loc)?.upgrade(reg) == UpgradeResult.NOT_ENOUGH_CURRENCY)
                            break
                        else
                            uct++
                if(uct > 0)
                    config().upgradeEffect.sendToIf(player,
                                                    player.location, { GensSettings.SOUNDS.isEnabled(it) },
                                                    player.location, { false })
                refresh()
            }
        } into 7

        button(mutateItem(config().bulkUpgradeMax) {
            selfPlug()
        }) {
            click = { event, button ->
                val player = event.whoClicked as Player
                var uct = 0
                if (account.canBulkUpgrade())
                    w@ while (true) {
                        var upgradedAny = false
                        f@ for (loc in items.map { it.blockLocation })
                            if (reg.getGenAt(loc)?.upgrade(reg) == UpgradeResult.SUCCESS)
                                upgradedAny = true;
                        if (!upgradedAny)
                            break@w
                        else
                            uct++
                    }
                if(uct > 0)
                    config().upgradeEffect.sendToIf(player,
                                                    player.location, { GensSettings.SOUNDS.isEnabled(it) },
                                                    player.location, { false })
                refresh()
            }
        } into 8

        select = { event, button, gen ->
            val player = event.whoClicked as Player
            getSubMenu(gen).open(player)
        }
    }

     */

    override fun queryItems(): MutableList<Level> = reg.levels;
    fun query(level: Level): ItemStack {
        val playerLevel = accounts.getAccount(owner).level!!.toInt()
        val req = level.levelNumber()

        if (playerLevel > req) return config().getUnlockedLevel(true);
        if ((playerLevel + 1) == req) return config().getNextLevel(true);
        if ((playerLevel < req)) return config().getLockedLevel(true);

        return ItemStackBuilder.of(Material.AIR).build()
    }
}