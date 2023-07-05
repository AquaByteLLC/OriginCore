package enderchests.impl;

import blocks.BlocksAPI;
import co.aikar.commands.PaperCommandManager;
import commons.CommonsPlugin;
import commons.data.AccountProvider;
import commons.data.AccountStorage;
import commons.events.api.EventRegistry;
import enderchests.ChestRegistry;
import enderchests.impl.cmd.EnderChestCommand;
import enderchests.impl.cmd.IllusionCommand;
import enderchests.impl.conf.Config;
import enderchests.impl.data.EChestAccountStorage;
import enderchests.impl.data.EnderChestAccount;
import me.vadim.util.conf.LiteConfig;
import me.vadim.util.conf.ResourceProvider;
import net.minecraft.network.protocol.game.PacketPlayInEntityAction;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author vadim
 */
public class EnderChestsPlugin extends JavaPlugin implements ResourceProvider {

	private static EnderChestsPlugin badDesignPatterns;

	public static EnderChestsPlugin singletonCringe() {
		return badDesignPatterns;
	}

	private final LiteConfig lfc = new LiteConfig(this);
	private PaperCommandManager commands;
	private ChestRegistry     chestRegistry;
	private EnderChestHandler chestHandler;
	private EChestAccountStorage accountStorage;

	public Config config() {
		return lfc.open(Config.class);
	}

	public ChestRegistry getChestRegistry() {
		return chestRegistry;
	}

	public AccountProvider<EnderChestAccount> getAccounts() {
		return accountStorage;
	}

	@Override
	public void onLoad() {
		badDesignPatterns = this;
	}

	@Override
	public void onEnable() {
		lfc.register(Config.class, Config::new);
		lfc.reload();

		accountStorage = new EChestAccountStorage(lfc, CommonsPlugin.commons().getDatabase());
		chestRegistry = new EnderChestRegistry(lfc, accountStorage);
		chestHandler  = new EnderChestHandler(this, chestRegistry, accountStorage, CommonsPlugin.commons().getEventRegistry());

		commands = new PaperCommandManager(this);
		commands.registerCommand(new IllusionCommand(BlocksAPI.getInstance().getIllusions()));
		commands.registerCommand(new EnderChestCommand(chestRegistry, accountStorage));
	}

	@Override
	public void onDisable() {

	}

}
