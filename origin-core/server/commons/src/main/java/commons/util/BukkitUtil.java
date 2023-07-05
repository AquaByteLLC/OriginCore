package commons.util;

import commons.CommonsPlugin;
import lombok.SneakyThrows;
import me.lucko.helper.text3.Text;
import me.vadim.util.conf.wrapper.Placeholder;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.Chunk;
import net.minecraft.world.level.chunk.ChunkSection;
import net.minecraft.world.level.levelgen.HeightMap;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.VoxelShape;
import org.jetbrains.annotations.NotNull;

/**
 * @author vadim
 */
public class BukkitUtil {

	@SuppressWarnings("DataFlowIssue")
	public static void formatItem(Placeholder pl, ItemStack item) {
		item.editMeta(meta -> {
			if (meta.hasDisplayName())
				meta.setDisplayName(Text.colorize(pl.format(meta.getDisplayName())));
			if (meta.hasLore())
				meta.setLore(meta.getLore().stream().map(pl::format).map(Text::colorize).toList());
		});
	}

	public static void sendPacket(Player player, Packet<?> packet) {
		((CraftPlayer) player).getHandle().b.a(packet);
	}

	//https://www.spigotmc.org/threads/how-to-check-if-a-block-is-realy-a-block.536470/#post-4314270
	public static boolean isCube(Block block) {
		VoxelShape  voxelShape  = block.getCollisionShape();
		BoundingBox boundingBox = block.getBoundingBox();
		return (voxelShape.getBoundingBoxes().size() == 1
				&& boundingBox.getWidthX() == 1.0
				&& boundingBox.getHeight() == 1.0
				&& boundingBox.getWidthZ() == 1.0
		);
	}

	@FunctionalInterface
	public interface Exceptional {
		void run() throws Throwable;
	}

	@SuppressWarnings("Convert2Lambda")
	private static Runnable wrap(Exceptional exceptional) {
		return new Runnable() {
			@Override
			@SneakyThrows
			public void run() {
				exceptional.run();
			}
		};
	}

	@Deprecated
	public static BukkitTask sync(Exceptional exceptional) {
		return Bukkit.getScheduler().runTask(CommonsPlugin.commons(), wrap(exceptional));
	}

	@Deprecated
	public static BukkitTask sync(Exceptional exceptional, long ticks) {
		return Bukkit.getScheduler().runTaskLater(CommonsPlugin.commons(), wrap(exceptional), ticks);
	}

	@Deprecated
	public static BukkitTask async(Exceptional exceptional) {
		return Bukkit.getScheduler().runTaskAsynchronously(CommonsPlugin.commons(), wrap(exceptional));
	}

	@Deprecated
	public static BukkitTask async(Exceptional exceptional, long ticks) {
		return Bukkit.getScheduler().runTaskLaterAsynchronously(CommonsPlugin.commons(), wrap(exceptional), ticks);
	}

	/*
	 * These are useful methods, so I'm leaving them here.
	 * However, they did not work for my purposes.
	 */

	public static @NotNull ChunkSection getSection(Chunk chunk, int blockY) {
		int sY = chunk.e(blockY & 15); // since Y can be negative, LevelHeightAccessor apparantly "converts" this to a ChunkSection index
		ChunkSection[] sections = chunk.d(); // chunk.getSections()
		ChunkSection section = sections[sY];
		if(section == null)
			section = sections[sY] = new ChunkSection(sY, chunk.biomeRegistry);
		return section;
	}

	public static void setBlockState(Chunk chunk, int x, int y, int z, IBlockData data) {
		setBlockState(getSection(chunk, y), chunk, x, y, z, data);
	}

	public static void setBlockState(@NotNull ChunkSection section, Chunk chunk, int x, int y, int z, IBlockData data) {
		// update inside chunk section
		section.a(x, y, z, data, false); // section.setType(x, y, z, data, some_kind_of_update)

		// update heightmap
		chunk.g.get(HeightMap.Type.e).a(x, y, z, data); // ((Heightmap)this.heightmaps.get(Types.MOTION_BLOCKING)).update(j, i, l, blockstate);
		chunk.g.get(HeightMap.Type.f).a(x, y, z, data); // ((Heightmap)this.heightmaps.get(Types.MOTION_BLOCKING_NO_LEAVES)).update(j, i, l, blockstate);
		chunk.g.get(HeightMap.Type.d).a(x, y, z, data); // ((Heightmap)this.heightmaps.get(Types.OCEAN_FLOOR)).update(j, i, l, blockstate);
		chunk.g.get(HeightMap.Type.b).a(x, y, z, data); // ((Heightmap)this.heightmaps.get(Types.WORLD_SURFACE)).update(j, i, l, blockstate);
	}

}
