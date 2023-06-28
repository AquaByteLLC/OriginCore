package blocks.impl;

import blocks.BlocksAPI;
import blocks.block.BlockRegistry;
import blocks.block.aspects.location.registry.BlockLocationRegistry;
import blocks.block.aspects.overlay.registry.OverlayLocationRegistry;
import blocks.block.aspects.regeneration.registry.RegenerationRegistry;
import blocks.block.illusions.IllusionsAPI;
import blocks.block.progress.SpeedAttribute;
import blocks.block.progress.registry.ProgressRegistry;
import blocks.block.regions.registry.RegionRegistry;
import blocks.impl.anim.item.BreakSpeed;
import blocks.impl.illusions.BlockIllusionRegistry;
import blocks.impl.illusions.IllusionFactoryImpl;
import blocks.impl.illusions.Illusions;
import blocks.impl.registry.*;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import commons.CommonsPlugin;
import commons.events.api.EventRegistry;
import lombok.Getter;
import me.vadim.util.conf.ResourceProvider;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class BlocksPlugin extends JavaPlugin implements ResourceProvider {
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

	private RegenerationRegistry regenerationRegistry;
	private IllusionsAPI illusions;
	private BlockRegistry blockRegistry;
	private OverlayLocationRegistry overlayLocationRegistry;
	private BlockLocationRegistry blockLocationRegistry;
	private ProgressRegistry progressRegistry;
	private SpeedAttribute speedAttribute;
	private RegionRegistry regionRegistry;

	@Override
	public void onEnable() {
		EventRegistry events = CommonsPlugin.commons().getEventRegistry();

		this.blockRegistry = new BlockRegistryImpl();
		this.overlayLocationRegistry = new OverlayRegistryImpl();
		this.illusions = new Illusions(new IllusionFactoryImpl(), new BlockIllusionRegistry(events));
		this.regenerationRegistry = new RegenerationRegistryImpl(this, illusions);
		this.blockLocationRegistry = new LocationRegistryImpl();
		this.progressRegistry = new ProgressRegistryImpl();
		this.speedAttribute = new BreakSpeed();
		this.regionRegistry = new RegionRegistryImpl();

		injector = Guice.createInjector(new BlockModule(new BlocksAPI(this, blockLocationRegistry, illusions, regenerationRegistry, blockRegistry, overlayLocationRegistry, progressRegistry, speedAttribute, regionRegistry)));
	}

	static class BlockModule extends AbstractModule {

		private final BlocksAPI blocksAPI;

		BlockModule(BlocksAPI blocks) {
			this.blocksAPI = blocks;
		}

		@Override
		protected void configure() {
			this.bind(BlocksAPI.class).toInstance(blocksAPI);
		}
	}
}
