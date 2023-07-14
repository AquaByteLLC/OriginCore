package farming.impl.commands;

import blocks.block.BlockRegistry;
import blocks.block.regions.OriginRegion;
import blocks.block.regions.registry.RegionRegistry;
import blocks.impl.BlocksPlugin;
import blocks.impl.data.account.BlockAccount;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import farming.impl.Messages;
import farming.impl.conf.MessagesConfig;
import me.lucko.helper.text3.Text;
import me.vadim.util.conf.LiteConfig;
import me.vadim.util.conf.wrapper.Placeholder;
import me.vadim.util.conf.wrapper.impl.StringPlaceholder;
import org.bukkit.entity.Player;

@CommandAlias("farming")
public class RegionCommands extends BaseCommand {

	private final BlockRegistry registry;
	private final RegionRegistry regionRegistry;
	private final MessagesConfig messagesConfig;
	private final LiteConfig liteConfig;

	public RegionCommands(LiteConfig liteConfig, BlockRegistry registry, RegionRegistry regionRegistry) {
		this.registry = registry;
		this.liteConfig = liteConfig;
		this.messagesConfig = liteConfig.open(MessagesConfig.class);
		this.regionRegistry = regionRegistry;
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
			player.sendMessage(messagesConfig.getBlockNotFound().format(pl));
			return;
		}

		final World world = BukkitAdapter.adapt(player.getWorld());
		final RegionContainer regionContainer = WorldGuard.getInstance().getPlatform().getRegionContainer();
		final RegionManager regionManager = regionContainer.get(world);

		if (!regionManager.getRegions().containsKey(regionName)) {
			player.sendMessage(messagesConfig.getNoWgRegion().format(pl));
			return;
		}

		OriginRegion region = new OriginRegion(regionName, blockName, player.getWorld());
		region.newInstance(regionRegistry);
		player.sendMessage(messagesConfig.getRegionRegister().format(pl));
		Text.sendMessage(player, Messages.ORIGIN_REGION_ADDED);
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
			player.sendMessage(messagesConfig.getNoWgRegion().format(pl));
			return;
		}

		if (!regionRegistry.getRegions().containsKey(regionName)) {
			player.sendMessage(messagesConfig.getRegionNotRegistered().format(pl));
			return;
		}

		OriginRegion region = regionRegistry.getRegions().get(regionName).getRegion();
		player.sendMessage(messagesConfig.getRegionRemove().format(pl));
	}
}
