package commons.conf.wrapper;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 * @author vadim
 */
public class EffectParticle {

	public static final EffectParticle EMPTY = new EffectParticle(null, 0);

	public final Particle particle;
	public final int count;

	public EffectParticle(Particle particle, int count) {
		this.particle = particle;
		this.count    = count;
	}

	public void spawnTo(Player player, Location location) {
		if (particle != null)
			player.spawnParticle(particle, location, count);
	}

	public void spawnAt(World world, Location location) {
		if (particle != null)
			world.spawnParticle(particle, location, count);
	}

}
