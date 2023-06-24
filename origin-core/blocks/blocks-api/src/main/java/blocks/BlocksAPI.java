package blocks;

import blocks.anim.BlockAnimHelper;
import blocks.factory.interfaces.OriginBlock;
import blocks.region.OriginRegion;
import blocks.registry.BlockRegistry;
import blocks.registry.ProgressRegistry;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import commons.CommonsPlugin;
import commons.events.api.EventRegistry;
import commons.events.impl.bukkit.BukkitEventSubscriber;
import me.lucko.helper.bossbar.BossBar;
import me.lucko.helper.bossbar.BossBarColor;
import me.lucko.helper.bossbar.BossBarStyle;
import me.lucko.helper.bossbar.BukkitBossBarFactory;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Objects;

public final class BlocksAPI {
	private static Injector injector;

	public static Injector get() {
		if (injector == null) {
			try {
				throw new Exception("The EnchantAPI hasn't been initialized anywhere. Create a new instance of the EnchantAPI class in the 'onEnable' method.");
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return injector;
	}

	private static YamlConfiguration generalConfig;
	private static BlockRegistry blockRegistry;
	private static ProgressRegistry progressRegistry;
	public static final BossBar progressBossBar = new BukkitBossBarFactory(Bukkit.getServer()).newBossBar()
			.color(BossBarColor.PINK)
			.style(BossBarStyle.SOLID);


	public static YamlConfiguration getGeneralConfig() {
		if (generalConfig == null) throw new RuntimeException("The BlocksAPI hasn't been initialized anywhere. Create a new instance of the BlocksAPI class in the 'onEnable' method.");
		return generalConfig;
	}

	public static BlockRegistry getBlockRegistry() {
		if (blockRegistry == null) throw new RuntimeException("The BlocksAPI hasn't been initialized anywhere. Create a new instance of the BlocksAPI class in the 'onEnable' method.");
		return blockRegistry;
	}

	public static ProgressRegistry getProgressRegistry() {
		if (progressRegistry == null) throw new RuntimeException("The BlocksAPI hasn't been initialized anywhere. Create a new instance of the BlocksAPI class in the 'onEnable' method.");
		return progressRegistry;
	}



	public static OriginBlock getBlock(Location location) {
		OriginRegion.RegionInstance instance = OriginRegion.getInstance(location);

		if (instance == null) return null;

		for (String blockKey : blockRegistry.getBlocks().keySet()) {
			if (Objects.equals(instance.getBlock(), blockKey)) {
				return blockRegistry.getBlocks().get(instance.getBlock());
			}
		}
		return null;
	}


	public BlocksAPI(JavaPlugin javaPlugin) {
		injector = Guice.createInjector(new BlocksModule(javaPlugin,  BlockRegistry.RandomBlockFactory.create()));
		blockRegistry = new BlockRegistry();
		progressRegistry = new ProgressRegistry();
		generalConfig = YamlConfiguration.loadConfiguration(new File(javaPlugin.getDataFolder(), "general.yml"));
		final EventRegistry eventRegistry = CommonsPlugin.commons().getEventRegistry();
		new BukkitEventSubscriber<>(BlockDamageEvent.class, (context, event) -> {
			BlockAnimHelper.BlockAnimation blockAnimation = new BlockAnimHelper.BlockAnimation(event.getPlayer(), event.getBlock(), 0.05f);
			blockAnimation.handleAnimation();
		}).bind(eventRegistry);
	}

	static class BlocksModule extends AbstractModule {
		private final JavaPlugin plugin;
		private final BlockRegistry.RandomBlock picker;

		BlocksModule(JavaPlugin plugin, BlockRegistry.RandomBlock picker) {
			this.plugin = plugin;
			this.picker = picker;
		}

		protected void configure() {
			this.bind(JavaPlugin.class).toInstance(plugin);
			this.bind(BlockRegistry.RandomBlock.class).toInstance(picker);
		}
	}
}
