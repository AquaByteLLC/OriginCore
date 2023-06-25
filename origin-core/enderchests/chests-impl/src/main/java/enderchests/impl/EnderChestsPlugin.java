package enderchests.impl;

import co.aikar.commands.PaperCommandManager;
import commons.CommonsPlugin;
import commons.events.api.EventRegistry;
import enderchests.ChestRegistry;
import enderchests.block.FakeBlockFactory;
import enderchests.IllusionRegistry;
import enderchests.impl.block.PacketBasedFakeBlockFactory;
import enderchests.impl.cmd.FakeBlockCommand;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author vadim
 */
public class EnderChestsPlugin extends JavaPlugin {

	private PaperCommandManager commands;
	private ChestRegistry     chestRegistry;
	private EnderChestHandler chestHandler;
	private IllusionRegistry  blockRegistry;
	private FakeBlockFactory  blockFactory;

	public ChestRegistry getChestRegistry() {
		return chestRegistry;
	}

	public IllusionRegistry getBlockRegistry() {
		return blockRegistry;
	}

	public FakeBlockFactory getBlockFactory() {
		return blockFactory;
	}

	@Override
	public void onLoad() {

	}

	@Override
	public void onEnable() {
		chestRegistry = new EnderChestRegistry();
		chestHandler  = new EnderChestHandler(chestRegistry);

		blockRegistry = new BlockIllusionRegistry();
		blockFactory  = new PacketBasedFakeBlockFactory();

		commands = new PaperCommandManager(this);
		commands.registerCommand(new FakeBlockCommand(blockRegistry, blockFactory));

		EventRegistry events = CommonsPlugin.commons().getEventRegistry();
		events.subscribeAll(chestHandler);
		events.subscribeAll(blockRegistry);
	}

	@Override
	public void onDisable() {

	}

}
