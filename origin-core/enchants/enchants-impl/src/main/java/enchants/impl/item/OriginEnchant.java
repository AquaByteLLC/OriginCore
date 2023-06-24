package enchants.impl.item;

import commons.events.impl.EventSubscriber;
import enchants.EnchantKey;
import enchants.conf.EnchantmentConfiguration;
import enchants.item.Enchant;
import enchants.item.EnchantTarget;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class OriginEnchant implements Enchant {

	private final EnchantKey          key;
	private final List<EnchantTarget> targets;
	private final List<String>        information;
	private final String lore;
	private final ItemStack menuItem;
	private final int maxLevel;
	private final double startCost;
	private final double maxCost;
	private final double startChance;
	private final double maxChance;
	private final ProgressionType chanceType;
	private final ProgressionType costType;
	private final EventSubscriber handleEnchant;
	private final EnchantmentConfiguration config;

	public OriginEnchant(EnchantKey key, List<EnchantTarget> targets,
						 List<String> information, String lore, ItemStack menuItem,
						 int maxLevel, double startCost, double maxCost, double startChance, double maxChance,
						 ProgressionType chanceType, ProgressionType costType,
						 EventSubscriber handleEnchant, EnchantmentConfiguration config) {
		this.key           = key;
		this.targets       = targets;
		this.information   = information;
		this.lore          = lore;
		this.menuItem      = menuItem;
		this.maxLevel      = maxLevel;
		this.startCost     = startCost;
		this.maxCost       = maxCost;
		this.startChance   = startChance;
		this.maxChance     = maxChance;
		this.chanceType    = chanceType;
		this.costType      = costType;
		this.handleEnchant = handleEnchant;
		this.config        = config;
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
			if(target.appliesToType(type))
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
	public int getMaxLevel() {
		return maxLevel;
	}

	@Override
	public double getStartCost() {
		return startCost;
	}

	@Override
	public double getMaxCost() {
		return maxCost;
	}

	@Override
	public double getStartChance() {
		return startChance;
	}

	@Override
	public double getMaxChance() {
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