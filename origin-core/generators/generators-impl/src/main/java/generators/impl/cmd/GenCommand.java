package generators.impl.cmd;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.google.common.collect.Multimap;
import commons.Commons;
import commons.util.StringUtil;
import commons.data.account.AccountProvider;
import commons.data.account.AccountStorage;
import generators.GeneratorRegistry;
import generators.impl.GensPlugin;
import generators.impl.conf.Tiers;
import generators.impl.data.GenAccount;
import generators.impl.data.GenStorage;
import generators.impl.menu.ManageMenu;
import generators.impl.menu.BuyMenu;
import generators.wrapper.Tier;
import me.vadim.util.conf.ConfigurationProvider;
import me.vadim.util.menu.Menu;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;

/**
 * @author vadim
 */
@CommandAlias("gen")
public class GenCommand extends BaseCommand {

	private final GensPlugin plugin;
	private final GenStorage genStorage;

	private final GeneratorRegistry reg;
	private final ConfigurationProvider conf;
	private final AccountProvider<GenAccount> accounts;

	public GenCommand(GensPlugin plugin, GenStorage genStorage) {
		this.plugin = plugin;
		this.genStorage = genStorage;
		this.reg = plugin.getRegistry();
		this.conf = plugin.getConfiguration();
		this.accounts = plugin.getAccounts();
	}

	@Subcommand("listsubcommands")
	@SuppressWarnings("ALL")
	public void reflect(Player player) throws Throwable {
		Field f = BaseCommand.class.getDeclaredField("subCommands");
		f.setAccessible(true);
		Multimap<String, ?> map = (Multimap) f.get(this);
		for (String s : map.keySet()) {
			System.out.println("SUBCOMMAND: " + s);
		}
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
		Commons.scheduler().getAsyncExecutor().submit(() -> {
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

		Location L = new Location(sender.getWorld(), 0, 0, 0);
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
					reg.createGen(tier.toGenerator(sender, L));
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

	@Subcommand("buy")
	@CommandAlias("gens")
	public void buyMenu(Player sender) {
		Menu menu = new BuyMenu(plugin).getMenu();
		menu.regen();
		menu.open(sender);
	}

	@Subcommand("manage")
	@CommandAlias("managegens")
	public void manage(Player sender) {
		Menu menu = new ManageMenu(plugin, sender).getMenu();
		menu.regen();
		menu.open(sender);
	}

	@Subcommand("manage")
	@CommandPermission("*")
	public void manage(Player sender, @Flags("other") Player target) {
		Menu menu = new ManageMenu(plugin, target).getMenu();
		menu.regen();
		menu.open(sender);
	}

}
