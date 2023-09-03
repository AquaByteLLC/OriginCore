package commons.stat.impl;

import commons.data.account.Account;
import commons.stat.Leaderboard;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

/**
 * @author vadim
 */
public abstract class LeaderboardAdapter<A> implements Leaderboard {

	protected final Comparator<A> comp;
	private final Function<UUID, A> adapter;
	private final List<UUID> unranked = new ArrayList<>();

	protected LeaderboardAdapter(Comparator<A> comparator, Function<UUID, A> adapter) {
		this.comp = comparator;
		this.adapter = adapter;
	}

	protected final A adapt(@NotNull Account account) {
		return adapter.apply(account.getOwnerUUID());
	}

	@Override
	public Account[] getTop10() {
		return getTopN(10);
	}

	@Override
	public Account[] getTopN(int n) {
//		return sorted.stream().limit(n).map(Account.class::cast).toArray(Account[]::new);
		return(null);
	}

	@Override
	public long getRank(Account account) {
//		return sorted.;
		return(0);
	}

}
