package blocks;

import blocks.block.BlockRegistry;
import blocks.block.aspects.location.registry.BlockLocationRegistry;
import blocks.block.aspects.overlay.registry.OverlayLocationRegistry;
import blocks.block.aspects.regeneration.registry.RegenerationRegistry;
import blocks.block.builder.AspectHolder;
import blocks.block.builder.FixedAspectHolder;
import blocks.block.illusions.IllusionsAPI;
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
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

public final class BlocksAPI {

	private static void uninitd() {
		throw new RuntimeException("The BlocksAPI hasn't been initialized anywhere. Is blocks listed as a 'depends' in your plugin.yml? Did you accidentaly shade the entire lib?.");
	}

	private static Injector injector;

	public static Injector get() {
		if (injector == null) uninitd();
		return injector;
	}

	private static BlocksAPI instance;

	public static BlocksAPI getInstance() {
		if (instance == null) uninitd();
		return instance;
	}

	private final RegenerationRegistry regenerationRegistry;
	private final BlockRegistry blockRegistry;
	private final BlockLocationRegistry locationRegistry;
	private final IllusionsAPI illusions;
	private final OverlayLocationRegistry overlayRegistry;
	private final ProgressRegistry progressRegistry;
	private final SpeedAttribute speedAttribute;
	private final RegionRegistry regionRegistry;

	public final ProgressRegistry getProgressRegistry() {
		return progressRegistry;
	}

	public final RegionRegistry getRegionRegistry() {
		return regionRegistry;
	}

	public final SpeedAttribute getSpeedAttribute() {
		return speedAttribute;
	}

	public final BlockLocationRegistry getLocationRegistry() {
		return locationRegistry;
	}

	public final RegenerationRegistry getRegenerationRegistry() {
		return regenerationRegistry;
	}

	public final OverlayLocationRegistry getOverlayLocationRegistry() {
		return overlayRegistry;
	}

	public final IllusionsAPI getIllusions() {
		return illusions;
	}

	public final BlockRegistry getBlockRegistry() {
		return blockRegistry;
	}

	public static @Nullable FixedAspectHolder getBlock(Location location) {
		FixedAspectHolder fah = instance.locationRegistry.getBlockAt(location);
		if (fah != null) return fah;

		RegionManager wgCurrWorldRM = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(location.getWorld()));
		if (wgCurrWorldRM == null) return null;
		ApplicableRegionSet arSet = wgCurrWorldRM.getApplicableRegions(BukkitAdapter.asBlockVector(location));
		if (arSet.size() > 0) {
			for (ProtectedRegion region : arSet) {
				if (instance.regionRegistry.getRegions().containsKey(region.getId())) {
					OriginRegion oRegion = instance.regionRegistry.getRegions().get(region.getId()).getRegion();
					AspectHolder editor = instance.blockRegistry.getBlocks().get(oRegion.allowedBlock());
					return instance.locationRegistry.createBlock(editor, location);
				}
			}
		}
		return null;
	}

	public static void resetBlock(Location loc) {
		instance.locationRegistry.deleteBlock(loc);
	}

	public static boolean inRegion(Location loc) {
		RegionManager wgCurrWorldRM = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(loc.getWorld()));
		if (wgCurrWorldRM == null) return false;
		ApplicableRegionSet arSet = wgCurrWorldRM.getApplicableRegions(BukkitAdapter.asBlockVector(loc));
		if (arSet.size() > 0) {
			for (ProtectedRegion region : arSet) {
				if (instance.regionRegistry.getRegions().containsKey(region.getId())) {
					return true;
				}
			}
		}
		return false;
	}

	public BlocksAPI(JavaPlugin javaPlugin, BlockLocationRegistry loc, IllusionsAPI illu, RegenerationRegistry regen, BlockRegistry blockReg, OverlayLocationRegistry overlayReg, ProgressRegistry progReg, SpeedAttribute speedAttr, RegionRegistry regionReg) {
		if (instance != null)
			throw new UnsupportedOperationException("API already instantiated. Use BlocksAPI#getInstance() instead.");
		regenerationRegistry = regen;
		locationRegistry = loc;
		illusions = illu;
		blockRegistry = blockReg;
		overlayRegistry = overlayReg;
		progressRegistry = progReg;
		speedAttribute = speedAttr;
		regionRegistry = regionReg;
		injector = Guice.createInjector(new BlocksModule(javaPlugin, locationRegistry, illusions, regenerationRegistry, blockRegistry, overlayRegistry, progressRegistry, speedAttribute, regionRegistry));
		instance = this;
	}

	private static class BlocksModule extends AbstractModule {
		private final JavaPlugin plugin;
		private final BlockLocationRegistry locationRegistry;
		private final IllusionsAPI illusions;
		private final RegenerationRegistry regenerationRegistry;
		private final BlockRegistry blockRegistry;
		private final OverlayLocationRegistry overlayLocationRegistry;
		private final SpeedAttribute speedAttribute;
		private final ProgressRegistry progressRegistry;
		private final RegionRegistry regionRegistry;

		BlocksModule(JavaPlugin plugin, BlockLocationRegistry locationRegistry, IllusionsAPI illusions, RegenerationRegistry regenerationRegistry, BlockRegistry blockRegistry, OverlayLocationRegistry overlayRegistry, ProgressRegistry progressRegistry, SpeedAttribute speedAttribute, RegionRegistry regionRegistry) {
			this.plugin = plugin;
			this.locationRegistry = locationRegistry;
			this.illusions = illusions;
			this.regenerationRegistry = regenerationRegistry;
			this.blockRegistry = blockRegistry;
			this.overlayLocationRegistry = overlayRegistry;
			this.progressRegistry = progressRegistry;
			this.speedAttribute = speedAttribute;
			this.regionRegistry = regionRegistry;
		}

		protected void configure() {
			this.bind(JavaPlugin.class).toInstance(this.plugin);
			this.bind(RegenerationRegistry.class).toInstance(this.regenerationRegistry);
			this.bind(IllusionsAPI.class).toInstance(this.illusions);
			this.bind(BlockRegistry.class).toInstance(this.blockRegistry);
			this.bind(BlockLocationRegistry.class).toInstance(this.locationRegistry);
			this.bind(OverlayLocationRegistry.class).toInstance(this.overlayLocationRegistry);
			this.bind(SpeedAttribute.class).toInstance(this.speedAttribute);
			this.bind(ProgressRegistry.class).toInstance(this.progressRegistry);
			this.bind(RegionRegistry.class).toInstance(this.regionRegistry);
		}
	}
}
