package blocks;

import blocks.block.BlockRegistry;
import blocks.block.aspects.illusions.registry.IllusionRegistry;
import blocks.block.aspects.location.registry.BlockLocationRegistry;
import blocks.block.aspects.overlay.registry.OverlayLocationRegistry;
import blocks.block.aspects.regeneration.registry.RegenerationRegistry;
import blocks.block.builder.OriginBlockBuilder;
import blocks.block.progress.SpeedAttribute;
import blocks.block.progress.registry.ProgressRegistry;
import blocks.block.regions.OriginRegion;
import blocks.block.regions.registry.RegionRegistry;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.vadim.util.conf.LiteConfig;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

public final class BlocksAPI {
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

	private static RegenerationRegistry regenerationRegistry;
	private static BlockRegistry blockRegistry;
	private static BlockLocationRegistry locationRegistry;
	private static IllusionRegistry illusionRegistry;
	private static OverlayLocationRegistry overlayRegistry;
	private static ProgressRegistry progressRegistry;
	private static SpeedAttribute speedAttribute;
	private static RegionRegistry regionRegistry;


	public static ProgressRegistry getProgressRegistry() {
		if (progressRegistry == null) throw new RuntimeException("The BlocksAPI hasn't been initialized anywhere. Create a new instance of the BlocksAPI class in the 'onEnable' method.");
		return progressRegistry;
	}

	public static RegionRegistry getRegionRegistry() {
		if (regionRegistry == null) throw new RuntimeException("The BlocksAPI hasn't been initialized anywhere. Create a new instance of the BlocksAPI class in the 'onEnable' method.");
		return regionRegistry;
	}

	public static SpeedAttribute getSpeedAttribute() {
		if (speedAttribute == null) throw new RuntimeException("The BlocksAPI hasn't been initialized anywhere. Create a new instance of the BlocksAPI class in the 'onEnable' method.");
		return speedAttribute;
	}

	public static BlockLocationRegistry getLocationRegistry() {
		if (locationRegistry == null) throw new RuntimeException("The BlocksAPI hasn't been initialized anywhere. Create a new instance of the BlocksAPI class in the 'onEnable' method.");
		return locationRegistry;
	}

	public static RegenerationRegistry getRegenerationRegistry() {
		if (regenerationRegistry == null) throw new RuntimeException("The BlocksAPI hasn't been initialized anywhere. Create a new instance of the BlocksAPI class in the 'onEnable' method.");
		return regenerationRegistry;
	}

	public static OverlayLocationRegistry getOverlayLocationRegistry() {
		if (overlayRegistry == null) throw new RuntimeException("The BlocksAPI hasn't been initialized anywhere. Create a new instance of the BlocksAPI class in the 'onEnable' method.");
		return overlayRegistry;
	}


	public static IllusionRegistry getIllusionRegistry() {
		if (illusionRegistry == null) throw new RuntimeException("The BlocksAPI hasn't been initialized anywhere. Create a new instance of the BlocksAPI class in the 'onEnable' method.");
		return illusionRegistry;
	}

	public static BlockRegistry getBlockRegistry() {
		if (blockRegistry == null) throw new RuntimeException("The BlocksAPI hasn't been initialized anywhere. Create a new instance of the BlocksAPI class in the 'onEnable' method.");
		return blockRegistry;
	}


	public static OriginBlockBuilder getBlock(Location location) {
		RegionManager wgCurrWorldRM = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(location.getWorld()));
		if (wgCurrWorldRM == null) return null;
		ApplicableRegionSet arSet = wgCurrWorldRM.getApplicableRegions(BukkitAdapter.asBlockVector(location));
		if (arSet.size() > 0) {
			for (ProtectedRegion region : arSet) {
				if (regionRegistry.getRegions().containsKey(region.getId())) {
					OriginRegion oRegion = regionRegistry.getRegions().get(region.getId()).getRegion();
					return blockRegistry.getBlocks().get(oRegion.allowedBlock());
				}
			}
		}
		return null;
	}

	public static boolean inRegion(Location loc)
	{
		RegionManager wgCurrWorldRM = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(loc.getWorld()));
		if (wgCurrWorldRM == null) return false;
		ApplicableRegionSet arSet = wgCurrWorldRM.getApplicableRegions(BukkitAdapter.asBlockVector(loc));
		if (arSet.size() > 0) {
			for (ProtectedRegion region : arSet) {
				if (regionRegistry.getRegions().containsKey(region.getId())) {
					return true;
				}
			}
		}
		return false;
	}


	public BlocksAPI(JavaPlugin javaPlugin, LiteConfig lfc, BlockLocationRegistry loc, IllusionRegistry illu, RegenerationRegistry regen, BlockRegistry blockReg, OverlayLocationRegistry overlayReg,  ProgressRegistry progReg, SpeedAttribute speedAttr, RegionRegistry regionReg) {
		regenerationRegistry = regen;
		locationRegistry = loc;
		illusionRegistry = illu;
		blockRegistry = blockReg;
		overlayRegistry = overlayReg;
		progressRegistry = progReg;
		speedAttribute = speedAttr;
		regionRegistry = regionReg;
		injector = Guice.createInjector(new BlocksModule(javaPlugin, lfc, locationRegistry, illusionRegistry, regenerationRegistry, blockRegistry, overlayRegistry, progressRegistry, speedAttribute, regionRegistry));
	}

	static class BlocksModule extends AbstractModule {
		private final JavaPlugin plugin;
		private final LiteConfig lfc;
		private final BlockLocationRegistry locationRegistry;
		private final IllusionRegistry illusionRegistry;
		private final RegenerationRegistry regenerationRegistry;
		private final BlockRegistry blockRegistry;
		private final OverlayLocationRegistry overlayLocationRegistry;
		private final SpeedAttribute speedAttribute;
		private final ProgressRegistry progressRegistry;
		private final RegionRegistry regionRegistry;

		BlocksModule(JavaPlugin plugin, LiteConfig lfc, BlockLocationRegistry locationRegistry, IllusionRegistry illusionRegistry, RegenerationRegistry regenerationRegistry, BlockRegistry blockRegistry, OverlayLocationRegistry overlayRegistry,  ProgressRegistry progressRegistry, SpeedAttribute speedAttribute, RegionRegistry regionRegistry) {
			this.plugin = plugin;
			this.lfc = lfc;
			this.locationRegistry = locationRegistry;
			this.illusionRegistry = illusionRegistry;
			this.regenerationRegistry = regenerationRegistry;
			this.blockRegistry = blockRegistry;
			this.overlayLocationRegistry = overlayRegistry;
			this.progressRegistry = progressRegistry;
			this.speedAttribute = speedAttribute;
			this.regionRegistry = regionRegistry;
		}

		protected void configure() {
			this.bind(JavaPlugin.class).toInstance(this.plugin);
			this.bind(LiteConfig.class).toInstance(this.lfc);
			this.bind(RegenerationRegistry.class).toInstance(this.regenerationRegistry);
			this.bind(IllusionRegistry.class).toInstance(this.illusionRegistry);
			this.bind(BlockRegistry.class).toInstance(this.blockRegistry);
			this.bind(BlockLocationRegistry.class).toInstance(this.locationRegistry);
			this.bind(OverlayLocationRegistry.class).toInstance(this.overlayLocationRegistry);
			this.bind(SpeedAttribute.class).toInstance(this.speedAttribute);
			this.bind(ProgressRegistry.class).toInstance(this.progressRegistry);
			this.bind(RegionRegistry.class).toInstance(this.regionRegistry);
		}
	}
}
