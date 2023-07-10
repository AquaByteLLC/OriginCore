package blocks.impl.protect;

import blocks.block.protect.ProtectedBlock;
import blocks.block.protect.ProtectedObject;
import blocks.block.protect.ProtectedRegion;
import blocks.block.protect.ProtectionRegistry;
import blocks.block.util.Cuboid;
import commons.util.PackUtil;
import it.unimi.dsi.fastutil.objects.ObjectList;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * (hopefully) thread-safe in-memory impl of {@link ProtectionRegistry}
 * @author vadim
 */
public class TransientProtectionRegistry implements ProtectionRegistry {

	private final Map<UUID, Guard> guards = new ConcurrentHashMap<>(200);

	@Override
	public boolean isProtected(Block block) {
		if (block == null)
			return false;

		Guard guard = guards.get(block.getWorld().getUID());
		if (guard == null)
			return false;

		if (guard.getBlockAt(block) != null)
			return true;

		int x, y, z;
		x = block.getX();
		y = block.getY();
		z = block.getZ();

		boolean result = false;
		guard.lock.readLock().lock();
		try {
			for (ProtectedRegion region : guard.regions)
				if ((result |= region.getBounds().contains(x, y, z)))
					break;
		} finally {
			guard.lock.readLock().unlock();
		}

		return result;
	}

	@Override
	public @Nullable ProtectedObject getActiveProtection(Block block) {
		if (block == null)
			return null;

		Guard guard = guards.get(block.getWorld().getUID());
		if (guard == null)
			return null;

		return guard.getProtectionAt(block);
	}

	@Override
	public @NotNull ProtectedObject[] getAllProtection(Block block) {
		if (block == null)
			return new ProtectedObject[0];

		Guard guard = guards.get(block.getWorld().getUID());
		if (guard == null)
			return new ProtectedObject[0];

		ObjectList<ProtectedRegion> list   = guard.getRegionsAt(block);
		ProtectedObject             object = guard.getBlockAt(block);

		int len = list.size();
		if (object != null) len++;

		ProtectedObject[] arr = new ProtectedObject[len];
		list.getElements(0, arr, 0, len);

		if (object != null)
			arr[len - 1] = object;

		return arr;
	}

	@Override
	public ProtectedBlock defineBlock(Block block) {
		if (block == null)
			throw new NullPointerException("Cannot protect a null block.");
		Guard guard = guards.computeIfAbsent(block.getWorld().getUID(), Guard::new);

		ProtectedBlock prot;
		guard.lock.writeLock().lock();
		try {
			prot = guard.blocks.computeIfAbsent(PackUtil.packLoc(block.getX(), block.getY(), block.getZ()), __ -> new ProtBlock(block));
		} finally {
			guard.lock.writeLock().unlock();
		}

		return prot;
	}

	@Override
	public ProtectedRegion defineRegion(World world, Cuboid cuboid) {
		if (world == null)
			throw new NullPointerException("Cannot protect a null world.");
		if (cuboid == null)
			throw new NullPointerException("Cannot protect a null cubiod.");

		Guard guard = guards.computeIfAbsent(world.getUID(), Guard::new);

		ProtectedRegion prot = null;
		guard.lock.readLock().lock();
		try {
			for (ProtectedRegion protectedRegion : guard.regions)
				if (protectedRegion.getBounds().equals(cuboid))
					prot = protectedRegion;
		} finally {
			guard.lock.readLock().unlock();
		}

		if (prot == null) {
			guard.lock.writeLock().lock();
			try {
				prot = new ProtRegion(world, cuboid);
				guard.regions.add(prot);
			} finally {
				guard.lock.writeLock().unlock();
			}
		}

		return prot;
	}

	@Override
	public void release(ProtectedObject object) {
		if (object == null)
			return;
		Guard guard = guards.get(object.getWorld().getUID());
		if (guard == null)
			return;
		guard.lock.writeLock().lock();
		try {
			if (object instanceof ProtectedBlock prot) {
				Block block = prot.getBlock();
				guard.blocks.remove(PackUtil.packLoc(block.getX(), block.getY(), block.getZ()));
			}
			if (object instanceof ProtectedRegion prot) {
				guard.regions.remove(prot);
			}
		} finally {
			guard.lock.writeLock().unlock();
		}
	}

}
