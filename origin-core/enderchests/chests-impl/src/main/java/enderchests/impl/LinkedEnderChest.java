package enderchests.impl;

import blocks.BlocksAPI;
import blocks.block.illusions.BlockOverlay;
import blocks.block.util.PlayerInteraction;
import blocks.impl.illusions.impl.FallingBlockOverlay;
import blocks.impl.illusions.impl.PacketBasedFakeBlock;
import commons.util.BukkitUtil;
import enderchests.ChestNetwork;
import enderchests.LinkedChest;
import enderchests.impl.data.EnderChestAccount;
import net.minecraft.network.protocol.game.PacketPlayOutBlockAction;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.craftbukkit.v1_19_R3.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_19_R3.util.CraftLocation;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.awt.Color;
import java.util.UUID;

/**
 * @author vadim
 */
public class LinkedEnderChest extends PacketBasedFakeBlock implements LinkedChest {

	private static org.bukkit.Color awt2bukkit(Color color) {
		return org.bukkit.Color.fromRGB(color.getRed(), color.getGreen(), color.getBlue());
	}

	private static BlockData blockDataFromFaceParameter(BlockFace face) {
		Directional data = (Directional) Material.ENDER_CHEST.createBlockData();
		switch (face) {
			case NORTH, SOUTH, EAST, WEST -> data.setFacing(face);
			default -> throw new IllegalArgumentException("illegal block face " + face + " for type chest");
		}
		return data;
	}
	private final ChestNetwork network;
	private final FallingBlockOverlay overlay;

	LinkedEnderChest(Location location, BlockFace face, ChestNetwork network) {
		super(location, blockDataFromFaceParameter(face), null);
		this.network  = network;
		this.overlay = new FallingBlockOverlay(location, network.getColor().chatColor, Material.GLASS.createBlockData(), (player, click) -> {
			//todo: sound
			if(click == PlayerInteraction.RIGHT_CLICK) {
				Bukkit.getScheduler().runTask(EnderChestsPlugin.singletonCringe(), () -> open(player));
			}
			if(click == PlayerInteraction.LEFT_CLICK) {
				//break
				Location loc = getBlockLocation().add(.5, .5, .5);
				player.playSound(getBlockLocation(), Sound.BLOCK_STONE_BREAK, 1f, 1f);
				player.spawnParticle(Particle.BLOCK_CRACK, loc, 75, 0.3, 0.3, 0.3, 0.6, Material.ENDER_CHEST.createBlockData());
				Color net = getNetwork().getColor().toColor();
				player.spawnParticle(Particle.DUST_COLOR_TRANSITION, loc, 1, new Particle.DustTransition(awt2bukkit(net.brighter()), awt2bukkit(net.darker()), 2));
				Bukkit.getScheduler().runTask(EnderChestsPlugin.singletonCringe(), () -> {
					getBlock().setType(Material.AIR);
					if(player.getGameMode() != GameMode.CREATIVE)
						player.getInventory().addItem(new ItemStack(Material.ENDER_CHEST));
					BlocksAPI.getInstance().getIllusions().globalRegistry().unregister(this);
					//todo: item
				});
			}
		});
	}

	private final PacketPlayOutBlockAction getChestAction(boolean isOpen) {
		return new PacketPlayOutBlockAction(CraftLocation.toBlockPosition(getBlockLocation()),
											 ((CraftBlockData) Material.ENDER_CHEST.createBlockData()).getState().b(),
											 1, isOpen ? 1 : 0);
	}

	public void open(Player player) {
		EnderChestAccount account = EnderChestsPlugin.singletonCringe().getAccounts().getAccount(player);
		if(account.isViewingLinkedInventory())
			account.currentLinkedInventory.close(player);
		account.currentLinkedInventory = this;
		player.openInventory(getInventory());
		BukkitUtil.sendPacket(player, getChestAction(true));
	}

	public void close(Player player) {
		EnderChestsPlugin.singletonCringe().getAccounts().getAccount(player).currentLinkedInventory = null;
		BukkitUtil.sendPacket(player, getChestAction(false));
	}

	@Override
	public ChestNetwork getNetwork() {
		return network;
	}

	/* constructor gayness bypass */

	@Override
	public FallingBlockOverlay getOverlay() {
		return overlay;
	}

	@Override
	public boolean hasOverlay() {
		return true;
	}

	/* delegate impl to network */

	@Override
	public OfflinePlayer getOfflineOwner() {
		return network.getOfflineOwner();
	}

	@Override
	public UUID getOwnerUUID() {
		return network.getOwnerUUID();
	}

	@Override
	public boolean isOwnedBy(OfflinePlayer test) {
		return network.isOwnedBy(test);
	}

	@Override
	public boolean isOwnedBy(UUID test) {
		return network.isOwnedBy(test);
	}

	@Override
	public Inventory getInventory() {
		return network.getInventory();
	}

}
