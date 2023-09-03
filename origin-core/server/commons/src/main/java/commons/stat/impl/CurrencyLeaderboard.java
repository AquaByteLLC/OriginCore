package commons.stat.impl;

import commons.Commons;
import commons.data.account.Account;
import commons.impl.data.account.PlayerDefaultAccount;
import commons.impl.econ.OriginCurrency;

import java.util.Comparator;

/**
 * @author vadim
 */
public class CurrencyLeaderboard extends LeaderboardAdapter<PlayerDefaultAccount> {

	public CurrencyLeaderboard() {
		super(Comparator.comparing(it -> it.getBalance(OriginCurrency.CURRENCY)), Commons.commons().getAccounts()::getAccount);
	}

	@Override
	public Account[] getTop10() {
		return new Account[0];
	}

	@Override
	public Account[] getTopN(int n) {
		return new Account[0];
	}

	@Override
	public long getRank(Account account) {
		return 0;
	}

	@Override
	public void update(Account account) {

	}

}
