package enchants.impl.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("enchants")
public class EnchantCommands extends BaseCommand {

	@Subcommand("types")
	public void sendTypes(CommandSender sender) {

	}

	@Subcommand("menu")
	public void openMenu(Player player) {

	}


}
