package generators.impl.wrapper;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import commons.impl.data.PlayerOwned;
import generators.GeneratorRegistry;
import generators.impl.GensPlugin;
import generators.impl.conf.Messages;
import generators.wrapper.Generator;
import generators.wrapper.Tier;
import generators.wrapper.Upgrade;
import generators.wrapper.result.DestroyResult;
import generators.wrapper.result.UpgradeResult;
import me.vadim.util.conf.ConfigurationProvider;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * @author vadim
 */
@DatabaseTable
public class Gen extends PlayerOwned implements Generator {

	private transient ConfigurationProvider conf;

	public void injectConf(ConfigurationProvider conf) {
		this.conf = conf;
	}

	private Messages msg() {
		return conf.open(Messages.class);
	}

	private final @DatabaseField Tier tier;
	private final @DatabaseField Location location;
	private final @DatabaseField UUID world; // ORMLite workaround

	private Gen() { // ORMLite
		super(null);
		this.tier = null;
		this.location = null;
		this.world = null;
	}

	public Gen(OfflinePlayer owner, Tier tier, Location location) {
		super(owner.getUniqueId());
		this.tier = tier;
		this.location = location.getBlock().getLocation().clone();
		this.world = location.getWorld().getUID();
	}

	@Override
	public Tier getCurrentTier() {
		return tier;
	}

	@Override
	public Location getBlockLocation() {
		if (location.getWorld() == null)
			location.setWorld(Bukkit.getWorld(world));
		return location.clone();
	}

	@Override
	public ItemStack asItem() {
		return tier.getGeneratorItem(getOfflineOwner());
	}

	@Override
	public UpgradeResult upgrade(GeneratorRegistry registry) {
		Player player = getOfflineOwner().getPlayer();

		if (player == null) return UpgradeResult.OWNER_OFFLINE;

		Block block = location.getBlock();
		Upgrade upgrade = getCurrentTier().getNextUpgrade();
		if (upgrade == null) return UpgradeResult.MAX_LEVEL; // max lvl

		Tier tier = upgrade.getNextTier();
		if (tier == null) return UpgradeResult.MAX_LEVEL; // max lvl

		//todo: econ
		//todo: sound
		Generator upgraded = tier.toGenerator(player, location);
		registry.createGen(upgraded);
		block.setType(tier.getBlock());
		player.sendMessage("upgraded <3");
		return UpgradeResult.SUCCESS;
	}

	@Override
	public DestroyResult destroy(GeneratorRegistry registry, Player by) {
		if (!isOwnedBy(by) && !by.isOp()) {
			by.sendMessage("dont break other ppl's gens >:[");
			return DestroyResult.NO_PERMISSION;
		}

		registry.deleteGen(this);
		location.getBlock().setType(Material.AIR);

		if(by.getGameMode() != GameMode.CREATIVE)
			by.getInventory().addItem(asItem());
		by.sendMessage("broke " + (isOwnedBy(by) ? "ur" : getOfflineOwner().getName() + "'s") + " gen :o");
		return DestroyResult.SUCCESS;
	}

}
