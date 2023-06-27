package blocks.impl.aspect.effect.type;

import blocks.block.effects.EffectType;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public class OriginParticle implements EffectType<Particle> {

	private final int val0;
	private Particle type = Particle.ASH;

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
	public void handleEffect(Player player, Location location, boolean global) {
		if (type == null) return;

		if (global) {
			player.getWorld().spawnParticle(type, location, this.val0);
		} else {
			player.getWorld().spawnParticle(type, player.getLocation(), this.val0);
		}
	}
}