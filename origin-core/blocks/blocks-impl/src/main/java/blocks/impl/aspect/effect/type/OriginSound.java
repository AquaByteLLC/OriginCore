package blocks.impl.aspect.effect.type;

import blocks.block.effects.EffectType;
import me.lucko.helper.utils.Players;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class OriginSound implements EffectType<org.bukkit.Sound> {

	private Sound sound;

	public OriginSound(String sound) {
		this.sound = Sound.valueOf(sound);
	}

	@Override
	public Sound getType() {
		return sound;
	}

	@Override
	public EffectType<Sound> setType(Sound type) {
		this.sound = type;
		return this;
	}

	@Override
	public void handleEffect(Player player, Location location) {
		Players.playSound(player, location, sound);
	}
}
