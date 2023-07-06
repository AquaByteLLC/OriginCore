package commons.impl.account;

import commons.data.AccountStorage;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * @author vadim
 */
public class AccountStorageHandler {

	private final Set<AccountStorage<?>> storages = new HashSet<>();

	public void track(AccountStorage<?> storage) {
		storages.add(storage);
	}

	public void untrack(AccountStorage<?> storage) {
		storages.remove(storage);
	}

	public void saveAll() {
		storages.forEach(AccountStorage::flushAndSave);
	}

	public void saveOne(UUID player) {
		storages.forEach(s -> s.savePlayer(player));
	}

	public void loadOne(UUID player) {
		storages.forEach(s -> s.loadPlayer(player));
	}

}
