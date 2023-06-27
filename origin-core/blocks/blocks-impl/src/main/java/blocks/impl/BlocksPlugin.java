package blocks.impl;

import blocks.BlocksAPI;
import blocks.block.BlockRegistry;
import blocks.block.aspects.illusions.registry.IllusionRegistry;
import blocks.block.aspects.location.registry.BlockLocationRegistry;
import blocks.block.aspects.overlay.registry.OverlayLocationRegistry;
import blocks.block.aspects.regeneration.registry.RegenerationRegistry;
import blocks.block.progress.SpeedAttribute;
import blocks.block.progress.registry.ProgressRegistry;
import blocks.block.regions.registry.RegionRegistry;
import blocks.impl.anim.item.BreakSpeed;
import blocks.impl.handler.BlockHandler;
import blocks.impl.registry.*;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import commons.events.api.EventRegistry;
import lombok.Getter;
import me.vadim.util.conf.LiteConfig;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.Consumer;

@Getter
public class BlocksPlugin {
	private static Injector injector;

	public static Injector get() {
		if (injector == null) {
			try {
				throw new Exception("The BlocksAPI hasn't been initialized anywhere. Create a new instance of the BlocksAPI class in the 'onEnable' method.");
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return injector;
	}

	private final RegenerationRegistry regenerationRegistry;
	private final IllusionRegistry illusionRegistry;
	private final BlockRegistry blockRegistry;
	private final OverlayLocationRegistry overlayLocationRegistry;
	private final BlockLocationRegistry blockLocationRegistry;
	private final ProgressRegistry progressRegistry;
	private final SpeedAttribute speedAttribute;
	private final RegionRegistry regionRegistry;
	public BlocksPlugin(JavaPlugin plugin, LiteConfig lfc, EventRegistry registry) {
		this.blockRegistry = new BlockRegistryImpl();
		this.overlayLocationRegistry = new OverlayRegistryImpl();
		this.illusionRegistry = new IllusionRegistryImpl(registry, overlayLocationRegistry);
		this.regenerationRegistry = new RegenerationRegistryImpl(plugin, lfc, illusionRegistry);
		this.blockLocationRegistry = new LocationRegistryImpl();
		this.progressRegistry = new ProgressRegistryImpl();
		this.speedAttribute = new BreakSpeed();
		this.regionRegistry = new RegionRegistryImpl();
		injector = Guice.createInjector(new BlockModule(new BlocksAPI(plugin, lfc, blockLocationRegistry, illusionRegistry, regenerationRegistry, blockRegistry, overlayLocationRegistry, progressRegistry, speedAttribute, regionRegistry)));
	}

	public static void createHandler(Consumer<YamlConfiguration> configurationConsumer) {
		BlockHandler.init(configurationConsumer);
	}

	static class BlockModule extends AbstractModule {

		private final BlocksAPI blocksAPI;

		public BlockModule(BlocksAPI blocks) {
			this.blocksAPI = blocks;
		}

		@Override
		protected void configure() {
			this.bind(BlocksAPI.class).toInstance(blocksAPI);
		}
	}
}
