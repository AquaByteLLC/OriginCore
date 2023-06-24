package blocks.regeneration;

import blocks.BlocksAPI;
import blocks.registry.BlockRegistry;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

public abstract class BlockRegeneration {

	@Getter
	private final JavaPlugin instance;
	@Getter
	private final Location blockPosition;
	@Getter
	private final Block block;
	@Getter
	private final long end;
	@Getter
	private final Player player;
	private static final BlockRegistry registry = BlocksAPI.get().getInstance(BlockRegistry.class);

	public BlockRegeneration(Player player, Location blockPosition, Block block, long end) {
		this.instance = BlocksAPI.get().getInstance(JavaPlugin.class);
		this.blockPosition = blockPosition;
		this.block = block;
		this.end = end;
		this.player = player;
		makeRegen();
	}

	abstract void makeRegen();

	static void cancelRegenerations() {
		for (Player user : Bukkit.getOnlinePlayers()) {
			for (Map.Entry<Location, Block> blocks : registry.getRegeneratingBlocks().entrySet()) {
				Location blockKey = blocks.getKey();
				Block blockVal = blocks.getValue();
				user.sendBlockChange(blockKey, blockVal.getBlockData());
				registry.getRegeneratingBlocks().remove(blockKey);
			}
		}
	}
}

	/*
	public void makeRegen() {
		this.block.setType(Material.STONE);
		registry.getRegeneratingBlocks().put(this.blockPosition, block);

		new BukkitRunnable() {
			@Override
			public void run() {
				if (!registry.getRegeneratingBlocks().containsKey(blockPosition)) return;
				if (!(System.currentTimeMillis() >= end)) return;

				player.sendBlockChange(blockPosition, block.getBlockData());
				registry.getRegeneratingBlocks().remove(blockPosition);
			}
		}.runTask(this.instance);
	}

	public static void cancelRegenerations() {
		for (Player user : Bukkit.getOnlinePlayers()) {
			for (Map.Entry<Location, Block> blocks : registry.getRegeneratingBlocks().entrySet()) {
				Location blockKey = blocks.getKey();
				Block blockVal = blocks.getValue();
				user.sendBlockChange(blockKey, blockVal.getBlockData());
				registry.getRegeneratingBlocks().remove(blockKey);
			}
		}
	}
}
	 */
