package enderchests.impl.cmd;

import blocks.block.util.PlayerInteraction;
import blocks.block.illusions.*;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import commons.util.BukkitUtil;
import net.minecraft.network.protocol.game.PacketPlayOutBlockAction;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.craftbukkit.v1_19_R3.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_19_R3.util.CraftLocation;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;

/**
 * @author vadim
 */
@CommandAlias("illusion")
public class IllusionCommand extends BaseCommand {

	private final IllusionRegistry registry;
	private final IllusionFactory factory;

	public IllusionCommand(IllusionsAPI api) {
		this.registry = api.registry();
		this.factory  = api.factory();
	}

	@Subcommand("fakeblock")
	void set(Player sender, Material material, @Optional BlockFace face) {
		if(!material.isBlock()) {
			sender.sendMessage("not a block bruh");
			return;
		}

		Block block = sender.getTargetBlockExact(10);
		if(block == null) {
			sender.sendMessage("look at a block ._.");
			return;
		}
		BlockData data = material.createBlockData();

		if(data instanceof Directional directional && face != null)
			directional.setFacing(face);

		FakeBlock fake = factory.newFakeBlock(block.getLocation(), data);

		registry.register(fake);
		sender.sendBlockChange(fake.getBlockLocation(), fake.getProjectedBlockData());
		sender.sendMessage("done");
	}

	private static void chestToggle(Player player, PlayerInteraction click, Location location) {
		PacketPlayOutBlockAction action = new PacketPlayOutBlockAction(CraftLocation.toBlockPosition(location),
																	   ((CraftBlockData) Material.ENDER_CHEST.createBlockData()).getState().b(),
																	   1, click == PlayerInteraction.RIGHT_CLICK ? 1 : 0);
		BukkitUtil.sendPacket(player, action);
	}

	@Subcommand("highlight")
	void highlight(Player sender, ChatColor color) {
		Block block = sender.getTargetBlockExact(10);
		if (block == null) {
			sender.sendMessage("look at a block ._.");
			return;
		}

		Location location = block.getLocation();
		FakeBlock fake = factory.newHighlightedBlock(location, color, (Player player, PlayerInteraction click) -> chestToggle(player, click, location));
		registry.register(fake);
	}

	@Subcommand("overlay")
	void overlay(Player sender, Material material) {
		Block block = sender.getTargetBlockExact(10);
		if(block == null) {
			sender.sendMessage("look at a block ._.");
			return;
		}

		Location location = block.getLocation();
		FakeBlock fake = factory.newOverlayedBlock(location, material.createBlockData(), (Player player, PlayerInteraction click) -> chestToggle(player, click, location));
		registry.register(fake);
	}

	@Subcommand("reset")
	void del(Player sender) {
		boolean did = false;

		Block block = sender.getTargetBlockExact(10);
		if(block != null) {
			if(unset(sender, block.getLocation())) {
				sender.sendBlockChange(block.getLocation(), block.getBlockData());
				did = true;
			}
		}

		Entity entity = sender.getTargetEntity(10, true);
		if(entity instanceof FallingBlock fb) {
			if(registry.isOverlayEntity(fb) && unset(sender, entity.getLocation())) {
				entity.remove();
				did = true;
			}
		}

		if(!did)
			sender.sendMessage("bruh '_'");
		else
			sender.sendMessage("done");
	}

	private boolean unset(Player sender, Location location) {
		FakeBlock fake = registry.getBlockAt(location);
		if(fake == null)
			return false;
		registry.unregister(fake);
		return true;
	}

}
