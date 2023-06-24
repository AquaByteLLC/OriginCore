package enchants.impl.item;

import commons.events.impl.EventSubscriber;
import enchants.EnchantAPI;
import enchants.EnchantKey;
import enchants.config.EnchantmentConfiguration;
import enchants.item.Enchant;
import enchants.item.EnchantBuilder;
import lombok.SneakyThrows;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class OriginEnchantBuilder implements EnchantBuilder {

	private final EnchantKey key;
	private String lore;
	private List<String> information;
	private int maxLevel;
	private ItemStack menuItem;
	private double startCost;
	private double maxCost;
	private double startChance;
	private double maxChance;
	private Enchant.ProgressionType chanceType;
	private Enchant.ProgressionType costType;
	private final EnchantmentConfiguration config;

	@SneakyThrows
	public OriginEnchantBuilder(EnchantKey key) {
		this.key = key;
		String enchantName = key.getName();

		this.config = new EnchantmentConfiguration(EnchantAPI.get().getInstance(JavaPlugin.class), enchantName);

		this.lore        = config.getEnchantLore(enchantName);
		this.information = config.getDescription(enchantName);
		this.maxLevel    = config.getMaxLevel(enchantName);
		this.menuItem    = config.getMenuItem(enchantName);
		this.startCost   = config.getStartCost(enchantName);
		this.maxCost     = config.getMaxCost(enchantName);
		this.startChance = config.getStartChance(enchantName);
		this.maxChance   = config.getMaxChance(enchantName);
		this.chanceType  = config.getChanceType(enchantName);
		this.costType    = config.getCostType(enchantName);
	}

	public EnchantBuilder setLore(String lore) {
		this.lore = lore;
		return this;
	}

	public EnchantBuilder setInfo(List<String> information) {
		this.information = information;
		return this;
	}

	public EnchantBuilder setMaxLevel(int maxLevel) {
		this.maxLevel = maxLevel;
		return this;
	}

	public EnchantBuilder setMenuItem(ItemStack menuItem) {
		this.menuItem = menuItem;
		return this;
	}

	public EnchantBuilder setStartCost(double startCost) {
		this.startCost = startCost;
		return this;
	}

	public EnchantBuilder setMaxCost(double maxCost) {
		this.maxCost = maxCost;
		return this;
	}

	public EnchantBuilder setStartChance(double startChance) {
		this.startChance = startChance;
		return this;
	}

	public EnchantBuilder setMaxChance(double maxChance) {
		this.maxChance = maxChance;
		return this;
	}

	public EnchantBuilder setChanceType(Enchant.ProgressionType type) {
		this.chanceType = type;
		return this;
	}

	public EnchantBuilder setCostType(Enchant.ProgressionType type) {
		this.costType = type;
		return this;
	}

	public Enchant build(EventSubscriber handleEnchant) {
		return new OriginEnchant(
				key,
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
				handleEnchant,
				config
		);
	}
}
