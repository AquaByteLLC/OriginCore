package commons.data;

import java.util.UUID;

/**
 * typealias for {@code Supplier<Account>} or {@code Supplier<T>} in {@link  AccountStorage} implementations
 * @author vadim
 */
@FunctionalInterface
public interface AccountFactory<T extends Account> {

	T create(UUID uuid);

}
