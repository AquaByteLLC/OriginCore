package blocks.block.protect.strategy;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * @author vadim
 */
class PlayersProtectionStrategy implements ProtectionStrategy {

	@Override
	public boolean permits(@NotNull ProtectedAction action) {
		return action.getEntity() instanceof Player;
	}

	@Override
	public String toString() {
		return "ProtectionStrategy{ mode = PERMIT_PLAYERS }";
	}

}
