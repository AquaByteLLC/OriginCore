package levels.event.impl;

import commons.Commons;
import commons.events.api.Subscribe;
import commons.impl.data.account.PlayerDefaultAccount;
import levels.Level;
import levels.LevelsPlugin;
import levels.conf.LevelsConfig;
import levels.conf.action.LevelMessages;
import levels.event.ExperienceGainEvent;
import levels.event.LevelUpEvent;
import levels.reward.LevelReward;
import me.vadim.util.conf.wrapper.Placeholder;
import me.vadim.util.conf.wrapper.impl.StringPlaceholder;
import org.bukkit.event.block.BlockBreakEvent;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.stream.Collectors;

public class LevelEvents {

	private final LevelsPlugin plugin;

	public LevelEvents(LevelsPlugin plugin) {
		this.plugin = plugin;
	}

	@Subscribe
	public void onXpGain(ExperienceGainEvent event) {
		if (event.getCalling().equals("commons")) {
			final PlayerDefaultAccount account = Commons.commons().getAccounts().getAccount(event.getPlayer());

			final int lvl = account.getLevel().intValueExact();

			if ((lvl) >= plugin.getLevelRegistry().getLevels().size()) {
				return;
			}

			final Level playerLevel = plugin.getLevelRegistry().getLevels().get(lvl);

			while (event.getAmount() >= playerLevel.getRequiredExperience() && !(account.getLevel().intValueExact() >= plugin.getLevelRegistry().getLevels().size())) {
				account.addLevel(BigInteger.ONE);
			}
		}
	}

	@Subscribe
	public void onLevelUp(LevelUpEvent event) {
		if (event.getCalling().equals("commons")) {
			final Placeholder pl = StringPlaceholder.builder()
													.set("level", String.valueOf(event.getNewLevel()))
													.set("prefix", "&e[&f" + event.getNewLevel() + "&e]")
													.set("rewards", plugin.config().getRewards(event.getNewLevel()).stream().map(LevelReward::getName).collect(Collectors.joining("\n")))
													.build();

			LevelMessages.versionSender.sendMessage(event.getPlayer(), LevelMessages.LEVEL_UP_MESSAGE, pl);
		}
	}
	
	@Subscribe
	public void onBreak(BlockBreakEvent event) {
		event.getPlayer().sendMessage("Hey, giving XP!");
		final PlayerDefaultAccount account = Commons.commons().getAccounts().getAccount(event.getPlayer());
		account.addExperience(BigDecimal.valueOf(20));
	}
	
}
