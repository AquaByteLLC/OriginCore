package levels.conf;

import levels.reward.LevelReward;
import me.vadim.util.conf.wrapper.Placeholder;
import me.vadim.util.conf.wrapper.impl.StringPlaceholder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * @author vadim
 */
class RewardImpl implements LevelReward {

	private final String command;
	private final String name;

	RewardImpl(String command, String name) {
		this.command = command;
		this.name    = name;
	}

	@Override
	public String getCommand() {
		return command;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void execute(Player player) {
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), StringPlaceholder.of("player", player.getName()).format(getCommand()));
	}

}
