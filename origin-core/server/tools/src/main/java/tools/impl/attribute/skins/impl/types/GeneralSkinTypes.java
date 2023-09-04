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
import tools.impl.registry.AttributeRegistry;
import tools.impl.target.ToolTarget;
import tools.impl.tool.impl.SkinnedTool;

import java.util.function.BiConsumer;
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
				final PlayerCachedAttribute<Skin> playerCachedAttribute = PlayerCachedAttribute.of(Skin.class, ctx.getPlayer(), skin);
				System.out.println(playerCachedAttribute);
				final Player player = ctx.getPlayer();

				if (item.activate(playerCachedAttribute, key)) {
					flamingoShelf.add(playerCachedAttribute);
					cache.add(playerCachedAttribute);
					System.out.println(cache.getCache().contains(playerCachedAttribute) + " YES OR NO");
					event.getPlayer().sendMessage(StringUtil.colorize("&eWorking! Skins"));
				}
			}), (creator, key) -> creator.setExpiringShelf(flamingoShelf)
			.setExpirationHandler(($) -> $.getPlayer().sendMessage("Seems that the ability ran out!"))
			.setWhileInCache(BlockBreakEvent.class, ($) -> {
				final Skin skin = ToolsPlugin.getPlugin().getSkinRegistry().getByKey(key);
				final PlayerCachedAttribute<Skin> playerCachedAttribute = PlayerCachedAttribute.of(Skin.class, $.getPlayer(), skin);

				if (flamingoShelf.contains(playerCachedAttribute)) {
					flamingoShelf.getSettings().expiringPolicies();
					$.getPlayer().sendMessage("Inside cache ");
				}
			})
			.setWhileNotInCache(BlockBreakEvent.class, ($) -> {
				final Skin skin = ToolsPlugin.getPlugin().getSkinRegistry().getByKey(key);
				final PlayerCachedAttribute<Skin> playerCachedAttribute = PlayerCachedAttribute.of(Skin.class, $.getPlayer(), skin);

				if (!flamingoShelf.contains(playerCachedAttribute)) {
					$.getPlayer().sendMessage("Not inside the cache so whatever was meant to be done inside isnt!");
				}
			}).build(),
			writer -> {

			}, ToolTarget.all());

	private final String name;
	private final NamespacedKey key;
	private final EventSubscriber subscriber;
	private final Consumer<FileConfiguration> writer;
	private final ToolTarget[] targets;
	private final BiConsumer<AbilityCreator<Skin, PlayerCachedAttribute<Skin>>, AttributeKey> creatorConsumer;

	GeneralSkinTypes(String name, EventSubscriber subscriber, BiConsumer<AbilityCreator<Skin, PlayerCachedAttribute<Skin>>, AttributeKey> creatorConsumer, Consumer<FileConfiguration> writer, ToolTarget... targets) {
		this.name = name;
		this.key = name2key(name);
		this.writer = writer;
		this.subscriber = subscriber;
		this.targets = targets;
		this.creatorConsumer = creatorConsumer;
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

	public static void init(AttributeRegistry<Skin> registry, ToolSkinFactory factory) {
		if (init)
			throw new UnsupportedOperationException();
		init = true;
		for (GeneralSkinTypes value : values())
			registry.register(factory.newAttributeBuilder(value).build(value.subscriber, value.creatorConsumer, value.writer, value.targets));
	}
}
