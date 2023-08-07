package levels.registry;

import levels.Level;
import levels.conf.LevelsConfig;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface LevelRegistry {

	void create(Level level);

	void createAll(LevelsConfig configuration);

	void remove(Level level);

	@NotNull List<Level> getLevels();
}
