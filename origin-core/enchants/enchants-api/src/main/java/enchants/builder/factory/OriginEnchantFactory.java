package enchants.builder.factory;

import enchants.builder.OriginEnchantBuilder;

public interface OriginEnchantFactory {
	static OriginEnchantBuilder create(String enchantName) {
		return new OriginEnchantBuilder(enchantName);
	}
}
