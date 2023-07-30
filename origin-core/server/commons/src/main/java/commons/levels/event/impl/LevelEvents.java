package commons.levels.event.impl;

import commons.Commons;
import commons.events.api.EventRegistry;
import commons.events.impl.impl.DetachedSubscriber;
import commons.impl.data.account.PlayerDefaultAccount;
import commons.levels.Level;
import commons.levels.conf.LevelsYml;
import commons.levels.conf.action.LevelMessages;
import commons.levels.event.ExperienceGainEvent;
import commons.levels.event.LevelUpEvent;
import commons.levels.registry.impl.LevelRegistry;
import me.vadim.util.conf.LiteConfig;
import me.vadim.util.conf.wrapper.Placeholder;
import me.vadim.util.conf.wrapper.impl.StringPlaceholder;
import org.bukkit.event.block.BlockBreakEvent;

import java.math.BigDecimal;
import java.math.BigInteger;

public class LevelEvents {
	private static DetachedSubscriber<ExperienceGainEvent> eventDetachedSubscriber;
	private static DetachedSubscriber<LevelUpEvent> levelUpEventDetachedSubscriber;
	private static DetachedSubscriber<BlockBreakEvent> breakEvent;

	private static final EventRegistry registry = Commons.events();
	private static final LevelRegistry levelRegistry = Commons.commons().levelRegistry;
	private final LiteConfig lfc;


	public LevelEvents(LiteConfig lfc) {
		this.lfc = lfc;
	}

	public void init() {
		eventDetachedSubscriber = new DetachedSubscriber<>(ExperienceGainEvent.class, (context, event) -> {
			if (event.getCalling().equals("commons")) {
				final PlayerDefaultAccount account = Commons.commons().getAccounts().getAccount(event.getPlayer());

				final int lvl = account.getLevel().intValueExact();

				if ((lvl) >= levelRegistry.getLevels().size()) {
					return;
				}

				final Level playerLevel = levelRegistry.getLevels().get(lvl);

				while (event.getAmount() >= playerLevel.getRequiredExperience() && !(account.getLevel().intValueExact() >= levelRegistry.getLevels().size())) {
					account.addLevel(BigInteger.ONE);
				}

			}});

		levelUpEventDetachedSubscriber = new DetachedSubscriber<>(LevelUpEvent.class, ((context, event) -> {
			if (event.getCalling().equals("commons")) {
				final StringBuilder rewardsAsList = new StringBuilder();

				for (String s : lfc.open(LevelsYml.class).getRewardNames(event.getLevel()))
					rewardsAsList.append("""
     
						%s""".formatted(s));

				System.out.println("Appending");

				final Placeholder pl = StringPlaceholder.builder()
						.set("level", String.valueOf(event.getLevel()))
						.set("prefix", "&e[&f" + event.getLevel() + "&e]")
						.set("rewards", rewardsAsList.toString())
					.build();

				System.out.println("Sending");

				LevelMessages.versionSender.sendMessage(event.getPlayer(), LevelMessages.LEVEL_UP_MESSAGE, pl);
			}}));

		breakEvent = new DetachedSubscriber<>(BlockBreakEvent.class, ((context, event) -> {
			event.getPlayer().sendMessage("Hey, giving XP!");
			final PlayerDefaultAccount account = Commons.commons().getAccounts().getAccount(event.getPlayer());
			account.addExperience(BigDecimal.valueOf(20));
		}));

		breakEvent.bind(registry);
		levelUpEventDetachedSubscriber.bind(registry);
		eventDetachedSubscriber.bind(registry);
	}
}
