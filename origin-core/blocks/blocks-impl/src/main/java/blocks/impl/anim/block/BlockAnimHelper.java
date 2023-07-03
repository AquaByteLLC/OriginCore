package blocks.impl.anim.block;

import blocks.BlocksAPI;
import blocks.block.BlockRegistry;
import blocks.block.aspects.AspectType;
import blocks.block.aspects.harden.Hardenable;
import blocks.block.aspects.projection.Projectable;
import blocks.block.aspects.regeneration.Regenable;
import blocks.block.aspects.regeneration.registry.RegenerationRegistry;
import blocks.block.builder.FixedAspectHolder;
import blocks.block.illusions.FakeBlock;
import blocks.block.illusions.IllusionsAPI;
import blocks.block.progress.SpeedAttribute;
import blocks.block.progress.registry.ProgressRegistry;
import blocks.impl.BlocksPlugin;
import blocks.impl.data.account.BlockAccount;
import blocks.impl.event.OriginBreakEvent;
import commons.events.impl.impl.PacketEventListener;
import commons.util.reflect.FieldAccess;
import commons.util.reflect.Reflection;
import lombok.Getter;
import lombok.SneakyThrows;
import me.lucko.helper.bossbar.BossBar;
import net.minecraft.core.BlockPosition;
import net.minecraft.network.protocol.game.PacketPlayOutBlockBreakAnimation;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.PlayerInteractManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.Random;

public class BlockAnimHelper {

	private final BlockRegistry blockRegistry;
	private final IllusionsAPI illusions;
	@Getter
	private final SpeedAttribute breakSpeed;
	private static final BlocksPlugin plugin = BlocksPlugin.get().getInstance(BlocksPlugin.class);


	public BlockAnimHelper() {
		this.blockRegistry = BlocksAPI.getInstance().getBlockRegistry();
		this.breakSpeed = BlocksAPI.getInstance().getSpeedAttribute();
		this.illusions = BlocksAPI.getInstance().getIllusions();
	}

	private void updatePacket(Player player) {
		final BlockAccount playerAccount = plugin.getAccounts().getAccount(player);
		final ProgressRegistry progressRegistry = playerAccount.getProgressRegistry();

		for (BlockPosition blockPosition : progressRegistry.getBlockProgress().keySet()) {
			double progress = progressRegistry.getBlockProgress().get(blockPosition);
			int progressInt = (int) progress;

			if (!progressRegistry.getRandomIntegers().containsKey(blockPosition)) {
				Random random = new Random(System.currentTimeMillis());
				progressRegistry.getRandomIntegers().put(blockPosition, random.nextInt(1000));
			}

			PacketPlayOutBlockBreakAnimation packet = new PacketPlayOutBlockBreakAnimation(progressRegistry.getRandomIntegers().get(blockPosition), blockPosition, (progressInt / 10));
			PacketEventListener.sendPacket(player, packet);
		}
	}

	private static final FieldAccess<Integer> currentDigTickField = Reflection.unreflectFieldAccess(PlayerInteractManager.class, "i");
	private static final FieldAccess<Integer> lastDigTickField = Reflection.unreflectFieldAccess(PlayerInteractManager.class, "g");

	@SneakyThrows
	public void progression() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (player == null) return;

			try {
				final BlockAccount playerAccount = plugin.getAccounts().getAccount(player);
				final RegenerationRegistry regenerationRegistry = playerAccount.getRegenerationRegistry();
				final ProgressRegistry progressRegistry = playerAccount.getProgressRegistry();
				final BossBar playerBar = playerAccount.getPlayerBar();

				Block block = player.getTargetBlockExact(5);
				double blockProgress = 0;
				if (block == null) return;

				BlockPosition blockPosition = new BlockPosition(block.getX(), block.getY(), block.getZ());
				FixedAspectHolder originBlock = BlocksAPI.getBlock(block.getLocation());

				if (originBlock == null) return;

				if (player.getLocation().distanceSquared(block.getLocation()) >= 16)
					cleanup(blockPosition, block, player, blockProgress, playerAccount);

				Hardenable hardenable = (Hardenable) originBlock.getAspects().get(AspectType.HARDENABLE);
				Projectable projectable = (Projectable) originBlock.getAspects().get(AspectType.PROJECTABLE);

				if (hardenable == null) return;
				if (hardenable.getHardnessMultiplier() <= 0) return;

				if (projectable == null) return;
				if (projectable.getProjectedBlockData() == null) return;

				double hardnessMultiplier = 1d / (hardenable.getHardnessMultiplier() / 100d);

				if (progressRegistry.getBlockBreak(blockPosition)) {
					EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();

					final int currentDigTick = currentDigTickField.get(entityPlayer.d);
					final int lastDigTick = lastDigTickField.get(entityPlayer.d);

					final int newDigTick = currentDigTick - lastDigTick;

					final ItemStack playerStack = player.getInventory().getItemInMainHand();
					if (!playerStack.hasItemMeta()) return;

					final PersistentDataContainer container = playerStack.getItemMeta().getPersistentDataContainer();
					if (!container.has(SpeedAttribute.getKey())) return;

					final float breakValue = breakSpeed.getSpeed(playerStack);
					final float newDamage = breakValue * (float) (newDigTick + 1);
					final double doubleProgress = (newDamage * 100f) * hardnessMultiplier;

					progressRegistry.getBlockProgress().remove(blockPosition);

					double oldProgress = 0;

					if (progressRegistry.getOldBlockProgress().containsKey(blockPosition)) {
						oldProgress = progressRegistry.getOldBlockProgress().get(blockPosition);
					}

					if (player.getGameMode() != GameMode.CREATIVE) {
						blockProgress = doubleProgress + oldProgress;
						progressRegistry.getBlockProgress().put(blockPosition, blockProgress);
					}

					final double bossBarProgress = blockProgress / 100;

					if (blockProgress < 100 && blockProgress > -1) {
						playerBar.title("&a&lBlock Progress: &f&l" + (Math.round(blockProgress * 100) / 100.0) + "%");
						playerBar.addPlayer(player);
						playerBar.progress(bossBarProgress);
						continue;
					}

					FakeBlock fake = projectable.toFakeBlock(originBlock.getBlockLocation());
					Regenable regenable = (Regenable) originBlock.getAspects().get(AspectType.REGENABLE);
					if (regenable == null) return;
					if (regenable.getRegenTime() <= 0) return;
					regenable.setFakeBlock(fake);

					long endTime = (long) (System.currentTimeMillis() + regenable.getRegenTime() * 1000);
					regenerationRegistry.createRegen(regenable, block, player, endTime);
					new OriginBreakEvent(block, player).callEvent();
					cleanup(blockPosition, block, player, blockProgress, playerAccount);
					updatePacket(player);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void cleanup(BlockPosition blockPosition, Block block, Player player, double progress, BlockAccount account) {
		final ProgressRegistry registry = account.getProgressRegistry();
		final BossBar bar = account.getPlayerBar();
		if (progress > 0) {
			registry.resetAll(blockPosition);
			int randomInts = registry.getRandomIntegers().get(blockPosition) != null ? registry.getRandomIntegers().get(blockPosition) : new Random().nextInt(999999999);
			PacketPlayOutBlockBreakAnimation packet = new PacketPlayOutBlockBreakAnimation(randomInts, blockPosition, -1);
			PacketEventListener.sendPacket(player, packet);
		}
		bar.removePlayer(player);
	}

}