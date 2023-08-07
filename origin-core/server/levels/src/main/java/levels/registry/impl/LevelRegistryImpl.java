package levels.registry.impl;

import levels.Level;
import levels.conf.LevelsConfig;
import levels.registry.LevelRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class LevelRegistryImpl implements LevelRegistry {

	private final List<Level> levels;

	public LevelRegistryImpl() {
		this.levels = new ArrayList<>();
	}

	public static LevelRegistry load(LevelsConfig config) {
		LevelRegistry registry = new LevelRegistryImpl();
		registry.createAll(config);
		return registry;
	}

	@Override
	public void create(Level level) {
		this.levels.add(level);
	}

	@Override
	public void createAll(LevelsConfig configuration) {
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
