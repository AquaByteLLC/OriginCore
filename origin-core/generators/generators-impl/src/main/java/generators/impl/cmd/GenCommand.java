package generators.impl.cmd;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import commons.StringUtil;
import commons.data.AccountProvider;
import commons.data.AccountStorage;
import generators.impl.GenRegistry;
import generators.impl.conf.Tiers;
import generators.impl.data.GenAccount;
import generators.impl.data.GenStorage;
import generators.impl.menu.TiersMenu;
import generators.impl.wrapper.Gen;
import generators.wrapper.Tier;
import me.vadim.util.conf.ConfigurationProvider;
import me.vadim.util.menu.Menu;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.concurrent.ExecutorService;

/**
 * @author vadim
 */
@CommandAlias("gen")
public class GenCommand extends BaseCommand {


	private final Plugin      plugin;
	private final GenStorage  genStorage;
	private final GenRegistry reg;

	private final ConfigurationProvider       conf;
	private final AccountProvider<GenAccount> accounts;

	public GenCommand(Plugin plugin, GenStorage genStorage, GenRegistry reg, ConfigurationProvider conf, AccountProvider<GenAccount> accounts) {
		this.plugin     = plugin;
		this.genStorage = genStorage;
		this.reg        = reg;
		this.conf       = conf;
		this.accounts   = accounts;
	}

	@Subcommand("setmaxslots")
	public void setMaxSlots(Player sender, int maxSlots) {
		accounts.getAccount(sender).slotLimit = maxSlots;
	}

	@Subcommand("getmaxslots")
	public void getMaxSlots(Player sender) {
		StringUtil.send(sender, "&enum slots: &b" + accounts.getAccount(sender).slotLimit);
	}

	@Subcommand("save-all")
	public void flushAndSave(Player sender) {
		Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
			((AccountStorage<GenAccount>) accounts).flushAndSave();
			genStorage.save();
			StringUtil.send(sender, "&ddone xP");
		});
	}

	@Subcommand("stress")
	public void stressTest(Player sender, int k) {
		Tier tier = conf.open(Tiers.class).findTier(sender.getItemInHand().getType());

		if (tier == null) {
			sender.sendMessage("blud");
			return;
		}

		Location L  = new Location(sender.getWorld(), 0, 0, 0);
		Location sL = sender.getLocation();
		double
				sX = sL.getX(),
				sY = sL.getY(),
				sZ = sL.getZ();
		double each = Math.pow(k * 1000, 1.0 / 3.0);
		for (int x = 0; x < each; x++) {
			for (int y = 0; y < each; y++) {
				for (int z = 0; z < each; z++) {
					L.set(sX + x, sY + y, sZ + z);
					reg.createGen(new Gen(sender, tier, L));
				}
			}
		}

		StringUtil.send(sender, "&acreated &b~" + k + "k&a gens");
	}

	@Subcommand("ctrl+a delete")
	public void deleteAll(Player sender) {
		reg.all().forEachRemaining(reg::deleteGen);
		sender.sendMessage("hehehehaw");
	}

	@Subcommand("menu")
	public void menu(Player sender) {
		Menu menu = new TiersMenu(conf).getMenu();
		menu.regen();
		menu.open(sender);
		sender.sendMessage("the");
	}

}
