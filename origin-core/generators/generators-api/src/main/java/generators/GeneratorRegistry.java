package generators;

import generators.wrapper.Generator;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 * @author vadim
 */
public interface GeneratorRegistry {

	void createGen(Generator generator);
	void deleteGen(Generator generator);

	Generator getGenAt(Location location);

	List<Generator> getGenerators(OfflinePlayer owner);

	int countGenerators(UUID ownerUUID);

	/**
	 * @return a fast iterator over all loaded {@link Generator}s
	 */
	Iterator<Generator> all();

}
