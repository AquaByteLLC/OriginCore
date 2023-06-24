package blocks.regeneration;

import blocks.BlocksAPI;
import blocks.registry.BlockRegistry;
import com.google.common.collect.Multiset;
import lombok.Getter;
import me.lucko.helper.Schedulers;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

public class BlockRegeneration {

	@Getter private final JavaPlugin instance;
	@Getter private final Location blockPosition;
	@Getter private final long end;
	@Getter private final Player player;
	private final YamlConfiguration config = BlocksAPI.getGeneralConfig();
	private static final BlockRegistry registry = BlocksAPI.getBlockRegistry();
	private static final BlockRegistry.RandomBlock randomBlock = BlocksAPI.get().getInstance(BlockRegistry.RandomBlock.class);
	private final Block block;

	public BlockRegeneration(Player player, Location blockPosition, long end) {
		this.instance = BlocksAPI.get().getInstance(JavaPlugin.class);
		this.blockPosition = blockPosition;
		this.end = end;
		this.block = randomBlock.get();
		this.player = player;
		makeRegen();
	}

	public void makeRegen() {
		final Material material = Material.matchMaterial(Objects.requireNonNull(config.getString("regenBlockType")));

		if (material == null) return;

		this.block.setType(material);
		registry.getRegeneratingBlocks().add(this.blockPosition);

		new BukkitRunnable() {
			@Override
			public void run() {
				if (!registry.getRegeneratingBlocks().contains(blockPosition)) return;
				if (!(System.currentTimeMillis() >= end)) return;
				if (block.getBlockData() instanceof Ageable ageable) {
					Schedulers.async().runRepeating(runnable -> {
						if (!ageable.isAdult()) {
							ageable.setAge(ageable.getAge() + 1);
							player.sendBlockChange(blockPosition, block.getBlockData());
						} else {
							registry.getRegeneratingBlocks().remove(blockPosition);
							cancel();
						}
					}, 10, 10);
				}
				player.sendBlockChange(blockPosition, block.getBlockData());
				registry.getRegeneratingBlocks().remove(blockPosition);
			}
		}.runTask(this.instance);
	}

	public static void cancelRegenerations() {
		for (Player user : Bukkit.getOnlinePlayers()) {
			for (Multiset.Entry<Location> blocks : registry.getRegeneratingBlocks().entrySet()) {
				final Location blockKey = blocks.getElement();
				final Block blockVal = randomBlock.get();
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
