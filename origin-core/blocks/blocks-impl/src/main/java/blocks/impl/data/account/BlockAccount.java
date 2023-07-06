package blocks.impl.data.account;


import blocks.impl.registry.ProgressRegistryImpl;
import blocks.impl.registry.RegenerationRegistryImpl;
import com.j256.ormlite.table.DatabaseTable;
import commons.data.impl.AbstractAccount;
import me.lucko.helper.bossbar.BossBar;
import me.lucko.helper.bossbar.BossBarColor;
import me.lucko.helper.bossbar.BossBarStyle;
import me.lucko.helper.bossbar.BukkitBossBarFactory;
import org.bukkit.Bukkit;

import java.util.UUID;

@DatabaseTable(tableName = "blockAccount")
public class BlockAccount extends AbstractAccount {

	private final RegenerationRegistryImpl regenerationRegistry;
	private final ProgressRegistryImpl progressRegistry;
	private final BossBar playerBar;

	private BlockAccount() { // ORMLite
		super(null);
		this.regenerationRegistry = new RegenerationRegistryImpl();
		this.progressRegistry = new ProgressRegistryImpl();
		this.playerBar = new BukkitBossBarFactory(Bukkit.getServer()).newBossBar()
				.color(BossBarColor.GREEN)
				.style(BossBarStyle.SOLID);
	}

	BlockAccount(UUID uuid) {
		super(uuid);
		this.regenerationRegistry = new RegenerationRegistryImpl();
		this.progressRegistry = new ProgressRegistryImpl();
		this.playerBar = new BukkitBossBarFactory(Bukkit.getServer()).newBossBar().color(BossBarColor.GREEN).style(BossBarStyle.SOLID);
	}

	public RegenerationRegistryImpl getRegenerationRegistry() {
		return this.regenerationRegistry;
	}

	public BossBar getPlayerBar() {
		return playerBar;
	}

	public ProgressRegistryImpl getProgressRegistry() {
		return progressRegistry;
	}
}
