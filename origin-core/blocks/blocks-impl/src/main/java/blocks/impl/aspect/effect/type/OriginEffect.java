package blocks.impl.aspect.effect.type;

import blocks.block.effects.EffectType;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class OriginEffect implements EffectType<Effect> {

	private final int val0;
	private Effect type;

	public OriginEffect(int val0) {
		this.val0 = val0;
	}

	@Override
	public Effect getType() {
		return type;
	}

	@Override
	public EffectType<Effect> setType(Effect type) {
		this.type = type;
		return this;
	}

	@Override
	public void handleEffect(Player player, Location location) {
		if (type == null) return;
		Location newLoc = new Location(player.getWorld(),
				location.getX() + 0.5D, location.getY() + 0.5D, location.getZ() + 0.5D);
		// Players.spawnEffect(player, newLoc, this.type, this.val0);
		System.out.println("DOING EFFECT");
	}
}