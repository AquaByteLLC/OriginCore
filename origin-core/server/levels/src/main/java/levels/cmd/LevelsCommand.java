package levels.cmd;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Subcommand;
import commons.util.StringUtil;
import levels.Level;
import levels.LevelsPlugin;
import levels.data.LevelsAccount;
import levels.menus.ModernLevelsMenu;
import me.vadim.util.menu.Menu;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("levels")
public class LevelsCommand extends BaseCommand {

	private final LevelsPlugin plugin;

	public LevelsCommand(LevelsPlugin plugin) {
		this.plugin = plugin;
	}

	@Subcommand("menu")
	void menu(Player sender) {
		ModernLevelsMenu lvlm = new ModernLevelsMenu(plugin, sender);

		Menu menu = lvlm.getMenu();
		menu.generate();
		menu.open(sender);
	}

	@Subcommand("list")
	void list(CommandSender sender) {
		for (Level level : plugin.getLevelRegistry().getLevels())
			sender.sendMessage(level.levelNumber() + " @ " + level.getRequiredExperience() + " (" + level.getRewardCount() + "x)");
	}

	@Subcommand("addxp")
	void addXP(CommandSender sender, @Flags("other") Player target, float amount) {
		LevelsAccount account = plugin.getAccounts().getAccount(target);
		int lvl = account.getLevel();
		account.addExperience(amount);
		StringUtil.send(sender, String.format("&egave &b%s &d%.2f&e xp. &etarget level delta: &d%d &f-> &d%d", target.getName(), amount, lvl, account.getLevel()));
	}

	@Subcommand("addlvl")
	void addLvl(CommandSender sender, @Flags("other") Player target, int count) {
		LevelsAccount account = plugin.getAccounts().getAccount(target);
		int lvl = account.getLevel();
		account.addLevel(count);
		StringUtil.send(sender, String.format("&egave &b%s &d%d&e levels. &etarget level delta: &d%d &f-> &d%d", target.getName(), count, lvl, account.getLevel()));
	}

}
