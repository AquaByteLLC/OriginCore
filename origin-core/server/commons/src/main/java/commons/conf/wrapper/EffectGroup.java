package commons.conf.wrapper;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.function.Predicate;

/**
 * @author vadim
 */
public class EffectGroup {

	public static final EffectGroup EMPTY = new EffectGroup(null, null);

	public final EffectSound sound;
	public final EffectParticle particle;

	public EffectGroup(EffectSound sound, EffectParticle particle) {
		this.sound    = sound;
		this.particle = particle;
	}

	public void sendToIf(Player player, Location s, Predicate<Player> sound, Location p, Predicate<Player> particle) {
		if (sound.test(player))
			if (this.sound != null)
				this.sound.playTo(player, s);
		if (particle.test(player))
			if (this.particle != null)
				this.particle.spawnTo(player, p);
	}

	public void sendAtIf(Location s, Predicate<Player> sound, Location p, Predicate<Player> particle) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (sound.test(player))
				if (this.sound != null)
					this.sound.playTo(player, s);
			if (particle.test(player))
				if (this.particle != null)
					this.particle.spawnTo(player, p);
		}
	}

}
