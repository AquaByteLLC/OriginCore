package tools.impl.attribute.enchants.impl;

import commons.events.impl.EventSubscriber;
import lombok.SneakyThrows;
import org.bukkit.inventory.ItemStack;
import tools.impl.attribute.AttributeKey;
import tools.impl.attribute.enchants.Enchant;
import tools.impl.attribute.enchants.EnchantBuilder;
import tools.impl.progression.ProgressionType;
import tools.impl.target.ToolTarget;

import java.math.BigDecimal;
import java.util.List;

public class CustomEnchantBuilder implements EnchantBuilder {
	private final AttributeKey key;
	private ToolTarget[] targets;
	private String lore;
	private List<String> information;
	private int maxLevel;
	private ItemStack menuItem;
	private BigDecimal startCost;
	private BigDecimal maxCost;
	private BigDecimal startChance;
	private BigDecimal maxChance;
	private ProgressionType chanceType;
	private ProgressionType costType;
	private final EnchantConfiguration config;

	@SneakyThrows
	public CustomEnchantBuilder(AttributeKey key) {
		this.key = key;
		this.config = new EnchantConfiguration(key);

		this.lore = config.getEnchantLore();
		this.information = config.getDescription();
		this.maxLevel = config.getMaxLevel();
		this.menuItem = config.getMenuItem();
		this.startCost = config.getStartCost();
		this.maxCost = config.getMaxCost();
		this.startChance = config.getStartChance();
		this.maxChance = config.getMaxChance();
		this.chanceType = config.getChanceType();
		this.costType = config.getCostType();
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

	public EnchantBuilder setStartCost(BigDecimal startCost) {
		this.startCost = startCost;
		return this;
	}

	public EnchantBuilder setMaxCost(BigDecimal maxCost) {
		this.maxCost = maxCost;
		return this;
	}

	public EnchantBuilder setStartChance(BigDecimal startChance) {
		this.startChance = startChance;
		return this;
	}

	public EnchantBuilder setMaxChance(BigDecimal maxChance) {
		this.maxChance = maxChance;
		return this;
	}

	public EnchantBuilder setChanceType(ProgressionType type) {
		this.chanceType = type;
		return this;
	}

	public EnchantBuilder setCostType(ProgressionType type) {
		this.costType = type;
		return this;
	}

	public Enchant build(EventSubscriber handleEnchant, ToolTarget... targets) {
		return new CustomEnchant(
				key,
				List.of(targets),
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
