package menu.augment
/*
import commons.menu.MenuAdapter
import me.vadim.util.conf.wrapper.impl.StringPlaceholder
import me.vadim.util.menu.MenuList
import me.vadim.util.menu.frame
import me.vadim.util.menu.frameWith
import me.vadim.util.menu.toList
import org.bukkit.enchantments.Enchantment
import tools.impl.ToolsPlugin
import tools.impl.attribute.augments.Augment
import tools.impl.conf.attr.AugmentMenuConfig
import tools.impl.registry.AttributeRegistry
import tools.impl.tool.type.IAugmentedTool

class AugmentsMenuBase(
    private val plugin: ToolsPlugin,
    private val item: IAugmentedTool,
    private val config: AugmentMenuConfig
) : MenuAdapter<Augment>() {
    override val MENU_SIZE = 9 * 3
    override val BACK_SLOT = 21
    override val DONE_SLOT = 22
    override val NEXT_SLOT = 23

    private val registry: AttributeRegistry<Augment> = plugin.augmentRegistry

    override val menu: MenuList<Augment> = template.toList(queryItems(), transformer = {

        item.formatMenuItemFor(it.key).apply {
            editMeta { meta ->
                if (item.hasAugment(it.key))
                    meta.addEnchant(Enchantment.MENDING, 1, true)
            }
        }

        for (i in 0..item.openSlots)


    }) {
        title = config.augmentMenuTitle.format(StringPlaceholder.of("name", item.itemStack.itemMeta.displayName))

        next = buttons[NEXT_SLOT]!! to NEXT_SLOT
        back = buttons[BACK_SLOT]!! to BACK_SLOT

        fill = exclude(*frame())
        item.itemStack.itemMeta.persistentDataContainer

        frameWith(blank())

        select = { event, button, enchant ->
            getSubMenu(enchant).open(event.whoClicked)
        }
    }

    override fun queryItems(): MutableList<Augment> {
        val list = mutableListOf<Augment>()

        for (e in registry.allAttributes)
            if (e != null) {
                if (item.hasAugment(e.key))
                    list.add(e)
            }

        return list
    }
}

 */