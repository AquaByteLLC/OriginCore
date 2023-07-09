package blocks.impl.protect;

import blocks.block.protect.ProtectedBlock;
import blocks.block.protect.ProtectedObject;
import blocks.block.protect.ProtectedRegion;
import blocks.block.protect.ProtectionRegistry;
import blocks.block.util.Cuboid;
import commons.util.PackUtil;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectList;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * @author vadim
 */
public class TransientProtectionRegistry implements ProtectionRegistry {

	private final Object2ObjectMap<UUID, Guard> guards = new Object2ObjectOpenHashMap<>(200);

	@Override
	public @NotNull ProtectedObject[] getProtectionAt(Block block) {
		if(block == null)
			return new ProtectedObject[0];

		Guard guard = guards.get(block.getWorld().getUID());
		if (guard == null)
			return new ProtectedObject[0];

		ObjectList<ProtectedRegion> list = guard.getRegionsAt(block);
		ProtectedObject object = guard.getBlockAt(block);

		int len = list.size();
		if (object != null) len++;

		ProtectedObject[] arr = new ProtectedObject[len];
		list.getElements(0, arr, 0, len);

		if (object != null)
			arr[len - 1] = object;

		return arr;
	}

	@Override
	public boolean isProtected(Block block) {
		if(block == null)
			return false;

		Guard guard = guards.get(block.getWorld().getUID());
		if(guard == null)
			return false;

		if (guard.getBlockAt(block) != null)
			return true;

		int x, y, z;
		x = block.getX();
		y = block.getY();
		z = block.getZ();

		for (ProtectedRegion region : guard.regions)
			if (region.getBounds().contains(x, y, z))
				return true;

		return false;
	}

	@Override
	public ProtectedBlock protectBlock(Block block) {
		if(block == null)
			throw new NullPointerException("Cannot protect a null block.");
		return guards.computeIfAbsent(block.getWorld().getUID(), Guard::new).blocks
					 .computeIfAbsent(PackUtil.packLoc(block.getX(), block.getY(), block.getZ()), __ -> new ProtBlock(block));
	}

	@Override
	public ProtectedRegion protectRegion(World world, Cuboid cuboid) {
		if (world == null)
			throw new NullPointerException("Cannot protect a null world.");
		if (cuboid == null)
			throw new NullPointerException("Cannot protect a null cubiod.");

		ProtectedRegion region = new ProtRegion(world, cuboid);
		ObjectList<ProtectedRegion> list = guards.computeIfAbsent(world.getUID(), Guard::new).regions;

		int i = list.indexOf(region); // impl uses hashCode, and prio is excluded from that calculation
		if (i == -1)
			list.add(region);
		else
			region = list.get(i);

		return region;
	}

	@Override
	public void removeProtection(ProtectedObject object) {
		if (object == null)
			return;
		Guard guard = guards.get(object.getWorld().getUID());
		if (guard == null)
			return;
		if (object instanceof ProtectedBlock prot) {
			Block block = prot.getBlock();
			guard.blocks.remove(PackUtil.packLoc(block.getX(), block.getY(), block.getZ()));
		}
		if (object instanceof ProtectedRegion prot) {
			guard.regions.remove(prot);
		}
	}

}
