package enchants.impl.type;

public interface OriginEnchantFactory {
	static OriginEnchantBuilder create(String enchantName) {
		return new OriginEnchantBuilder(enchantName);
	}
}
