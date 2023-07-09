package generators.impl.menu

import commons.util.StringUtil
import generators.impl.GensPlugin
import generators.impl.conf.GensSettings
import generators.impl.wrapper.GenInfo
import generators.wrapper.Generator
import generators.wrapper.result.DestroyResult
import generators.wrapper.result.UpgradeResult
import me.vadim.util.conf.wrapper.Placeholder
import me.vadim.util.item.ItemBuilderKt
import me.vadim.util.item.mutateItem
import me.vadim.util.menu.*
import org.bukkit.OfflinePlayer
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import settings.EnumeratedSetting

/**
 * @author vadim
 */
@Suppress("UNUSED_ANONYMOUS_PARAMETER", "NON_EXHAUSTIVE_WHEN_STATEMENT")
class ManageMenu(plugin: GensPlugin, private val owner: OfflinePlayer) : GenMenu<Generator>(plugin) {

	private fun Menu.getSubMenu(gen: Generator): Menu = menu(9 * 2) {
		fun gen(): Generator? = reg.getGenAt(gen.blockLocation)

		title = config().manageMenuTitle
		parent = this@getSubMenu

		blank() into (0 until size.slots)

		frameWith(blank())
		previous(0)

		val icon = button(gen()!!.asItem()) {}
		icon into 4

		val pl: Placeholder = GenInfo.placeholdersForTier(gen.currentTier)

		button(config().manageMenuIndividualUpgrade.format(pl).build()) {
			click = { event, button ->
				val player = event.whoClicked as Player
				if(gen()?.upgrade(reg) == UpgradeResult.SUCCESS) {
					icon.item = gen()!!.asItem()
					config().upgradeEffect.sendToIf(player,
													player.location, { GensSettings.SOUNDS.isEnabled(it) },
													player.location, { false })
				}
				regen()
			}
		} into 11

		button(config().manageMenuIndividualDelete.format(pl).build()) {
			click = { event, button ->
				val player = event.whoClicked as Player
				if(gen()?.destroy(reg, player) == DestroyResult.SUCCESS)
					config().errorEffect.sendToIf(player,
													player.location, { GensSettings.SOUNDS.isEnabled(it) },
													player.location, { false })
				refresh()
				if (reg.countGenerators(player.uniqueId) == 0)
					player.closeInventory()
				else
					parent!!.open(player)
				//todo: bug in MenuList impl when items becomes empty the first page is not reset
			}
		} into 15

	}.apply { regen() }

	override val menu: MenuList<Generator> = template.toList(queryItems(), transformer = { it.asItem() }) {
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

	override fun queryItems(): MutableList<Generator> = reg.getGenerators(owner)
}