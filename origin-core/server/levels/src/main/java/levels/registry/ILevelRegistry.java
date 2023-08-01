package levels.registry;

import levels.Level;
import levels.conf.LevelsYml;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ILevelRegistry {

	void create(Level level);

	void createAll(LevelsYml configuration);

	void remove(Level level);

	@NotNull List<Level> getLevels();
}
