package blocks.impl.cmd;

import blocks.block.protect.ProtectedBlock;
import blocks.block.protect.ProtectedObject;
import blocks.block.protect.ProtectedRegion;
import blocks.block.protect.ProtectionRegistry;
import blocks.block.protect.ProtectionStrategy;
import blocks.block.util.Cuboid;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.SessionManager;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 * @author vadim
 */
@CommandAlias("protect")
@CommandPermission("*")
public class ProtectCommand extends BaseCommand {

	private final ProtectionRegistry registry;

	public ProtectCommand(ProtectionRegistry registry) {
		this.registry = registry;
	}

	@Subcommand("block")
	void setBlockStrategy(Player player, @Optional ProtectionStrategy strategy) {
		Block target = player.getTargetBlockExact(10);
		if (target == null) {
			player.sendMessage("look at a block");
			return;
		}

		ProtectedBlock block = registry.protectBlock(target);
		if (strategy == null) {
			registry.removeProtection(block);
			player.sendMessage("protection removed");
		} else {
			block.setProtectionStrategy(strategy);
			player.sendMessage("protection strategy updated to " + strategy);
		}
	}

	ProtectedRegion getRegion(Player player) {
		com.sk89q.worldedit.entity.Player actor = BukkitAdapter.adapt(player);

		SessionManager manager      = WorldEdit.getInstance().getSessionManager();
		LocalSession   localSession = manager.get(actor);
		Region         selection;

		com.sk89q.worldedit.world.World selectionWorld = localSession.getSelectionWorld();
		try {
			if (selectionWorld == null) throw new IncompleteRegionException();
			selection = localSession.getSelection(selectionWorld);
		} catch (IncompleteRegionException ex) {
			player.sendMessage("selecte a region");
			return null;
		}
		World        world = BukkitAdapter.adapt(selectionWorld);
		BlockVector3 min   = selection.getBoundingBox().getMinimumPoint();
		BlockVector3 max   = selection.getBoundingBox().getMaximumPoint();

		Cuboid cuboid = new Cuboid(BukkitAdapter.adapt(world, min), BukkitAdapter.adapt(world, max));
		return registry.protectRegion(world, cuboid);
	}

	@Subcommand("region")
	void setRegionStrategy(Player player, @Optional ProtectionStrategy strategy) {
		ProtectedRegion region = getRegion(player);
		if (strategy == null) {
			registry.removeProtection(region);
			player.sendMessage("protection removed");
		} else {
			region.setProtectionStrategy(strategy);
			player.sendMessage("protection strategy updated to " + strategy);
		}
	}

	@Subcommand("priority")
	void setRegionPriority(Player player, int priority) {
		ProtectedRegion region = getRegion(player);
		region.setPriority(priority);
		player.sendMessage("priority set to " + priority);
	}

	@Subcommand("what")
	void printStatus(Player player) {
		Block target = player.getTargetBlockExact(10);
		if (target == null) {
			player.sendMessage("look at a block");
			return;
		}

		// this shit is AIDS, please do not scroll further

		ProtectedObject[] prot = registry.getProtectionAt(target);

		ProtectedBlock  block  = null;
		ProtectedRegion region = null;

		int rg_ct = 0;
		for (ProtectedObject object : prot)
			if (object instanceof ProtectedRegion rg) {
				if (region == null)
					region = rg;
				if (rg.getPriority() > region.getPriority())
					region = rg;
				rg_ct++;
			} else
				block = (ProtectedBlock) object;

		if (block != null)
			player.sendMessage("protected by BLOCK " + block.getProtectionStrategy());
		if (region != null)
			player.sendMessage("protected by " + rg_ct + " REGION" + (rg_ct == 1 ? "" : "S") + " (highest priority" + (block != null ? " BELOW block" : "") + " is " + region.getPriority() + " -> " + region.getProtectionStrategy() + ")");
		if (block == null && region == null)
			player.sendMessage("not protected");
	}

}
