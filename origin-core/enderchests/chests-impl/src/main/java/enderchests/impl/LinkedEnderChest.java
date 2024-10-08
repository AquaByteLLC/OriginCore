package enderchests.impl;

import blocks.BlocksAPI;
import blocks.block.protect.ProtectedBlock;
import blocks.block.protect.ProtectedObject;
import blocks.block.protect.ProtectionRegistry;
import blocks.block.protect.strategy.ProtectionStrategies;
import blocks.block.util.PlayerInteraction;
import blocks.impl.illusions.impl.FallingBlockOverlay;
import blocks.impl.illusions.impl.PacketBasedFakeBlock;
import commons.Commons;
import commons.data.account.AccountProvider;
import commons.util.BukkitUtil;
import enderchests.ChestNetwork;
import enderchests.ChestRegistry;
import enderchests.LinkedChest;
import enderchests.impl.conf.Config;
import enderchests.impl.conf.EnderChestSettings;
import enderchests.impl.data.EnderChestAccount;
import me.vadim.util.conf.ConfigurationProvider;
import me.vadim.util.conf.bukkit.wrapper.EffectGroup;
import net.minecraft.network.protocol.game.PacketPlayOutBlockAction;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Particle;
import org.bukkit.SoundGroup;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
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
import settings.EnumeratedSetting;

import java.awt.*;
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

	private final AccountProvider<EnderChestAccount> accounts;
	private final ChestNetwork network;
	private final FallingBlockOverlay overlay;
	private final ChestRegistry registry;
	private final ConfigurationProvider conf;

	LinkedEnderChest(Location location, AccountProvider<EnderChestAccount> accounts, BlockFace face, ChestNetwork network, ChestRegistry registry, ConfigurationProvider conf) {
		super(location, blockDataFromFaceParameter(face), null);
		this.accounts = accounts;
		this.network  = network;
		this.overlay  = new FallingBlockOverlay(location, network.getColor().chatColor, null, (player, click) -> {
			if (click == PlayerInteraction.RIGHT_CLICK)
				rightClick(player);
			if (click == PlayerInteraction.LEFT_CLICK)
				leftClick(player);
		});
		this.registry = registry;
		this.conf     = conf;
	}

	private Config config() {
		return conf.open(Config.class);
	}

	public void leftClick(Player player) {
		if (!isOwnedBy(player) && !player.isOp())
			return;
		EnderChestAccount account = accounts.getAccount(player);
		if (player.isSneaking()) {
			// manage menu
		} else {
			// break
			Location loc = getBlockLocation().add(.5, .5, .5);
			player.playEffect(loc, Effect.STEP_SOUND, Material.ENDER_CHEST);
			Color net = getNetwork().getColor().toColor();
			player.spawnParticle(Particle.DUST_COLOR_TRANSITION, loc, 1, new Particle.DustTransition(awt2bukkit(net.brighter()), awt2bukkit(net.darker()), 2));
			Commons.scheduler().getBukkitSync().runTask(() -> {
				getBlock().setType(Material.AIR);
				if (player.getGameMode() != GameMode.CREATIVE)
					player.getInventory().addItem(config().getEnderChestItem());
				BlocksAPI.getInstance().getIllusions().globalRegistry().unregister(this);
				registry.deleteChest(this);
			});
		}
	}

	public void rightClick(Player player) {
		if (!isOwnedBy(player) && !player.isOp())
			return;
		EnderChestAccount account = accounts.getAccount(player);
		if (player.isSneaking()) {
			// place against
			ItemStack item = player.getItemInHand();
			Material  type = item.getType();
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

				Commons.scheduler().getBukkitSync().runLater(() -> {
					BlockState ogState = place.getState(true);
					BlockData  ogData  = place.getBlockData().clone();

					// hook into other plugins
					place.setBlockData(data);
					BlockPlaceEvent event = new BlockPlaceEvent(place, ogState, getBlock(), item, player, true, EquipmentSlot.HAND);
					Bukkit.getPluginManager().callEvent(event);
					if (event.isCancelled()) {
						place.setBlockData(ogData);
						ogState.update();
					} else {
						// trigger physics update ?
						((CraftWorld) place.getWorld()).getHandle().a(CraftLocation.toBlockPosition(place.getLocation()), ((CraftBlockData) place.getBlockData()).getState(), 3);

						SoundGroup sg = data.getSoundGroup();
						player.playSound(place.getLocation(), sg.getPlaceSound(), sg.getVolume(), sg.getPitch());
						player.swingMainHand();
						player.setItemInHand(item);
					}
				}, 1);
			} else {
				account.openNewLinkedInventory(this);
			}
		} else {
			// open inventory
			account.openNewLinkedInventory(this);
		}
	}

	@Override
	public void setOpen(boolean isOpen) {
		EffectGroup effect;
		if (isOpen)
			effect = config().getOpenEffect();
		else
			effect = config().getShutEffect();
		effect.sendAtIf(getBlockLocation().add(.5, .5, .5), EnderChestSettings.SOUNDS::isEnabled,
						getBlockLocation(), EnumeratedSetting::never);
		for (Player onlinePlayer : Bukkit.getOnlinePlayers())
			BukkitUtil.sendPacket(onlinePlayer, new PacketPlayOutBlockAction(CraftLocation.toBlockPosition(getBlockLocation()),
																			 ((CraftBlockData) Material.ENDER_CHEST.createBlockData()).getState().b(),
																			 1, isOpen ? 1 : 0));
	}

	@Override
	public void updateAnimation() {
		setOpen(!getInventory().getViewers().isEmpty());
	}

	@Override
	public void validateBlock() {
		ProtectionRegistry protection = BlocksAPI.getInstance().getProtectionRegistry();
		ProtectedObject    object     = protection.getActiveProtection(getBlock());
		if (object instanceof ProtectedBlock block)
			protection.release(block);

		BlocksAPI.getInstance().getIllusions().globalRegistry().register(this);

		getBlock().setType(Material.CHEST);
		Inventory inventory = ((Chest) getBlock().getState()).getBlockInventory();
		inventory.clear();
		ItemStack blank = new ItemStack(Material.STONE);
		ItemUtil.mark(blank); // no reason
		inventory.addItem(blank); // add dummy item to trigger inventory move event

		ProtectedBlock block = protection.defineBlock(getBlock());
		block.setProtectionStrategy(ProtectionStrategies.permitOwner(getOfflineOwner()));
	}

	@Override
	public ChestNetwork getNetwork() {
		return network;
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

	/* constructor gayness bypass */

	@Override
	public FallingBlockOverlay getOverlay() {
		return overlay;
	}

	@Override
	public boolean hasOverlay() {
		return true;
	}

	@Override
	public Directional getProjectedBlockData() {
		return (Directional) super.getProjectedBlockData(); // this will always be ENDER_CHEST
	}

}
