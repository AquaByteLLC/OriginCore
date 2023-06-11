package enchants.item;

import enchants.records.OriginEnchant;

import java.util.ArrayDeque;

public interface ItemEnchantFactory extends EnchantFactory {
	ArrayDeque<OriginEnchant> getEnchants();


}
