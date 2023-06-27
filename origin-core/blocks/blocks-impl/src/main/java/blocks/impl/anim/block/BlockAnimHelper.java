package blocks.impl.anim.block;

import blocks.BlocksAPI;
import blocks.block.BlockRegistry;
import blocks.block.aspects.harden.Hardenable;
import blocks.block.aspects.illusions.FakeBlock;
import blocks.block.aspects.location.BlockLocatable;
import blocks.block.aspects.regeneration.Regenable;
import blocks.block.aspects.regeneration.registry.RegenerationRegistry;
import blocks.block.builder.OriginBlockBuilder;
import blocks.block.progress.SpeedAttribute;
import blocks.block.progress.registry.ProgressRegistry;
import blocks.impl.aspect.AspectEnum;
import blocks.impl.aspect.location.BlockLocation;
import blocks.impl.event.OriginBreakEvent;
import commons.events.impl.packet.PacketEventListener;
import lombok.Getter;
import lombok.SneakyThrows;
import me.lucko.helper.bossbar.BossBar;
import net.minecraft.core.BlockPosition;
import net.minecraft.network.protocol.game.PacketPlayOutBlockBreakAnimation;
import net.minecraft.server.level.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;

import java.lang.reflect.Field;
import java.util.Random;

public class BlockAnimHelper {

	private final ProgressRegistry registry;
	private final BlockRegistry blockRegistry;
	private final RegenerationRegistry regenerationRegistry;
	@Getter private final SpeedAttribute breakSpeed;
	private final BossBar bossBar;

	public BlockAnimHelper(BossBar bossBar) {
		this.registry = BlocksAPI.getProgressRegistry();
		this.blockRegistry = BlocksAPI.getBlockRegistry();
		this.breakSpeed = BlocksAPI.getSpeedAttribute();
		this.regenerationRegistry = BlocksAPI.getRegenerationRegistry();
		this.bossBar = bossBar;
	}

	private void updatePacket() {
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			for (BlockPosition blockPosition : registry.getBlockProgress().keySet()) {
				double progress = registry.getBlockProgress().get(blockPosition);
				int progressInt = (int) progress;

				if (!registry.getRandomIntegers().containsKey(blockPosition)) {
					Random random = new Random(System.currentTimeMillis());
					registry.getRandomIntegers().put(blockPosition, random.nextInt(1000));
				}

				PacketPlayOutBlockBreakAnimation packet = new PacketPlayOutBlockBreakAnimation(registry.getRandomIntegers().get(blockPosition), blockPosition, (progressInt / 10));
				PacketEventListener.sendPacket(player, packet);
			}
		}
	}

	@SneakyThrows
	public void progression() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (player == null) return;
			try {
				Block block = player.getTargetBlockExact(5);
				double blockProgress = 0;
				if (block == null) return;

				BlockPosition blockPosition = new BlockPosition(block.getX(), block.getY(), block.getZ());
				if (BlocksAPI.getBlock(block.getLocation()) == null) return;

				OriginBlockBuilder originBlock = BlocksAPI.getBlock(block.getLocation());

				if (originBlock == null) return;

				if ((player.getLocation().getZ() + 12.0 >= block.getZ()) || (player.getLocation().getX() + 12.0 >= block.getX()) || player.getLocation().getZ() + 12.0 >= block.getZ()) {
					cleanup(blockPosition, block, player, blockProgress);
				}

				Hardenable hardenable = (Hardenable) originBlock.getAspects().get(AspectEnum.HARDENABLE.getName());
				FakeBlock fakeBlock = (FakeBlock) originBlock.getAspects().get(AspectEnum.FAKE_BLOCK.getName());

				if (hardenable == null) return;
				if (hardenable.getHardnessMultiplier() <= 0) return;

				if (fakeBlock == null) return;
				if (fakeBlock.getProjectedBlockData() == null) return;

				double hardnessMultiplier = 1d / (hardenable.getHardnessMultiplier() / 100d);

				if (registry.getBlockBreak(blockPosition)) {
					EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();

					final Field currentDigTickField = entityPlayer.d.getClass().getDeclaredField("i");
					final Field lastDigTickField = entityPlayer.d.getClass().getDeclaredField("g");
					currentDigTickField.setAccessible(true);
					lastDigTickField.setAccessible(true);

					final int currentDigTick = currentDigTickField.getInt(entityPlayer.d);
					final int lastDigTick = lastDigTickField.getInt(entityPlayer.d);

					final int newDigTick = currentDigTick - lastDigTick;

					final ItemStack playerStack = player.getInventory().getItemInMainHand();
					if (!playerStack.hasItemMeta()) return;
					final PersistentDataContainer container = playerStack.getItemMeta().getPersistentDataContainer();
					if (!container.has(SpeedAttribute.getKey())) return;

					final float breakValue = breakSpeed.getSpeed(playerStack);
					final float newDamage = breakValue * (float) (newDigTick + 1);
					final double doubleProgress = (newDamage * 100f) * hardnessMultiplier;

					registry.getBlockProgress().remove(blockPosition);

					double oldProgress = 0;

					if (registry.getOldBlockProgress().containsKey(blockPosition)) {
						oldProgress = registry.getOldBlockProgress().get(blockPosition);
					}

					if (player.getGameMode() != GameMode.CREATIVE) {
						registry.getBlockProgress().put(blockPosition, doubleProgress + oldProgress);
					}

					blockProgress = registry.getBlockProgress().get(blockPosition);
					final double bossBarProgress = blockProgress / 100;

					if (blockProgress < 100 && blockProgress > -1) {
						bossBar.title("&a&lBlock Progress: &f&l" + (Math.round(blockProgress * 100) / 100.0) + "%");
						bossBar.addPlayer(player);
						bossBar.progress(bossBarProgress);
						continue;
					}

					fakeBlock.setLocatable((BlockLocatable) new BlockLocation(originBlock).setBlock(block).setBlockLocation(block.getLocation()));
					System.out.println(fakeBlock.getLocatable().getBlockLocation());
					if (fakeBlock.getLocatable() == null) return;
					originBlock.createAspect(AspectEnum.FAKE_BLOCK.getName(), fakeBlock);

					Regenable regenable = (Regenable) originBlock.getAspects().get(AspectEnum.REGENABLE.getName());
					if (regenable == null) return;
					if (regenable.getFakeBlock() == null) return;
					if (regenable.getRegenTime() <= 0) return;

					regenable.setFakeBlock(fakeBlock);

					long endTime = (long) (System.currentTimeMillis() + regenable.getRegenTime() * 1000);
					regenerationRegistry.createRegen(regenable, block, player, endTime);
					new OriginBreakEvent(block, player).callEvent();
					cleanup(blockPosition, block, player, blockProgress);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		updatePacket();

	}

	private void cleanup(BlockPosition blockPosition, Block block, Player player, double progress) {
		if (progress > 0) {
			registry.resetAll(blockPosition);
			int randomInts = registry.getRandomIntegers().get(blockPosition) != null ? registry.getRandomIntegers().get(blockPosition) : new Random().nextInt(999999999);
			PacketPlayOutBlockBreakAnimation packet = new PacketPlayOutBlockBreakAnimation(randomInts, blockPosition, 0);
			PacketEventListener.sendPacket(player, packet);
		}
		bossBar.removePlayer(player);
	}

}