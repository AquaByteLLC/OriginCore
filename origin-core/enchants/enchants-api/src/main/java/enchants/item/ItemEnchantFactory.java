package enchants.item;

import enchants.records.OriginEnchant;
import me.lucko.helper.item.ItemStackBuilder;

import java.util.ArrayDeque;

public interface ItemEnchantFactory extends EnchantFactory {
	ArrayDeque<OriginEnchant> getEnchants();


}
