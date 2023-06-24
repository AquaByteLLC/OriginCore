package blocks.anim;

import blocks.BlocksAPI;
import blocks.events.OriginBreakEvent;
import blocks.factory.interfaces.OriginBlock;
import blocks.regeneration.BlockRegeneration;
import blocks.registry.ProgressRegistry;
import commons.events.impl.packet.PacketEventListener;
import net.minecraft.core.BlockPosition;
import net.minecraft.network.protocol.game.PacketPlayOutBlockBreakAnimation;
import net.minecraft.server.level.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_19_R3.block.CraftBlock;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.Random;

public class BlockAnimHelper {
	public static class BlockAnimation {
		private Field currentDigTickField, lastDigTickField;
		private final EntityPlayer entityPlayer;
		private final Player player;
		private final Block block;
		private OriginBlock worldBlocksBlock;

		private final float damageDoneToBlock;
		private final ProgressRegistry registry = BlocksAPI.getProgressRegistry();

		public BlockAnimation(Player player, Block block, float damage) {
			this.player = player;
			this.entityPlayer = ((CraftPlayer) player).getHandle();
			this.block = block;
			this.damageDoneToBlock = damage;

			if (BlocksAPI.getBlock(block.getLocation()) != null) {
				this.worldBlocksBlock = BlocksAPI.getBlock(block.getLocation());
			}


		}

		public void handleAnimation() throws NoSuchFieldException, IllegalAccessException {
			BlockPosition blockPos = ((CraftBlock) this.block).getPosition();

			if (!isCreative(this.player)) {
				registry.getBlockProgress().put(blockPos, getDamageDoneToBlock());
			}

			completeBreak(blockPos);

			updatePacket();
		}

		public void updatePacket() {

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

		public double getDamageDoneToBlock() throws NoSuchFieldException, IllegalAccessException {
			BlockPosition blockPos = ((CraftBlock) this.block).getPosition();

			this.currentDigTickField = entityPlayer.d.getClass().getDeclaredField("i");
			this.lastDigTickField = entityPlayer.d.getClass().getDeclaredField("g");

			currentDigTickField.setAccessible(true);
			lastDigTickField.setAccessible(true);

			int currentInt = currentDigTickField.getInt(entityPlayer.d);
			int lastInt = lastDigTickField.getInt(entityPlayer.d);

			int newDigTick = currentInt - lastInt;

			float newDamage = damageDoneToBlock * (float) (newDigTick + 1);
			double doubleProgress = (newDamage * 100f) * this.worldBlocksBlock.getHardnessMultiplier();

			registry.getBlockProgress().remove(blockPos);

			double oldProgress = 0;

			if (registry.getOldBlockProgress().containsKey(blockPos)) {
				oldProgress = registry.getOldBlockProgress().get(blockPos);
			}

			return doubleProgress + oldProgress;
		}

		private void completeBreak(BlockPosition blockPos) {
			double blockProgress = registry.getBlockProgress().get(blockPos);
			double bossBarProgress = blockProgress / 100;

			updateBars(blockPos, blockProgress, bossBarProgress);
		}

		public void updateBars(BlockPosition blockPos, double blockProgress, double bossBarProgress) {
			if (blockProgress < 100 && blockProgress > -1) {
				BlocksAPI.progressBossBar.title("&a&lBlock Progress: &f&l" + (Math.round(blockProgress * 100) / 100) + "%");
				BlocksAPI.progressBossBar.addPlayer(this.player);
				BlocksAPI.progressBossBar.progress(bossBarProgress);
			} else {
				registry.getBlocksBreaking().remove(blockPos);
				registry.getBlockProgress().remove(blockPos);
				registry.getOldBlockProgress().remove(blockPos);

				new BlockRegeneration(this.player, this.block.getLocation(), (long) this.worldBlocksBlock.getRegenTime());

				/*
				Bukkit.getPluginManager().callEvent(new OriginBreakEvent(this.block, this.player));

				 */

				OriginBreakEvent.call();

				BlocksAPI.progressBossBar.close();
			}
		}

		private boolean isCreative(Player player) {
			return player.getGameMode() == GameMode.CREATIVE;
		}
	}
}
