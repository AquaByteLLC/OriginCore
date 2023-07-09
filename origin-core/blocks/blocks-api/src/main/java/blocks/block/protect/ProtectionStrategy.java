package blocks.block.protect;

import org.bukkit.entity.Player;

/**
 * @author vadim
 */
public enum ProtectionStrategy {

	/**
	 * Override strategy that blocks all events regardless of what triggered them.
	 */
	OVERRIDE_ON,

	/**
	 * Default strategy that protects from events that were not triggered by an {@linkplain Player#isOp() operator}.
	 */
	DEFAULT,

	/**
	 * Override strategy that allows all events regardless of what triggered them.
	 */
	OVERRIDE_OFF;

}