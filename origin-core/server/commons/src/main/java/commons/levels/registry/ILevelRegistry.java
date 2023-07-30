package commons.levels.registry;

import commons.levels.Level;
import commons.levels.conf.LevelsYml;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ILevelRegistry {

	void create(Level level);

	void createAll(LevelsYml configuration);

	void remove(Level level);

	@NotNull List<Level> getLevels();
}
