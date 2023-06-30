package commons.events.api;

import org.bukkit.entity.Player;

/**
 * Extra information about an event. Accepting this class as a {@linkplain Subscribe paramter} is optional.
 * @author vadim
 */
public interface EventContext {

	/**
	 * Callers are advised to verify {@link #hasPlayer()},
	 * however this is not enforced by a {@linkplain org.jetbrains.annotations.Nullable compile-time warning} because it will almost always be evident based on the actual event type.
	 * @return the player involved with this event, or {@code null} if no player is involved
	 */
	Player getPlayer();

	/**
	 * @return whether or not the current event involves a player
	 */
	boolean hasPlayer();

	/**
	 * @return whether this event has been cancelled or not
	 */
	boolean isCancelled();

	/**
	 * Cancel the event (if applicable). This may be used to uncancel certain events,
	 * however it must be noted that the default state of {@link #isCancelled()} will vary depending on the implementation,
	 * though generally it reflects the state of the underlying event.
	 * @param toCancel the new {@link #isCancelled() cancelled state}
	 */
	void setCancelled(boolean toCancel);

}
