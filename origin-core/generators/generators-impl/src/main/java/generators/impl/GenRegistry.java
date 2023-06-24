package generators.impl;

import commons.PackUtil;
import generators.GeneratorRegistry;
import generators.wrapper.Generator;
import generators.wrapper.Tier;
import generators.wrapper.Upgrade;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import me.vadim.util.conf.ConfigurationProvider;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 * @author vadim
 */
public class GenRegistry implements GeneratorRegistry {

	private final Long2ObjectMap<Generator> gens = Long2ObjectMaps.synchronize(new Long2ObjectOpenHashMap<>(5000));

	protected @NotNull Iterable<Long2ObjectMap.Entry<Generator>> iterable() {
		return Long2ObjectMaps.fastIterable(gens);
	}

	private final ConfigurationProvider conf;

	public GenRegistry(ConfigurationProvider conf) {
		this.conf = conf;
	}

	@Override
	public void createGen(Generator generator) {
		gens.put(PackUtil.packLoc(generator.getBlockLocation()), generator);
	}

	@Override
	public void deleteGen(Generator generator) {
		gens.remove(PackUtil.packLoc(generator.getBlockLocation()));
	}

	@Override
	public @Nullable Generator getGenAt(Location location) {
		return gens.get(PackUtil.packLoc(location.getBlock().getLocation()));
	}

	// we use fastutil on methods that will be called a lot

	@Override
	public @NotNull List<Generator> getGenerators(OfflinePlayer owner) {
		ObjectList<Generator> g = new ObjectArrayList<>(100);
		for (Long2ObjectMap.Entry<Generator> entry : Long2ObjectMaps.fastIterable(gens))
			if (entry.getValue().isOwnedBy(owner))
				g.add(entry.getValue());
		return g;
	}

	@Override
	public int countGenerators(UUID ownerUUID) {
		int num = 0;
		for (Long2ObjectMap.Entry<Generator> entry : Long2ObjectMaps.fastIterable(gens))
			if (entry.getValue().isOwnedBy(ownerUUID))
				num++;
		return num;
	}

	@Override
	public @NotNull Iterator<Generator> all() {
		return gens.values().iterator();
	}

}
