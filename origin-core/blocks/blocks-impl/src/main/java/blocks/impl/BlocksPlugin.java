package blocks.impl;

import blocks.BlocksAPI;
import blocks.block.BlockRegistry;
import blocks.block.aspects.location.registry.BlockLocationRegistry;
import blocks.block.aspects.overlay.registry.OverlayLocationRegistry;
import blocks.block.aspects.regeneration.registry.RegenerationRegistry;
import blocks.block.illusions.IllusionsAPI;
import blocks.block.progress.SpeedAttribute;
import blocks.block.progress.registry.ProgressRegistry;
import blocks.block.protect.ProtectionRegistry;
import blocks.block.protect.strategy.ProtectionStrategies;
import blocks.block.protect.strategy.ProtectionStrategy;
import blocks.block.regions.registry.RegionRegistry;
import blocks.impl.cmd.IllusionCommand;
import blocks.impl.cmd.ProtectCommand;
import blocks.impl.data.account.BlockAccount;
import blocks.impl.data.account.BlockAccountStorage;
import blocks.impl.anim.item.BreakSpeed;
import blocks.impl.data.region.RegionsStorage;
import blocks.impl.illusions.impl.Illusions;
import blocks.impl.protect.TransientProtectionRegistry;
import blocks.impl.registry.*;
import co.aikar.commands.PaperCommandManager;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import commons.Commons;
import commons.CommonsPlugin;
import commons.OriginModule;
import commons.data.account.AccountProvider;
import commons.data.account.AccountStorage;
import commons.events.api.EventRegistry;
import commons.util.ReflectUtil;
import lombok.Getter;
import me.vadim.util.conf.ConfigurationManager;
import me.vadim.util.conf.ResourceProvider;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public class BlocksPlugin extends JavaPlugin implements ResourceProvider, OriginModule {

	private static Injector injector;

	public static Injector get() {
		if (injector == null) {
			try {
				throw new Exception("The BlocksAPI hasn't been initialized anywhere. Create a new instance of the BlocksAPI class in the 'onEnable' method.");
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return injector;
	}

	private RegenerationRegistry regenerationRegistry;
	private IllusionsAPI illusions;
	private BlockRegistry blockRegistry;
	private OverlayLocationRegistry overlayLocationRegistry;
	private BlockLocationRegistry blockLocationRegistry;
	private ProgressRegistry progressRegistry;
	private SpeedAttribute speedAttribute;
	private RegionRegistry regionRegistry;
	private ProtectionRegistry protectionRegistry;
	private BlockAccountStorage accountStorage;
	private RegionsStorage regionsStorage;
	private PaperCommandManager commands; // op-only debug commands

	@Override
	public void onEnable() {
		final CommonsPlugin commonsPlugin = CommonsPlugin.commons();

		this.blockRegistry           = new BlockRegistryImpl();
		this.overlayLocationRegistry = new OverlayRegistryImpl();
		this.illusions               = new Illusions(this, Commons.events());
		this.regenerationRegistry    = new RegenerationRegistryImpl();
		this.blockLocationRegistry   = new LocationRegistryImpl();
		this.progressRegistry        = new ProgressRegistryImpl();
		this.speedAttribute          = new BreakSpeed();
		this.regionRegistry          = new RegionRegistryImpl();
		this.protectionRegistry      = new TransientProtectionRegistry();

		injector = Guice.createInjector(new BlockModule(new BlocksAPI(this, blockLocationRegistry, illusions, regenerationRegistry, blockRegistry, overlayLocationRegistry, progressRegistry, speedAttribute, regionRegistry, protectionRegistry), this));

		accountStorage = new BlockAccountStorage(Commons.db());
		regionsStorage = new RegionsStorage(Commons.db(), regionRegistry);
		commonsPlugin.registerModule(this);

		commands = new PaperCommandManager(this);
		commands.getCommandCompletions().registerCompletion("strategy", c -> {
			List<String> completions = new ArrayList<>();
			completions.addAll(Bukkit.getOnlinePlayers().stream().map(Player::getName).toList());
			completions.addAll(Arrays.stream(ProtectionStrategies.values()).map(ProtectionStrategies::name).toList());
			completions.add("PLAYERS");
			return completions;
		});
		commands.getCommandContexts().registerContext(ProtectionStrategy.class, c -> {
			String arg = c.popFirstArg();
			if (arg.equalsIgnoreCase("PLAYERS"))
				return ProtectionStrategies.PERMIT_PLAYERS;
			Player player = Bukkit.getPlayer(arg);
			if (player != null)
				return ProtectionStrategies.permitOwner(player);
			return ReflectUtil.getEnum(ProtectionStrategies.class, arg.toUpperCase().replace(' ', '_'));
		});
		commands.registerCommand(new IllusionCommand(illusions));
		commands.registerCommand(new ProtectCommand(protectionRegistry));

		regionsStorage.load();
	}

	@Override
	public void onDisable() {
		regionsStorage.save();
	}

	@Override
	public void onSave() throws Exception {
		regionsStorage.save();
	}

	@Override
	public AccountStorage<BlockAccount> getAccounts() {
		return accountStorage;
	}

	@Override
	public ConfigurationManager getConfigurationManager() {
		return null;
	}

	static class BlockModule extends AbstractModule {

		private final BlocksAPI blocksAPI;
		private final BlocksPlugin blocksPlugin;

		BlockModule(BlocksAPI blocks, BlocksPlugin blocksPlugin) {
			this.blocksAPI    = blocks;
			this.blocksPlugin = blocksPlugin;
		}

		@Override
		protected void configure() {
			this.bind(BlocksAPI.class).toInstance(blocksAPI);
			this.bind(BlocksPlugin.class).toInstance(blocksPlugin);
		}

	}

}
