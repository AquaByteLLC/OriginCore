package levels.reward;

import org.bukkit.entity.Player;

public interface LevelReward {

	String getCommand();
	String getName();

	void execute(Player player);
}
