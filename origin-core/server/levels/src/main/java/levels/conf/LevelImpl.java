package levels.conf;

import levels.Level;
import levels.reward.LevelReward;

import java.util.Collections;
import java.util.List;

/**
 * @author vadim
 */
class LevelImpl implements Level {

	private final double xp;
	private final List<LevelReward> rewards;
	private final int rewardCt;
	private final int level;

	LevelImpl(double xp, List<LevelReward> rewards, int rewardCt, int level) {
		this.xp       = xp;
		this.rewards  = Collections.unmodifiableList(rewards);
		this.rewardCt = rewardCt;
		this.level    = level;
	}

	@Override
	public double getRequiredExperience() {
		return xp;
	}

	@Override
	public List<LevelReward> getRewards() {
		return rewards;
	}

	@Override
	public int getRewardCount() {
		return rewardCt;
	}

	@Override
	public int levelNumber() {
		return level;
	}

}
