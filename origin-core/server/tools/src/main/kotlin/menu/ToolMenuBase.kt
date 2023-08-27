package menu

import commons.menu.MenuAdapter
import me.vadim.util.menu.*
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import tools.impl.ToolsPlugin
import tools.impl.attribute.augments.Augment
import tools.impl.attribute.enchants.Enchant
import tools.impl.attribute.skins.Skin
import tools.impl.conf.MenuConfig
import tools.impl.registry.AttributeRegistry
import tools.impl.tool.impl.EnchantedTool

@Suppress("UNUSED_ANONYMOUS_PARAMETER")
class ToolMenuBase(
    private val plugin: ToolsPlugin,
    private val item: ItemStack,
    private val isEnchant: Boolean,
    private val isAugment: Boolean,
    private val isSkin: Boolean,
    private val config: MenuConfig
) : MenuAdapter<ItemStack>() {

    override val MENU_SIZE = 9 * 4
    override val BACK_SLOT = 30
    override val DONE_SLOT = 31
    override val NEXT_SLOT = 32
    override val menu: MenuList<ItemStack>
        get() = TODO("Not yet implemented")

    override fun queryItems(): MutableList<ItemStack> {
        TODO("Not yet implemented")
    }


}