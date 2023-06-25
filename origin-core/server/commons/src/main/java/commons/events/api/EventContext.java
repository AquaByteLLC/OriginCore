package commons.events.api;

/**
 * @author vadim
 */
public interface EventContext {

	Object getEvent();

	/**
	 * Mutate the event object that is to be passed on.
	 * <p>
	 * {@link org.bukkit.event.Event Bukkit events} only allow for {@link #setCancelled(boolean) cancelling}, so this method is <b>not always guaranteed</b> to work. <p>
	 * However, calling {@code context.mutate(null)} is identical to calling {@code context.setCancelled(true)}, regardless of the implementation.
	 * @param event the new event
	 */
	void mutate(Object event);

	boolean isCancelled();

	void setCancelled(boolean toCancel);

}
