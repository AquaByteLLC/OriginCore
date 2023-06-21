package enchants.impl.type;

import commons.events.api.EventRegistry;
import commons.events.impl.EventSubscriber;
import enchants.impl.EnchantPlugin;
import enchants.impl.config.EnchantsConfig;
import enchants.records.OriginEnchant;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class OriginEnchantBuilder {
	private final String enchantName;
	private String lore;
	private String[] information;
	private int maxLevel;
	private ItemStack menuItem;
	private double startCost;
	private double maxCost;
	private double startChance;
	private double maxChance;
	private OriginEnchant.EnchantProgressionType chanceType;
	private OriginEnchant.EnchantProgressionType costType;

	public OriginEnchantBuilder(String enchantName) {
		this.enchantName = enchantName;

		final EnchantsConfig config = EnchantPlugin.getEnchantsConfig();
		final YamlConfiguration configuration = config.getConfiguration();

		EnchantsConfig.EnchantConfigPaths.createSections(enchantName, configuration, config);

		this.lore = config.getEnchantLore(enchantName);
		this.information = config.getDescription(enchantName);
		this.maxLevel = config.getMaxLevel(enchantName);
		this.menuItem = config.getMenuItem(enchantName);
		this.startCost = config.getStartCost(enchantName);
		this.maxCost = config.getMaxCost(enchantName);
		this.startChance = config.getStartChance(enchantName);
		this.maxChance = config.getMaxChance(enchantName);
		this.chanceType = config.getChanceType(enchantName);
		this.costType = config.getCostType(enchantName);
	}

	public OriginEnchantBuilder setLore(String lore) {
		this.lore = lore;
		return this;
	}

	public OriginEnchantBuilder setInfo(String[] information) {
		this.information = information;
		return this;
	}

	public OriginEnchantBuilder setMaxLevel(int maxLevel) {
		this.maxLevel = maxLevel;
		return this;
	}

	public OriginEnchantBuilder setMenuItem(ItemStack menuItem) {
		this.menuItem = menuItem;
		return this;
	}

	public OriginEnchantBuilder setStartCost(double startCost) {
		this.startCost = startCost;
		return this;
	}

	public OriginEnchantBuilder setMaxCost(double maxCost) {
		this.maxCost = maxCost;
		return this;
	}

	public OriginEnchantBuilder setStartChance(double startChance) {
		this.startChance = startChance;
		return this;
	}

	public OriginEnchantBuilder setMaxChance(double maxChance) {
		this.maxChance = maxChance;
		return this;
	}

	public OriginEnchantBuilder setChanceType(OriginEnchant.EnchantProgressionType type) {
		this.chanceType = type;
		return this;
	}

	public OriginEnchantBuilder setCostType(OriginEnchant.EnchantProgressionType type) {
		this.costType = type;
		return this;
	}

	public void build(JavaPlugin plugin, EventRegistry events, EventSubscriber handleEnchant) {
		handleEnchant.bind(plugin, events);
		new OriginEnchant(
				enchantName,
				information,
				lore,
				menuItem,
				maxLevel,
				startCost,
				maxCost,
				startChance,
				maxChance,
				chanceType,
				costType,
				handleEnchant
		).addToRegistry();
	}
}
