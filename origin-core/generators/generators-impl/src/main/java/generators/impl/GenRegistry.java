package generators.impl;

import blocks.BlocksAPI;
import blocks.block.protect.ProtectedBlock;
import blocks.block.protect.ProtectedObject;
import blocks.block.protect.ProtectionRegistry;
import blocks.block.protect.strategy.ProtectionStrategies;
import commons.Commons;
import generators.GeneratorRegistry;
import generators.wrapper.Generator;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import me.vadim.util.conf.ConfigurationProvider;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 * @author vadim
 */
public class GenRegistry implements GeneratorRegistry {

	private final Object2ObjectMap<Location, Generator> gens = Object2ObjectMaps.synchronize(new Object2ObjectOpenHashMap<>(5000));

	protected @NotNull Iterable<Object2ObjectMap.Entry<Location, Generator>> iterable() {
		return Object2ObjectMaps.fastIterable(gens);
	}

	private final ConfigurationProvider conf;

	public GenRegistry(ConfigurationProvider conf) {
		this.conf = conf;
	}

	public static boolean verify(Generator generator) {
		Block    actual = generator.getBlock();
		Material expect = generator.getCurrentTier().getBlock();
		if (actual.getType() != expect) {
			Commons.logger().severe(
					String.format("ERROR: Block %s does not match expected generator type %s, removing generator owned by %s AT NO REFUND.",
								  actual, expect, generator.getOwnerUUID())
								   );
			return false;
		}
		return true;
	}

	@Override
	public void createGen(Generator generator) {
		ProtectionRegistry registry = BlocksAPI.getInstance().getProtectionRegistry();
		ProtectedBlock     block    = registry.defineBlock(generator.getBlock());
		block.setProtectionStrategy(ProtectionStrategies.permitOwner(generator.getOfflineOwner()));
		gens.put(generator.getBlockLocation(), generator);
	}

	@Override
	public void deleteGen(Generator generator) {
		ProtectionRegistry registry = BlocksAPI.getInstance().getProtectionRegistry();
		ProtectedObject    object   = registry.getActiveProtection(generator.getBlock());
		if (object instanceof ProtectedBlock block)
			registry.release(block);
		gens.remove(generator.getBlockLocation());
	}

	@Override
	public @Nullable Generator getGenAt(Location location) {
		return gens.get(location.getBlock().getLocation());
	}

	// we use fastutil on methods that will be called a lot

	@Override
	public @NotNull List<Generator> getGenerators(OfflinePlayer owner) {
		ObjectList<Generator> g = new ObjectArrayList<>(100);
		for (Object2ObjectMap.Entry<Location, Generator> entry : iterable())
			if (entry.getValue().isOwnedBy(owner))
				g.add(entry.getValue());
		return g;
	}

	@Override
	public int countGenerators(UUID ownerUUID) {
		int num = 0;
		for (Object2ObjectMap.Entry<Location, Generator> entry : iterable())
			if (entry.getValue().isOwnedBy(ownerUUID))
				num++;
		return num;
	}

	@Override
	public @NotNull Iterator<Generator> all() {
		return gens.values().iterator();
	}

}
