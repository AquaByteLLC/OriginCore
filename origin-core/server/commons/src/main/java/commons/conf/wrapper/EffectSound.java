package commons.conf.wrapper;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 * @author vadim
 */
public class EffectSound {

	public static final EffectSound EMPTY = new EffectSound(null, 0f, 0f);

	public final Sound sound;
	public final float volume;
	public final float pitch;

	public EffectSound(Sound sound, float volume, float pitch) {
		this.sound  = sound;
		this.volume = volume;
		this.pitch  = pitch;
	}

	public void playTo(Player player, Location location) {
		if (sound != null)
			player.playSound(location, sound, volume, pitch);
	}

	public void playAt(World world, Location location) {
		if (sound != null)
			world.playSound(location, sound, volume, pitch);
	}

}
