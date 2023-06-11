package enchants.records;

import enchants.config.EnchantConfig;
import lombok.Getter;

import java.util.*;

public class EnchantChance {
	@Getter private final HashMap<Integer, Double> buffToChance;

	/**
	 *
	 * @param enchantConfig configuration for the enchant.
	 */

	@SuppressWarnings("all")
	public EnchantChance(EnchantConfig enchantConfig) {
		this.buffToChance = new HashMap<>();

		enchantConfig.getConfig().getConfigurationSection("buff").getKeys(false).forEach($ -> {
			double configDouble = enchantConfig.getConfig().getDouble("buff." + $);
			buffToChance.put(Integer.valueOf($), configDouble);
		});

		double totalChance = buffToChance.values().stream().mapToDouble(Double::doubleValue).sum();

		if (totalChance > 100.0) {
			try {
				throw new Exception("The total chance for these buffs cannot exceed 100.0");
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
}
