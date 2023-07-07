package enchants.impl.item;

import commons.events.impl.EventSubscriber;
import enchants.EnchantKey;
import enchants.conf.EnchantmentConfiguration;
import enchants.item.Enchant;
import enchants.item.EnchantTarget;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.util.List;

public class OriginEnchant implements Enchant {

	private final EnchantKey key;
	private final List<EnchantTarget> targets;
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
	private final EnchantmentConfiguration config;

	public OriginEnchant(EnchantKey key, List<EnchantTarget> targets,
	                     List<String> information, String lore, ItemStack menuItem,
	                     long maxLevel, BigDecimal startCost, BigDecimal maxCost, BigDecimal startChance, BigDecimal maxChance,
	                     ProgressionType chanceType, ProgressionType costType,
	                     EventSubscriber handleEnchant, EnchantmentConfiguration config) {
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
	public EnchantKey getKey() {
		return key;
	}

	@Override
	public List<EnchantTarget> getEnchantTargets() {
		return targets;
	}

	@Override
	public boolean targetsItem(Material type) {
		for (EnchantTarget target : targets)
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
	public EventSubscriber getHandleEnchant() {
		return handleEnchant;
	}

	@Override
	public EnchantmentConfiguration getConfig() {
		return config;
	}

}