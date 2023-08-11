package tools.impl.attribute.enchants.impl;

import commons.events.impl.EventSubscriber;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import tools.impl.attribute.AttributeKey;
import tools.impl.attribute.enchants.Enchant;
import tools.impl.conf.AttributeConfiguration;
import tools.impl.progression.ProgressionType;
import tools.impl.target.ToolTarget;

import java.math.BigDecimal;
import java.util.List;

public class CustomEnchant implements Enchant {
	private final AttributeKey key;
	private final List<ToolTarget> targets;
	private final List<String> information;
	private final String lore;
	private final ItemStack menuItem;
	private final long maxLevel;
	private final BigDecimal startCost;
	private final BigDecimal maxCost;
	private final BigDecimal startChance;
	private final BigDecimal maxChance;
	private final ProgressionType chanceType;
	private final ProgressionType costType;
	private final EventSubscriber handleEnchant;
	private final AttributeConfiguration config;

	public CustomEnchant(AttributeKey key, List<ToolTarget> targets,
	                     List<String> information, String lore, ItemStack menuItem,
	                     long maxLevel, BigDecimal startCost, BigDecimal maxCost, BigDecimal startChance,
	                     BigDecimal maxChance, ProgressionType chanceType, ProgressionType costType,
	                     EventSubscriber handleEnchant, AttributeConfiguration config) {
		this.key = key;
		this.targets = targets;
		this.information = information;
		this.lore = lore;
		this.menuItem = menuItem;
		this.maxLevel = maxLevel;
		this.startCost = startCost;
		this.maxCost = maxCost;
		this.startChance = startChance;
		this.maxChance = maxChance;
		this.chanceType = chanceType;
		this.costType = costType;
		this.handleEnchant = handleEnchant;
		this.config = config;
	}

	@Override
	public AttributeKey getKey() {
		return key;
	}

	@Override
	public List<ToolTarget> getAttributeTargets() {
		return targets;
	}

	@Override
	public boolean targetsItem(Material type) {
		for (ToolTarget target : targets)
			if (target.appliesToType(type))
				return true;
		return false;
	}

	@Override
	public List<String> getInformation() {
		return information;
	}

	@Override
	public String getLore() {
		return lore;
	}

	@Override
	public ItemStack getMenuItem() {
		return menuItem.clone();
	}

	@Override
	public long getMaxLevel() {
		return maxLevel;
	}

	@Override
	public BigDecimal getStartCost() {
		return startCost;
	}

	@Override
	public BigDecimal getMaxCost() {
		return maxCost;
	}

	@Override
	public BigDecimal getStartChance() {
		return startChance;
	}

	@Override
	public BigDecimal getMaxChance() {
		return maxChance;
	}

	@Override
	public ProgressionType getChanceType() {
		return chanceType;
	}

	@Override
	public ProgressionType getCostType() {
		return costType;
	}

	@Override
	public EventSubscriber getHandle() {
		return handleEnchant;
	}

	@Override
	public AttributeConfiguration getConfig() {
		return config;
	}
}
