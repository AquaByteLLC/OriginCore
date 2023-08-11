package tools.impl.attribute.augments.impl;

import commons.events.impl.EventSubscriber;
import org.bukkit.inventory.ItemStack;
import tools.impl.attribute.AttributeKey;
import tools.impl.attribute.augments.Augment;
import tools.impl.attribute.augments.AugmentBuilder;
import tools.impl.conf.AttributeConfiguration;
import tools.impl.target.ToolTarget;

import java.util.Arrays;
import java.util.List;

public class ToolAugmentBuilder implements AugmentBuilder {
	private AttributeKey key;
	private EventSubscriber handle;
	private AttributeConfiguration configuration;
	private List<ToolTarget> targets;
	private long maxBoost, minBoost;
	private List<String> information;
	private String appliedLore;
	private ItemStack augmentStack;
	private final AugmentConfiguration config;

	public ToolAugmentBuilder(AttributeKey key) {
		this.key = key;
		this.config = new AugmentConfiguration(key);

		this.appliedLore = config.getAugmentAppliedLore();
		this.information = config.getDescription();
		this.maxBoost = config.getMaxBoost();
		this.augmentStack = config.getMenuItem();
		this.minBoost = config.getMinBoost();
	}

	@Override
	public AugmentBuilder setMinimumBoost(long boost) {
		this.minBoost = boost;
		return this;
	}

	@Override
	public AugmentBuilder setMaximumBoost(long boost) {
		this.maxBoost = boost;
		return this;
	}

	@Override
	public AugmentBuilder setInformation(String... information) {
		this.information = Arrays.stream(information).toList();
		return this;
	}

	@Override
	public AugmentBuilder setAugmentStack(ItemStack stack) {
		this.augmentStack = stack;
		return this;
	}

	@Override
	public AugmentBuilder setAppliedLore(String lore) {
		this.appliedLore = lore;
		return this;
	}

	@Override
	public Augment build(EventSubscriber handleEnchant, ToolTarget... targets) {
		return new ToolAugment(key, handleEnchant, configuration, Arrays.stream(targets).toList(), maxBoost, minBoost, information, appliedLore, augmentStack);
	}
}
