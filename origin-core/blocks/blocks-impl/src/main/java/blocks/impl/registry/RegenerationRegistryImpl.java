package blocks.impl.registry;

import blocks.block.aspects.illusions.FakeBlock;
import blocks.block.aspects.illusions.registry.IllusionRegistry;
import blocks.block.aspects.location.BlockLocatable;
import blocks.block.aspects.location.Locatable;
import blocks.block.aspects.regeneration.Regenable;
import blocks.block.aspects.regeneration.registry.RegenerationRegistry;
import blocks.impl.aspect.AspectEnum;
import blocks.impl.handler.BlocksConfig;
import me.lucko.helper.Schedulers;
import me.lucko.helper.text3.format.TextColor;
import me.vadim.util.conf.LiteConfig;
import net.minecraft.core.BlockPosition;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class RegenerationRegistryImpl implements RegenerationRegistry {

	private final HashMap<BlockPosition, Regenable> regenableHashMap;
	private final IllusionRegistry illusionRegistry;
	private final BlocksConfig config;
	private final JavaPlugin plugin;

	public RegenerationRegistryImpl(JavaPlugin plugin, LiteConfig lfc, IllusionRegistry illusionRegistry) {
		this.regenableHashMap = new HashMap<>();
		this.illusionRegistry = illusionRegistry;
		this.config = lfc.open(BlocksConfig.class);
		this.plugin = plugin;
	}

	@Override
	public void createRegen(Regenable block, Block original, Player player, long end) {
		final BlockLocatable locatable = block.getFakeBlock().getLocatable();
		final BlockPosition position = new BlockPosition(locatable.getBlockLocation().getBlockX(), locatable.getBlockLocation().getBlockY(), locatable.getBlockLocation().getBlockZ());

		final YamlConfiguration configuration = YamlConfiguration.loadConfiguration(config.file);
		final Material regenMaterial = Material.matchMaterial(String.valueOf(configuration.getString("regenMaterial")));

		regenableHashMap.put(position, block);
		block.getBuilder().createAspect(AspectEnum.OVERLAYABLE.getName(), block.getBuilder().getFactory().newOverlayable().setOverlayColor(TextColor.GREEN).setPlayerConsumer(player1 -> player1.sendMessage("test")));
		illusionRegistry.register(block.getFakeBlock());

		player.sendBlockChange(locatable.getBlockLocation(), block.getFakeBlock().getProjectedBlockData());

		while (System.currentTimeMillis() >= end) {
			if (getRegenerations().containsKey(position)) {

				BlockData blockData = original.getBlockData();
				if (blockData instanceof Ageable ageable) {
					Schedulers.bukkit().runTaskTimer(plugin, runnable -> {
						if (!(ageable.getAge() == ageable.getMaximumAge())) {
							ageable.setAge(ageable.getAge() + 1);
							FakeBlock newFakeBlock = illusionRegistry.getBlockAt(locatable.getBlockLocation()).setProjectedBlockData(blockData).setLocatable(locatable);
							illusionRegistry.unregister(block.getFakeBlock());
							illusionRegistry.register(newFakeBlock);
							player.sendBlockChange(locatable.getBlockLocation(), newFakeBlock.getProjectedBlockData());
						} else {
							runnable.cancel();
						}
					}, 10, 10);
				}
			}
		}

		deleteRegen(block);
		player.sendBlockChange(locatable.getBlockLocation(), original.getBlockData());
		illusionRegistry.unregister(block.getFakeBlock());
	}

	@Override
	public void deleteRegen(Regenable block) {
		Locatable locatable = block.getFakeBlock().getLocatable();
		BlockPosition position = new BlockPosition(locatable.getBlockLocation().getBlockX(), locatable.getBlockLocation().getBlockY(), locatable.getBlockLocation().getBlockZ());
		regenableHashMap.remove(position);
	}

	@Override
	public @NotNull HashMap<BlockPosition, Regenable> getRegenerations() {
		return this.regenableHashMap;
	}
}
