package commons.stat;

import commons.data.account.Account;

/**
 * @author vadim
 */
public interface Leaderboard {

	public static final long UNRANKED = -1;


	/**
	 * @return an array of the the top 10 accounts, the greatest being at index {@code 0} and least at index {@code 9}
	 */
	public Account[] getTop10();

	/**
	 * @return an array of the the top {@code n} accounts, the greatest being at index {@code 0} and least at index {@code n-1}
	 */
	public Account[] getTopN(int n);

	/**
	 * @param account the player to query
	 * @return the player's current rank or {@value #UNRANKED}
	 */
	public long getRank(Account account);

	/**
	 * Forces a refresh of the given account.
	 * This is intended to be called after modification to an account.
	 * @param account the player to update
	 */
	public void update(Account account);

}
