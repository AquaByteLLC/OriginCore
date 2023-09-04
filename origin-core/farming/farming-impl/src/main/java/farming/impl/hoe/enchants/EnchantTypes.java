package farming.impl.hoe.enchants;

import blocks.BlocksAPI;
import blocks.block.aspects.regeneration.registry.RegenerationRegistry;
import blocks.block.builder.FixedAspectHolder;
import blocks.impl.BlocksPlugin;
import blocks.impl.data.account.BlockAccount;
import blocks.impl.events.BreakEvent;
import commons.events.impl.EventSubscriber;
import commons.events.impl.impl.DetachedSubscriber;
import commons.util.StringUtil;
import dev.oop778.shelftor.api.shelf.expiring.ExpiringShelf;
import farming.impl.action.FarmingActions;
import farming.impl.hoe.enchants.abilities.Abilities;
import farming.impl.hoe.enchants.entity.ExplosiveEntity;
import farming.impl.util.LocationUtil;
import me.lucko.helper.Schedulers;
import me.vadim.util.conf.wrapper.Placeholder;
import me.vadim.util.conf.wrapper.impl.StringPlaceholder;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import tools.impl.ToolsPlugin;
import tools.impl.ability.cache.types.impl.PlayerCachedAttribute;
import tools.impl.attribute.AttributeKey;
import tools.impl.attribute.Consumer3;
import tools.impl.attribute.enchants.Enchant;
import tools.impl.attribute.enchants.impl.CustomEnchantFactory;
import tools.impl.conf.AttributeConfiguration;
import tools.impl.registry.AttributeRegistry;
import tools.impl.target.ToolTarget;
import tools.impl.tool.impl.EnchantedTool;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

;

public enum EnchantTypes implements AttributeKey {

	EXPLOSION("Explosion",
			subscribe(BreakEvent.class, (key, ctx, event) -> {
				if (event.isCalledFromEnchant()) return;

				final ItemStack playersItem = ctx.getPlayer().getInventory().getItemInMainHand();
				final BlocksPlugin plugin = BlocksPlugin.get().getInstance(BlocksPlugin.class);

				if (playersItem.getType().isAir()) return;
				final EnchantedTool item = new EnchantedTool(playersItem);
				final Player player = ctx.getPlayer();
				final Block block = player.getTargetBlockExact(5);
				final FileConfiguration configuration = AttributeConfiguration.getConfigurations().get(key);

				if (block == null) return;

				if (BlocksAPI.inRegion(block.getLocation())) {
					final BlockAccount account = plugin.getAccounts().getAccount(player);
					final RegenerationRegistry regenerationRegistry = account.getRegenerationRegistry();
					final int radius = configuration.getInt("ExplosionRadius");
					final Placeholder pl = StringPlaceholder.builder()
							.set("name", key.getName())
							.set("chance", item.getChance(key))
							.set("radius", radius)
							.build();

					if (item.activate(key)) {
						FarmingActions.send(FarmingActions.EXPLOSIVE_ENCHANT_ACTIVATION, player, pl);
						new ExplosiveEntity(((CraftWorld) player.getWorld()).getHandle(), player, block, entity -> {
							Schedulers.bukkit().runTask(plugin, () -> {

								final List<FixedAspectHolder> aspectList = LocationUtil.getBlocks(block, radius);

								// START
								aspectList.forEach(holder -> {
									if (holder.getBlock().getBlockData() instanceof Ageable ageable)
										if (BlocksAPI.inRegion(block.getLocation()))
											if (ageable.getAge() == ageable.getMaximumAge())
												new BreakEvent("farming", holder.getBlock(), player, true).callEvent();
								});
								// END

							});
						});
					}
				}
			}), writer -> {
		writer.set("ExplosionRadius", 4);
	}, ToolTarget.HOE),
	FIRE_FEET("FireFeet", subscribe(BreakEvent.class, (key, ctx, event) -> {
		if (event.isCalledFromEnchant()) return;

		final ItemStack playersItem = ctx.getPlayer().getInventory().getItemInMainHand();
		final BlocksPlugin plugin = BlocksPlugin.get().getInstance(BlocksPlugin.class);

		if (playersItem.getType().isAir()) return;
		final EnchantedTool item = new EnchantedTool(playersItem);
		final Player player = ctx.getPlayer();
		final Block block = player.getTargetBlockExact(5);
		final FileConfiguration configuration = AttributeConfiguration.getConfigurations().get(key);

		if (block == null) return;
		final Placeholder pl = StringPlaceholder.builder()
				.set("name", key.getName())
				.set("chance", item.getChance(key))
				.set("length", configuration.getInt("Ability.Length"))
				.set("unit", StringUtil.convertToUserFriendlyCase(configuration.getString("Ability.Unit")))
				.build();

		if (BlocksAPI.inRegion(block.getLocation())) {
			final BlockAccount account = plugin.getAccounts().getAccount(player);
			final RegenerationRegistry regenerationRegistry = account.getRegenerationRegistry();
			final PlayerCachedAttribute<Enchant> playerCachedAttribute = PlayerCachedAttribute.of(Enchant.class, player, ToolsPlugin.getPlugin().getEnchantRegistry().getByKey(key));
			if (item.activate(key)) {
				FarmingActions.send(FarmingActions.FIRE_FEET_ENCHANT_ACTIVATION, player, pl);
				final ExpiringShelf<PlayerCachedAttribute<Enchant>> cache = Abilities.FIRE_FEET.getAbilityCreator().getCache();
				System.out.println(block.getLocation() + " : Block Location");
				cache.remove(playerCachedAttribute);
				cache.add(playerCachedAttribute);
			}
		}
	}), writer -> {
		if (writer.isConfigurationSection("Ability")) return;
		writer.set("Ability.Length", 10);
		writer.set("Ability.Unit", TimeUnit.SECONDS.toString());
	}, ToolTarget.HOE);
	private final String name;
	private final NamespacedKey key;
	private final EventSubscriber subscriber;
	private final ToolTarget[] targets;
	private final Consumer<FileConfiguration> writer;

	EnchantTypes(String name, EventSubscriber subscriber, Consumer<FileConfiguration> writer, ToolTarget... targets) {
		this.name = name;
		this.key = name2key(name);
		this.subscriber = subscriber;
		this.targets = targets;
		this.writer = writer;
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
		return new NamespacedKey("enchants", "enchant." + ChatColor.stripColor(name.toLowerCase()).replace(' ', '_'));
	}

	/* we perform miniscule amount of static abuse */

	private static int v = 0;

	private static <T> EventSubscriber subscribe(Class<T> clazz, Consumer3<T> cons) {
		final int vf = v++;
		return new DetachedSubscriber<>(clazz, (ctx, event) -> cons.consume(values()[vf], ctx, event));
	}

	public static @Nullable AttributeKey fromName(String name) {
		for (EnchantTypes value : values())
			if (value.name.equalsIgnoreCase(name))
				return value;
		return null;
	}

	private static boolean init = false;

	public static void init(AttributeRegistry<Enchant> registry, CustomEnchantFactory factory) {
		if (init)
			throw new UnsupportedOperationException();
		init = true;
		for (EnchantTypes value : values())
			registry.register(factory.newAttributeBuilder(value).build(value.subscriber, value.writer, value.targets));
	}

}
