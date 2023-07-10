package blocks.impl.cmd;

import blocks.block.protect.ProtectedBlock;
import blocks.block.protect.ProtectedObject;
import blocks.block.protect.ProtectedRegion;
import blocks.block.protect.ProtectionRegistry;
import blocks.block.protect.strategy.ProtectionStrategy;
import blocks.block.util.Cuboid;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.SessionManager;
import commons.util.StringUtil;
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

	private static final String BLOCKS = "&7[&bBLOCKS&7]&r ";
	private static final String REGION = "&7[&bREGION&7]&r ";
	private static final String PROTECTION = "&7[&bPROTECTION&7]&r ";

	@Subcommand("block strategy set")
	@CommandCompletion("@strategy")
	void setBlockStrategy(Player player, ProtectionStrategy strategy) {
		if (strategy == null) {
			StringUtil.send(player, PROTECTION + "&cunknown protection strategy");
			return;
		}

		Block target = player.getTargetBlockExact(10);
		if (target == null) {
			StringUtil.send(player, PROTECTION + "&clook at a block");
			return;
		}

		ProtectedBlock block = registry.defineBlock(target);

		block.setProtectionStrategy(strategy);
		StringUtil.send(player, BLOCKS + "&aprotection strategy updated to '&b" + block.getProtectionStrategy() + "&a'");
	}

	@Subcommand("block release")
	void releaseBlock(Player player) {
		Block target = player.getTargetBlockExact(10);
		if (target == null) {
			StringUtil.send(player, PROTECTION + "&clook at a block");
			return;
		}

		ProtectedObject object = registry.getActiveProtection(target);
		if (object instanceof ProtectedBlock block) {
			registry.release(block);
			StringUtil.send(player, BLOCKS + "&aprotection strategy '&b" + block.getProtectionStrategy() + "&a' removed");
		} else {
			StringUtil.send(player, BLOCKS + "&cnot protected");
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
			StringUtil.send(player, REGION + "&cselect a cubiod region first");
			return null;
		}
		World        world = BukkitAdapter.adapt(selectionWorld);
		BlockVector3 min   = selection.getBoundingBox().getMinimumPoint();
		BlockVector3 max   = selection.getBoundingBox().getMaximumPoint();

		Cuboid cuboid = new Cuboid(BukkitAdapter.adapt(world, min), BukkitAdapter.adapt(world, max));
		return registry.defineRegion(world, cuboid);
	}

	@Subcommand("region strategy set")
	@CommandCompletion("@strategy")
	void setRegionStrategy(Player player, ProtectionStrategy strategy) {
		if (strategy == null) {
			StringUtil.send(player, PROTECTION + "&cunknown protection strategy");
			return;
		}

		ProtectedRegion region = getRegion(player);
		region.setProtectionStrategy(strategy);
		StringUtil.send(player, REGION + "&aprotection strategy updated to '&b" + region.getProtectionStrategy() + "&a'");
	}

	@Subcommand("region priority set")
	void setRegionPriority(Player player, int priority) {
		ProtectedRegion region = getRegion(player);
		region.setPriority(priority);
		StringUtil.send(player, REGION + "&apriority updated to &b" + region.getPriority());
	}

	@Subcommand("region release")
	void releaseRegion(Player player) {
		Block target = player.getTargetBlockExact(10);
		if (target == null) {
			StringUtil.send(player, PROTECTION + "&clook at a block");
			return;
		}

		ProtectedObject object = registry.getActiveProtection(target);
		if (object instanceof ProtectedRegion region) {
			registry.release(region);
			StringUtil.send(player, REGION + "&aprotection strategy '&b" + region.getProtectionStrategy() + "&a' with priority &b" + region.getPriority() + "&a removed");
		} else {
			StringUtil.send(player, REGION + "&cnot protected");
		}
	}

	@Subcommand("status")
	void sendStatus(Player player) {
		Block target = player.getTargetBlockExact(10);
		if (target == null) {
			StringUtil.send(player, PROTECTION + "&clook at a block");
			return;
		}

		// this shit is AIDS, please do not scroll further

		ProtectedObject[] prot = registry.getAllProtection(target);

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
			} else if (object instanceof ProtectedBlock b)
				block = b;

		if (block != null)
			StringUtil.send(player, BLOCKS + "&aprotected by '&b" + block.getProtectionStrategy() + "&a'");
		if (region != null)
			StringUtil.send(player, REGION + "&aprotected by &b" + rg_ct + "&a region" + (rg_ct == 1 ? "" : "s") + ", with the highest" + (block != null ? " BELOW block" : "") + " being '&b" + region.getProtectionStrategy() + "&a' (with priority &b" + region.getPriority() + "&a)");
		if (block == null && region == null)
			StringUtil.send(player, PROTECTION + "&cnot protected");
	}

}
