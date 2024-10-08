package generators.impl.data;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import commons.data.account.impl.AbstractAccount;
import generators.GeneratorRegistry;
import generators.impl.conf.Config;
import me.vadim.util.conf.ConfigurationProvider;

import java.util.UUID;

/**
 * @author vadim
 */
@DatabaseTable
public class GenAccount extends AbstractAccount {

	GeneratorRegistry registry;

	private GenAccount() { // ORMLite
		super(null);
	}

	GenAccount(UUID uuid, GeneratorRegistry registry, ConfigurationProvider conf) {
		super(uuid);
		this.registry = registry;
		slotLimit = conf.open(Config.class).getDefaultMaxSlots();
	}

	@DatabaseField
	public int slotLimit;

	public int getSlotsUsed() {
		return registry.countGenerators(getOwnerUUID());
	}

	public boolean isAtSlotLimit() {
		return getSlotsUsed() >= slotLimit;
	}

	public boolean canBulkUpgrade() {
		return getOfflineOwner().getPlayer().hasPermission("gens.bulk");
	}

}
