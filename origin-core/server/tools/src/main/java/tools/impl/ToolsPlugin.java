package tools.impl;

import co.aikar.commands.PaperCommandManager;
import commons.Commons;
import commons.events.api.EventRegistry;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;
import tools.impl.ability.cache.impl.AttributeCache;
import tools.impl.ability.cache.types.PlayerBasedCachedAttribute;
import tools.impl.attribute.AttributeKey;
import tools.impl.attribute.augments.Augment;
import tools.impl.attribute.augments.impl.ToolAugmentFactory;
import tools.impl.attribute.augments.impl.commands.AugmentCommands;
import tools.impl.attribute.augments.impl.listeners.AugmentEvents;
import tools.impl.attribute.augments.impl.types.GeneralAugmentTypes;
import tools.impl.attribute.enchants.Enchant;
import tools.impl.attribute.enchants.impl.CustomEnchantFactory;
import tools.impl.attribute.enchants.impl.commands.EnchantCommands;
import tools.impl.attribute.enchants.impl.types.GeneralEnchantTypes;
import tools.impl.attribute.skins.Skin;
import tools.impl.attribute.skins.impl.ToolSkinFactory;
import tools.impl.attribute.skins.impl.commands.SkinCommands;
import tools.impl.attribute.skins.impl.listeners.SkinEvents;
import tools.impl.attribute.skins.impl.types.GeneralSkinTypes;
import tools.impl.attribute.skins.impl.types.shelf.Shelves;
import tools.impl.registry.impl.BaseAttributeRegistry;
import tools.impl.tool.builder.typed.impl.UniqueItemBuilder;

public class ToolsPlugin extends JavaPlugin {

	@Getter
	private static ToolsPlugin plugin;
	@Getter
	private BaseAttributeRegistry<Enchant> enchantRegistry;
	@Getter
	private CustomEnchantFactory enchantFactory;

	@Getter
	private BaseAttributeRegistry<Augment> augmentRegistry;
	@Getter
	private ToolAugmentFactory augmentFactory;

	@Getter
	private BaseAttributeRegistry<Skin> skinRegistry;
	@Getter
	private ToolSkinFactory skinFactory;
	@Getter
	private AttributeCache<Skin, PlayerBasedCachedAttribute<Skin>> skinCache;

	@Override
	public void onEnable() {
		plugin = this;

		final EventRegistry registry = Commons.events();

		this.enchantRegistry = new BaseAttributeRegistry<>(registry);
		this.enchantFactory = new CustomEnchantFactory();

		this.augmentRegistry = new BaseAttributeRegistry<>(registry);
		this.augmentFactory = new ToolAugmentFactory();

		this.skinRegistry = new BaseAttributeRegistry<>(registry);
		this.skinFactory = new ToolSkinFactory();
		this.skinCache = new AttributeCache<>();

		GeneralSkinTypes.init(skinRegistry, skinFactory);
		GeneralAugmentTypes.init(augmentRegistry, augmentFactory);
		GeneralEnchantTypes.init(enchantRegistry, enchantFactory);
		Shelves.init();

		final PaperCommandManager commands = new PaperCommandManager(this);

		commands.getCommandContexts().registerContext(AttributeKey.class, c -> enchantRegistry.keyFromName(c.popFirstArg()));
		commands.getCommandCompletions().registerCompletion("enchants", c -> {
			Material holding = c.getPlayer().getInventory().getItemInMainHand().getType();
			return enchantRegistry.getAllAttributes().stream().filter(e -> e.targetsItem(holding)).map(Enchant::getKey).map(AttributeKey::getName).toList();
		});
		commands.registerCommand(new EnchantCommands());

		commands.getCommandContexts().registerContext(AttributeKey.class, c -> skinRegistry.keyFromName(c.popFirstArg()));
		commands.getCommandCompletions().registerCompletion("skins", c -> {
			Material holding = c.getPlayer().getInventory().getItemInMainHand().getType();
			return skinRegistry.getAllAttributes().stream().filter(e -> e.targetsItem(holding)).map(Skin::getKey).map(AttributeKey::getName).toList();
		});
		commands.registerCommand(new SkinCommands());

		commands.getCommandContexts().registerContext(AttributeKey.class, c -> augmentRegistry.keyFromName(c.popFirstArg()));
		commands.getCommandCompletions().registerCompletion("augments", c -> {
			Material holding = c.getPlayer().getInventory().getItemInMainHand().getType();
			return augmentRegistry.getAllAttributes().stream().filter(e -> e.targetsItem(holding)).map(Augment::getKey).map(AttributeKey::getName).toList();
		});
		commands.registerCommand(new AugmentCommands());

		new AugmentEvents(registry);
		new SkinEvents(registry);

		UniqueItemBuilder.getEvents().forEach(event -> event.bind(registry));
	}

	@Override
	public void onDisable() {

	}
}
