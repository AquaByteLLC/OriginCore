package tools.impl.attribute.skins.impl;

import commons.events.impl.EventSubscriber;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import tools.impl.ability.builder.impl.AbilityCreator;
import tools.impl.ability.cache.types.impl.PlayerCachedAttribute;
import tools.impl.attribute.AttributeKey;
import tools.impl.attribute.skins.Skin;
import tools.impl.attribute.skins.SkinBuilder;
import tools.impl.target.ToolTarget;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ToolSkinBuilder implements SkinBuilder {

	private final AttributeKey key;
	private EventSubscriber handle;
	private List<ToolTarget> targets;
	private int modelData;
	private List<String> information;
	private String appliedLore;
	private ItemStack skinStack;
	private final SkinConfiguration config;
	private TimeUnit timeUnit;
	private int time;

	public ToolSkinBuilder(AttributeKey key) {
		this.key = key;
		this.config = new SkinConfiguration(key);

		this.timeUnit = config.getCooldownUnit();
		this.time = config.getCooldownDuration();
		this.appliedLore = config.getAugmentAppliedLore();
		this.information = config.getDescription();
		this.skinStack = config.getMenuItem();
		this.modelData = config.getModelData();
	}

	@Override
	public SkinBuilder setAppliedLore(String appliedLore) {
		this.appliedLore = appliedLore;
		return this;
	}

	@Override
	public SkinBuilder setInfo(List<String> information) {
		this.information = information;
		return this;
	}

	@Override
	public SkinBuilder setSkinStack(ItemStack skinItem) {
		this.skinStack = skinItem;
		return this;
	}

	@Override
	public SkinBuilder setModelData(int modelData) {
		this.modelData = modelData;
		return this;
	}

	@Override
	public Skin build(EventSubscriber handleEnchant, BiConsumer<AbilityCreator<Skin, PlayerCachedAttribute<Skin>>, AttributeKey> creator, Consumer<FileConfiguration> writer, ToolTarget... targets) {
		this.config.writeAndSave(writer);
		creator.accept(new AbilityCreator<>(), this.key);
		return new ToolSkin(key, handleEnchant, config, timeUnit, time, List.of(targets), information, appliedLore, skinStack, modelData);
	}
}
