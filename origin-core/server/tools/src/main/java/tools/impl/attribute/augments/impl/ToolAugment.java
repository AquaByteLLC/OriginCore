package tools.impl.attribute.augments.impl;

import commons.events.impl.EventSubscriber;
import org.bukkit.inventory.ItemStack;
import tools.impl.attribute.AttributeKey;
import tools.impl.attribute.augments.Augment;
import tools.impl.conf.AttributeConfiguration;
import tools.impl.target.ToolTarget;

import java.util.List;

public class ToolAugment implements Augment {
	private final AttributeKey key;
	private final EventSubscriber handle;
	private final AttributeConfiguration configuration;
	private final List<ToolTarget> targets;
	private final long maxBoost, minBoost;
	private final List<String> information;
	private final String appliedLore;
	private final ItemStack augmentStack;

	public ToolAugment(AttributeKey key, EventSubscriber subscriber, AttributeConfiguration configuration, List<ToolTarget> targets, long maxBoost, long minBoost, List<String> information, String appliedLore, ItemStack stack) {
		this.key = key;
		this.handle = subscriber;
		this.configuration = configuration;
		this.targets = targets;
		this.maxBoost = maxBoost;
		this.minBoost = minBoost;
		this.information = information;
		this.appliedLore = appliedLore;
		this.augmentStack = stack;
	}

	@Override
	public AttributeKey getKey() {
		return this.key;
	}

	@Override
	public EventSubscriber getHandle() {
		return this.handle;
	}

	@Override
	public AttributeConfiguration getConfig() {
		return this.configuration;
	}

	@Override
	public List<ToolTarget> getAttributeTargets() {
		return this.targets;
	}

	@Override
	public long getMinimumBoost() {
		return this.minBoost;
	}

	@Override
	public long getMaximumBoost() {
		return this.maxBoost;
	}

	@Override
	public String getAppliedLore() {
		return this.appliedLore;
	}

	@Override
	public List<String> getInformation() {
		return this.information;
	}

	@Override
	public ItemStack getAugmentStack() {
		return this.augmentStack;
	}
}
