package blocks.block.protect.strategy;

import org.jetbrains.annotations.NotNull;

public interface ProtectionStrategy {

	/**
	 * @param action the {@link ProtectedAction} happening
	 * @return whether or not this {@link ProtectionStrategy} permits {@code action} to happen
	 */
	boolean permits(@NotNull ProtectedAction action);

}