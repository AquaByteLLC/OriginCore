package levels.data;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import commons.data.account.impl.AbstractAccount;
import levels.Level;
import levels.event.ExperienceGainEvent;
import levels.event.LevelUpEvent;
import levels.registry.LevelRegistry;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.UUID;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


@DatabaseTable
public class LevelsAccount extends AbstractAccount {

	LevelRegistry registry;

	private LevelsAccount() { // ORMLite
		super(null);
	}

	LevelsAccount(UUID uuid) {
		super(uuid);
	}

	@DatabaseField
	int level;
	@DatabaseField
	float experience;

	public void addExperience(float value) {
		if (level > registry.getLevels().size()) return;

		experience += value;

		new ExperienceGainEvent("commons", getOfflineOwner().getPlayer(), value, false).callEvent();
	}

	public void addLevel(int value) {
		if ((level + value) > registry.getLevels().size()) return;

		level += value;

		final Level lev = registry.getLevels().get(level - 1);

		experience -= lev.getRequiredExperience();
		if (experience < 0) experience = 0;

		new LevelUpEvent("commons", getOfflineOwner().getPlayer(), level, false).callEvent();
	}

	public int getLevel() {
		return level;
	}

	public float getExperience() {
		return experience;
	}

}
