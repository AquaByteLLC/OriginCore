package blocks.block.protect.strategy;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Default implementations for {@link ProtectionStrategy}.
 * @author vadim
 */
public enum ProtectionStrategies implements ProtectionStrategy {

	/**
	 * Override strategy that blocks all events regardless of what triggered them.
	 */
	OVERRIDE_ON(){
		@Override
		public boolean permits(@NotNull ProtectedAction action) {
			return false;
		}
	},

	/**
	 * Default strategy that protects from events that were not triggered by an {@linkplain Player#isOp() operator}.
	 */
	DEFAULT(){
		@Override
		public boolean permits(@NotNull ProtectedAction action) {
			Entity entity = action.getEntity();
			if(entity == null)
				return false;
			return entity.isOp();
		}
	},

	/**
	 * Override strategy that allows all events regardless of what triggered them.
	 */
	OVERRIDE_OFF(){
		@Override
		public boolean permits(@NotNull ProtectedAction action) {
			return true;
		}
	};

	/**
	 * A {@link ProtectionStrategy} that behaves like {@link #DEFAULT}, but always permits {@linkplain Player players}.
	 */
	public static final ProtectionStrategy PERMIT_PLAYERS = new PlayersProtectionStrategy();

	/**
	 * Create a new {@link ProtectionStrategy} that behaves like {@link #DEFAULT}, but also permits {@code owner}.
	 * @param owner the {@linkplain OfflinePlayer player} to allow
	 * @return a new {@link ProtectionStrategy}
	 */
	public static ProtectionStrategy permitOwner(OfflinePlayer owner) {
		if(owner == null)
			throw new NullPointerException("illegal owner argument");
		return new OwnedProtectionStrategy(owner.getUniqueId());
	}

	@Override
	public String toString() {
		return "ProtectionStrategy{ mode = " + name() + " }";
	}

}