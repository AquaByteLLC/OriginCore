package menu.augment

import me.vadim.util.conf.wrapper.impl.StringPlaceholder
import me.vadim.util.menu.MenuList
import me.vadim.util.menu.frame
import me.vadim.util.menu.frameWith
import me.vadim.util.menu.toList
import menu.AttributeMenu
import org.bukkit.enchantments.Enchantment
import tools.impl.ToolsPlugin
import tools.impl.attribute.augments.Augment
import tools.impl.tool.type.IAugmentedTool

class AugmentsMenuBase(plugin: ToolsPlugin, item: IAugmentedTool) : AttributeMenu<Augment, IAugmentedTool>(plugin, plugin.augmentRegistry, item) {


    override val menu: MenuList<Augment> = template.toList(queryItems(), transformer = {
        item.formatMenuItemFor(it.key).apply {
            editMeta { meta ->
                if (item.hasAugment(it.key))
                    meta.addEnchant(Enchantment.MENDING, 1, true)
            }
        }

//        for (i in 0..item.openSlots)


    }) {
        title = config.augmentMenuTitle.format(StringPlaceholder.of("name", item.itemStack.itemMeta.displayName))

        next = buttons[NEXT_SLOT]!! to NEXT_SLOT
        back = buttons[BACK_SLOT]!! to BACK_SLOT

        fill = exclude(*frame())
        item.itemStack.itemMeta.persistentDataContainer

        frameWith(blank())

        select = { event, button, augment ->
//            getSubMenu(augment).open(event.whoClicked)
        }
    }.apply { generate() }

    override fun test(e: Augment, item: IAugmentedTool): Boolean = item.hasAugment(e.key)

}