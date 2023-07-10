package blocks.block.protect.strategy;

import commons.impl.data.PlayerOwned;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * @author vadim
 */
class OwnedProtectionStrategy extends PlayerOwned implements ProtectionStrategy {

	OwnedProtectionStrategy(UUID uuid) {
		super(uuid);
	}

	@Override
	public boolean permits(@NotNull ProtectedAction action) {
		Entity entity = action.getEntity();
		if(entity == null)
			return false;
		return entity.isOp() || isOwnedBy(entity.getUniqueId());
	}

	@Override
	public String toString() {
		return "ProtectionStrategy{ permits = "+getOwnerUUID()+" }";
	}

}
