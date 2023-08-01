package levels;


import levels.reward.LevelReward;

import java.util.List;

public interface Level {

	/**
	 *
	 * @return returns the required experience for the player to level up.
	 */
	double getRequiredExperience();

	/**
	 *
	 * @return returns a list of possible rewards.
	 */
	List<LevelReward> getRewards();

	/**
	 *
	 * @return returns an int of the amount of rewards picked from the list.
	 */
	int getRewardCount();

	/**
	 *
	 * @return returns an int which will be used to identify the level number.
	 */
	int levelNumber();

}
