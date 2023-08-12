package tools.impl.attribute.skins.impl;

import commons.events.impl.EventSubscriber;
import org.bukkit.inventory.ItemStack;
import tools.impl.attribute.AttributeKey;
import tools.impl.attribute.skins.Skin;
import tools.impl.conf.AttributeConfiguration;
import tools.impl.target.ToolTarget;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class ToolSkin implements Skin {

	private final AttributeKey key;
	private final EventSubscriber handle;
	private final AttributeConfiguration configuration;
	private final TimeUnit timeUnit;
	private final int time;
	private final List<ToolTarget> targets;
	private final List<String> information;
	private final String appliedLore;
	private final ItemStack skinStack;
	private final int modelData;

	public ToolSkin(AttributeKey key, EventSubscriber subscriber, AttributeConfiguration configuration,
	                TimeUnit timeUnit, int time, List<ToolTarget> targets,
	                List<String> information, String appliedLore, ItemStack skinStack, int modelData) {
		this.key = key;
		this.handle = subscriber;
		this.configuration = configuration;
		this.timeUnit = timeUnit;
		this.time = time;
		this.targets = targets;
		this.information = information;
		this.appliedLore = appliedLore;
		this.skinStack = skinStack;
		this.modelData = modelData;
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
	public List<String> getInformation() {
		return this.information;
	}

	@Override
	public String getAppliedLore() {
		return this.appliedLore;
	}

	@Override
	public int getModelData() {
		return this.modelData;
	}

	@Override
	public ItemStack getSkinStack() {
		return this.skinStack;
	}

	@Override
	public TimeUnit getTimeUnit() {
		return this.timeUnit;
	}

	@Override
	public long getAmount() {
		return this.time;
	}
}
