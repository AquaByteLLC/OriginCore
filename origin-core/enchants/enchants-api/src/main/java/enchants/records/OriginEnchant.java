package enchants.records;

import commons.entity.EntityEvent;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @param name Name of the enchantment
 * @param information Information about the enchant.
 * @param lore The lore which shows on the item.
 *
 * @param handleEnchant The executor for the enchant.
 */
public record OriginEnchant(String name,
                          String information,
                          String lore,
						  ItemStack menuItem,
                          EntityEvent<?> handleEnchant){}
