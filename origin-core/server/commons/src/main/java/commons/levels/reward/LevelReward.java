package commons.levels.reward;

import me.vadim.util.conf.wrapper.Placeholder;
import me.vadim.util.conf.wrapper.impl.StringPlaceholder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public interface LevelReward {

	String getCommand();
	String getName();

	default void execute(Player player) {
		final Placeholder placeholder = StringPlaceholder.builder().set("player", player.getName()).build();
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), placeholder.format(getCommand()));
	}
}
