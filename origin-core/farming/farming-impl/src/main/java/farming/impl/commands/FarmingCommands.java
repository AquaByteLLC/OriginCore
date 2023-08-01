package farming.impl.commands;

import blocks.block.BlockRegistry;
import blocks.block.regions.OriginRegion;
import blocks.block.regions.registry.RegionRegistry;
import blocks.impl.BlocksPlugin;
import blocks.impl.data.account.BlockAccount;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import commons.versioning.VersionSender;
import farming.impl.action.Messages;
import farming.impl.hoe.OriginHoe;
import me.vadim.util.conf.wrapper.Placeholder;
import me.vadim.util.conf.wrapper.impl.StringPlaceholder;
import org.bukkit.entity.Player;

@CommandAlias("farming")
public class FarmingCommands extends BaseCommand {

	private final BlockRegistry registry;
	private final RegionRegistry regionRegistry;
	private final VersionSender sender;

	public FarmingCommands(BlockRegistry registry, RegionRegistry regionRegistry) {
		this.registry = registry;
		this.regionRegistry = regionRegistry;
		this.sender = Messages.versionSender;
	}

	@Subcommand("tool give")
	public void giveHoe(@Flags("other") Player target) {
		OriginHoe hoe = new OriginHoe();
		hoe.give(target);
	}

	@Subcommand("region register")
	@CommandPermission("origin.admin.regions")
	@Syntax("<regionName> <blockName>")
	public void registerRegion(Player player, String regionName, String blockName) {
		final Placeholder pl = StringPlaceholder.builder()
				.set("region_name", regionName)
				.set("block_name", blockName)
				.build();

		if (!registry.getBlocks().containsKey(blockName)) {
			sender.sendMessage(player, Messages.BLOCK_NOT_FOUND, pl);
			return;
		}

		final World world = BukkitAdapter.adapt(player.getWorld());
		final RegionContainer regionContainer = WorldGuard.getInstance().getPlatform().getRegionContainer();
		final RegionManager regionManager = regionContainer.get(world);

		if (!regionManager.getRegions().containsKey(regionName)) {
			sender.sendMessage(player, Messages.WG_REGION_NOT_FOUND, pl);
			return;
		}

		OriginRegion region = new OriginRegion(regionName, blockName, player.getWorld());
		sender.sendMessage(player, Messages.REGION_REGISTER, pl);
		region.newInstance(regionRegistry);
	}

	@Subcommand("region unregister")
	@CommandPermission("origin.admin.regions")
	@Syntax("<regionName>")
	public void registerRegion(Player player, String regionName) {
		final World world = BukkitAdapter.adapt(player.getWorld());
		final RegionContainer regionContainer = WorldGuard.getInstance().getPlatform().getRegionContainer();
		final RegionManager regionManager = regionContainer.get(world);
		final BlockAccount account = BlocksPlugin.get().getInstance(BlocksPlugin.class).getAccounts().getAccount(player);

		final Placeholder pl = StringPlaceholder.builder()
				.set("region_name", regionName)
				.build();

		if (!regionManager.getRegions().containsKey(regionName)) {
			sender.sendMessage(player, Messages.WG_REGION_NOT_FOUND, pl);
			return;
		}

		if (!regionRegistry.getRegions().containsKey(regionName)) {
			sender.sendMessage(player, Messages.REGION_NOT_REGISTERED, pl);
			return;
		}

		OriginRegion region = regionRegistry.getRegions().get(regionName).getRegion();
		sender.sendMessage(player, Messages.REGION_REMOVE, pl);
	}
}
