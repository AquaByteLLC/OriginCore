package tools.impl.attribute.skins.impl.types;

import commons.events.impl.EventSubscriber;
import commons.events.impl.impl.DetachedSubscriber;
import commons.util.StringUtil;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import tools.impl.ToolsPlugin;
import tools.impl.ability.builder.impl.AbilityCreator;
import tools.impl.ability.cache.impl.AttributeCache;
import tools.impl.ability.cache.types.PlayerBasedCachedAttribute;
import tools.impl.ability.cache.types.impl.PlayerCachedAttribute;
import tools.impl.attribute.AttributeKey;
import tools.impl.attribute.Consumer3;
import tools.impl.attribute.skins.Skin;
import tools.impl.attribute.skins.impl.ToolSkinFactory;
import tools.impl.registry.impl.BaseAttributeRegistry;
import tools.impl.target.ToolTarget;
import tools.impl.tool.impl.SkinnedTool;

import java.util.function.Consumer;

import static tools.impl.attribute.skins.impl.types.shelf.Shelves.flamingoShelf;

public enum GeneralSkinTypes implements AttributeKey {

	FLAMINGO_PICKAXE("FlamingoPickaxe",
			subscribe(BlockBreakEvent.class, (key, ctx, event) -> {
				final ItemStack playersItem = ctx.getPlayer().getInventory().getItemInMainHand();
				if (playersItem.getType().isAir()) return;
				final SkinnedTool item = new SkinnedTool(playersItem);
				final AttributeCache<Skin, PlayerBasedCachedAttribute<Skin>> cache = ToolsPlugin.getPlugin().getSkinCache();
				final Skin skin = ToolsPlugin.getPlugin().getSkinRegistry().getByKey(key);
				final PlayerCachedAttribute<Skin> playerCachedAttribute = new PlayerCachedAttribute<>(ctx.getPlayer(), skin);
				final Player player = ctx.getPlayer();

				final AbilityCreator<Skin, PlayerCachedAttribute<Skin>> abilityCreator = new AbilityCreator<>();

				abilityCreator.setExpiringShelf(flamingoShelf)
						.setExpirationHandler(($) -> {
							$.getPlayer().sendMessage("Seems that the ability ran out!");
						})
						.setWhileInCache(($) -> {
							$.getPlayer().sendMessage("Still in the cache so the event is functioning!");
						})
						.setWhileNotInCache(($) -> {
							$.getPlayer().sendMessage("Not inside the cache so whatever was meant to be done inside isnt!");
						})
						.create(BlockBreakEvent.class, playerCachedAttribute);

				if (item.activate(player, key)) {
					flamingoShelf.add(playerCachedAttribute);
					cache.add(playerCachedAttribute);
					event.getPlayer().sendMessage(StringUtil.colorize("&eWorking!"));
				}
			}), writer -> {
	}, ToolTarget.PICK);

	private final String name;
	private final NamespacedKey key;
	private final EventSubscriber subscriber;
	private final Consumer<FileConfiguration> writer;
	private final ToolTarget[] targets;

	GeneralSkinTypes(String name, EventSubscriber subscriber, Consumer<FileConfiguration> writer, ToolTarget... targets) {
		this.name = name;
		this.key = name2key(name);
		this.writer = writer;
		this.subscriber = subscriber;
		this.targets = targets;
	}

	@Override
	public NamespacedKey getNamespacedKey() {
		return key;
	}

	@Override
	public String getName() {
		return name;
	}

	public static NamespacedKey name2key(String name) {
		return new NamespacedKey("skins", "skin." + ChatColor.stripColor(name.toLowerCase()).replace(' ', '_'));
	}

	/* we perform miniscule amount of static abuse */

	private static int v = 0;

	private static <T> EventSubscriber subscribe(Class<T> clazz, Consumer3<T> cons) {
		final int vf = v++;
		return new DetachedSubscriber<>(clazz, (ctx, event) -> cons.consume(values()[vf], ctx, event));
	}

	public static @Nullable AttributeKey fromName(String name) {
		for (GeneralSkinTypes value : values())
			if (value.name.equalsIgnoreCase(name))
				return value;
		return null;
	}

	private static boolean init = false;

	public static void init(BaseAttributeRegistry<Skin> registry, ToolSkinFactory factory) {
		if (init)
			throw new UnsupportedOperationException();
		init = true;
		for (GeneralSkinTypes value : values())
			registry.register(factory.newAttributeBuilder(value).build(value.subscriber, value.writer, value.targets));
	}
}
