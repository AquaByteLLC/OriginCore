package tools.impl.cmd;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import menu.ToolMenuBase;
import org.bukkit.entity.Player;
import tools.impl.ToolsPlugin;

/**
 * @author vadim
 */
@CommandAlias("testing")
public class TC extends BaseCommand {


	final ToolsPlugin plugin;

	public TC(ToolsPlugin plugin) {
		this.plugin = plugin;
	}

	@Default
	void thrusting(Player p){
		new ToolMenuBase(plugin, p.getInventory().getItemInMainHand()).getMenu().open(p);
		p.sendMessage("3x");
	}
}
