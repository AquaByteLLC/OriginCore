package blocks.impl.aspect.effect.type;

import blocks.block.effects.EffectType;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class OriginEffect implements EffectType<Effect> {

	private final int val0;
	private final int val1;
	private Effect type = Effect.ANVIL_BREAK;

	public OriginEffect(int val0, int val1) {
		this.val0 = val0;
		this.val1 = val1;
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
	public void handleEffect(Player player, Location location, boolean global) {
		if (type == null) return;

		if (global) {
			player.getWorld().playEffect(location, type, this.val0, this.val1);
		} else {
			player.getWorld().playEffect(player.getLocation(), type, this.val0, this.val1);
		}
	}
}