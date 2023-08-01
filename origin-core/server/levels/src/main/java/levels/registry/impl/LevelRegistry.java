package levels.registry.impl;

import levels.Level;
import levels.conf.LevelsYml;
import levels.registry.ILevelRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class LevelRegistry implements ILevelRegistry {

	private final List<Level> levels;

	public LevelRegistry() {
		this.levels = new ArrayList<>();
	}

	public LevelRegistry(LevelsYml yml) {
		this.levels = new ArrayList<>();
		createAll(yml);
	}

	@Override
	public void create(Level level) {
		this.levels.add(level);
	}

	@Override
	public void createAll(LevelsYml configuration) {
		configuration.getLevels().forEach(this::create);
	}

	@Override
	public void remove(Level level) {
		this.levels.remove(level);
	}

	@Override
	public @NotNull List<Level> getLevels() {
		return this.levels;
	}
}
