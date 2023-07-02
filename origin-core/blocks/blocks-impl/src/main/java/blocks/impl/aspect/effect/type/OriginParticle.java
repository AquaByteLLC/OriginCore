package blocks.impl.aspect.effect.type;

import blocks.block.effects.EffectType;
import me.lucko.helper.utils.Players;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public class OriginParticle implements EffectType<Particle> {

	private final int val0;
	private Particle type;

	public OriginParticle(int val0) {
		this.val0 = val0;
	}

	@Override
	public Particle getType() {
		return type;
	}

	@Override
	public EffectType<Particle> setType(Particle type) {
		this.type = type;
		return this;
	}

	@Override
	public void handleEffect(Player player, Location location) {
		if (type == null) return;
		Location newLoc = new Location(player.getWorld(),
				location.getX() + 0.5D, location.getY() + 0.5D, location.getZ() + 0.5D);
		Players.spawnParticle(player, newLoc, this.type, this.val0);

	}
}