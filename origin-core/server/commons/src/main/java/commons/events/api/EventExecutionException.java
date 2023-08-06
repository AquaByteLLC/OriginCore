package commons.events.api;

/**
 * Wraps an exception that happened {@linkplain EventRegistry#publish(org.bukkit.entity.Player, Object) during} {@linkplain EventRegistry#publish(Object) execution} of an event.
 */
public class EventExecutionException extends RuntimeException {

	public EventExecutionException(String message) {
		super(message);
	}

	public EventExecutionException(String message, Throwable cause) {
		super(message, cause);
	}

	public EventExecutionException(Throwable cause) {
		super(cause);
	}

}
