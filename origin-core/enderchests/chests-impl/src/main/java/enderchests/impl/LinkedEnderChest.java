package enderchests.impl;

import blocks.BlocksAPI;
import blocks.block.util.PlayerInteraction;
import blocks.impl.illusions.impl.FallingBlockOverlay;
import blocks.impl.illusions.impl.PacketBasedFakeBlock;
import commons.util.BukkitUtil;
import enderchests.ChestNetwork;
import enderchests.LinkedChest;
import enderchests.impl.data.EnderChestAccount;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.protocol.game.PacketPlayOutBlockAction;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.FaceAttachable;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R3.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_19_R3.util.CraftLocation;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.awt.Color;
import java.nio.ByteBuffer;
import java.util.UUID;

/**
 * @author vadim
 */
public class LinkedEnderChest extends PacketBasedFakeBlock implements LinkedChest {

	private static void sync(Runnable run) {
		Bukkit.getScheduler().runTask(EnderChestsPlugin.singletonCringe(), run);
	}

	private static void sync(Runnable run, long ticks) {
		Bukkit.getScheduler().runTaskLater(EnderChestsPlugin.singletonCringe(), run, ticks);
	}

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
		this.network = network;
		this.overlay = new FallingBlockOverlay(location, network.getColor().chatColor, Material.GLASS.createBlockData(), (player, click) -> {
			if(!isOwnedBy(player) && !player.isOp())
				return;
			if (click == PlayerInteraction.RIGHT_CLICK) {
				if (player.isSneaking()) {
					// place against
					ItemStack item = player.getItemInHand();
					Material type = item.getType();
					if (type.isItem() && type.isBlock() && !type.isAir()) {
						BlockFace f = player.getTargetBlockFace(5);
						if (f == null) return;
						Block place = getBlock().getRelative(f);

						if (player.getGameMode() != GameMode.CREATIVE)
							item.setAmount(item.getAmount() - 1);

						BlockData data = type.createBlockData();
						// handle special block placements
						// it's not perfect but it's close enough
						if (data instanceof Directional directional) {
							if (data instanceof FaceAttachable attachable) {
								if (BukkitUtil.isCube(place.getRelative(BlockFace.DOWN)))
									attachable.setAttachedFace(FaceAttachable.AttachedFace.FLOOR);
								else
									return;
							} else {
								f = f.getOppositeFace();
								if (directional.getFaces().contains(f))
									directional.setFacing(f);
								else
									return;
							}
						}

						sync(() -> {
							BlockState ogState = place.getState(true);
							BlockData ogData = place.getBlockData().clone();

							// hook into other plugins
							place.setBlockData(data);
							BlockPlaceEvent event = new BlockPlaceEvent(place, ogState, getBlock(), item, player, true, EquipmentSlot.HAND);
							Bukkit.getPluginManager().callEvent(event);
							if(event.isCancelled()) {
								place.setBlockData(ogData);
								ogState.update();
							} else {
								// trigger physics update ?
								((CraftWorld) place.getWorld()).getHandle().a(CraftLocation.toBlockPosition(place.getLocation()), ((CraftBlockData) data).getState(), 3);

								SoundGroup sg = data.getSoundGroup();
								player.playSound(place.getLocation(), sg.getPlaceSound(), sg.getVolume(), sg.getPitch());
								player.swingMainHand();
								player.setItemInHand(item);
							}
						}, 1);
					} else {
						sync(() -> open(player));
					}
				} else {
					// open inventory
					sync(() -> open(player));
				}
			}
			if (click == PlayerInteraction.LEFT_CLICK) {
				if (player.isSneaking()) {
					// manage menu
				} else {
					// break
					Location   loc = getBlockLocation().add(.5, .5, .5);
					SoundGroup sg  = Material.ENDER_CHEST.createBlockData().getSoundGroup();
					player.playEffect(loc, Effect.STEP_SOUND, Material.ENDER_CHEST);
//					player.playSound(getBlockLocation(), sg.getBreakSound(), sg.getVolume(), sg.getPitch());
//					player.spawnParticle(Particle.BLOCK_CRACK, loc, 75, 0.3, 0.3, 0.3, 0.6, Material.ENDER_CHEST.createBlockData());
					Color net = getNetwork().getColor().toColor();
					player.spawnParticle(Particle.DUST_COLOR_TRANSITION, loc, 1, new Particle.DustTransition(awt2bukkit(net.brighter()), awt2bukkit(net.darker()), 2));
					sync(() -> {
						getBlock().setType(Material.AIR);
						if (player.getGameMode() != GameMode.CREATIVE)
							player.getInventory().addItem(new ItemStack(Material.ENDER_CHEST));
						BlocksAPI.getInstance().getIllusions().globalRegistry().unregister(this);
						//todo: item
					});
				}
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
		if (account.isViewingLinkedInventory())
			account.currentLinkedInventory.close(player);
		account.currentLinkedInventory = this;
		player.openInventory(getInventory());
		getBlock().getWorld().playSound(getBlockLocation().add(.5, .5, .5), Sound.BLOCK_ENDER_CHEST_OPEN, .8f, .01f);
		for (Player onlinePlayer : Bukkit.getOnlinePlayers())
			BukkitUtil.sendPacket(onlinePlayer, getChestAction(true));
	}

	public void close(Player player) {
		EnderChestsPlugin.singletonCringe().getAccounts().getAccount(player).currentLinkedInventory = null;
		getBlock().getWorld().playSound(getBlockLocation().add(.5, .5, .5), Sound.BLOCK_END_PORTAL_SPAWN, .8f, 1.01f);
		for (Player onlinePlayer : Bukkit.getOnlinePlayers())
			BukkitUtil.sendPacket(onlinePlayer, getChestAction(false));
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
