package commons.hologram;

import animation.math.RangeUntilKt;
import animation.math.RangesKt;
import commons.CommonsPlugin;
import commons.interpolation.impl.InterpolationType;
import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import me.lucko.helper.Schedulers;
import me.lucko.helper.serialize.Position;
import me.vadim.util.conf.wrapper.Placeholder;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class InterpolatedHologram {

	private final Position startPosition;
	private Hologram hologram;
	private final String text;
	private final Block block;
	private final HashMap<Position, String> positionMap;

	public InterpolatedHologram(Block block, Position startPosition, Placeholder placeholder, String... text) {
		this.startPosition = startPosition;
		this.block = block;
		this.positionMap = new HashMap<>();

		this.text = placeholder.format(Arrays.toString(text));

		positionMap.put(startPosition, String.valueOf(UUID.randomUUID()));
	}

	public void create(Player player, float lifeSpan, float moveTicks, float xIncr, float yIncr, float zIncr, boolean interpolateX, boolean interpolateY, boolean interpolateZ, InterpolationType interpolation) {
		this.hologram = DHAPI.createHologram(positionMap.get(startPosition),
				startPosition.toLocation(),
				Collections.singletonList(text));

		final AtomicInteger timeLived = new AtomicInteger();

		hologram.setDefaultVisibleState(false);
		hologram.setShowPlayer(player);

		Schedulers.bukkit().runTaskTimerAsynchronously(CommonsPlugin.commons(), ($) -> {

			if (timeLived.get() >= lifeSpan) {
				hologram.delete();
				positionMap.remove(startPosition);
				$.cancel();
			}

			if (moveTicks >= timeLived.get()) {
				final float[] vals = interpolatedValues(player, timeLived.get(), lifeSpan, yIncr, xIncr, zIncr, interpolateX, interpolateY, interpolateZ, interpolation);
				final Position position = Position.of(vals[0], vals[1], vals[2], player.getWorld());
				DHAPI.moveHologram(positionMap.get(startPosition), position.toLocation());
			}

			timeLived.getAndIncrement();
		}, 1, 1);
	}

	private float[] interpolatedValues(Player player, int ticksRan, float lifeSpan, float yIncr, float xIncr, float zIncr, boolean interpolateX, boolean interpolateY, boolean interpolateZ, InterpolationType interpolation) {

		double xValue = interpolateX ? startPosition.getX() + xIncr : startPosition.getX();
		double yValue = interpolateY ? startPosition.getY() + yIncr : startPosition.getY();
		double zValue = interpolateZ ? startPosition.getZ() + zIncr : startPosition.getZ();

		Position textTarget = Position.of(
				xValue, yValue, zValue, player.getWorld());

		float xPos = interpolateX ? RangesKt.interpolate(RangeUntilKt.rangeUntil((float) startPosition.getX(), (float) textTarget.getX()),
				RangesKt.coerceAtMost((float) ticksRan / lifeSpan, 1f),
				interpolation.getInterpolation()) : (float) xValue;

		float yPos = interpolateY ? RangesKt.interpolate(RangeUntilKt.rangeUntil((float) startPosition.getY(), (float) textTarget.getY()),
				RangesKt.coerceAtMost((float) ticksRan / lifeSpan, 1f),
				interpolation.getInterpolation()) : (float) yValue;

		float zPos = interpolateZ ? RangesKt.interpolate(
				RangeUntilKt.rangeUntil((float) startPosition.getZ(), (float) textTarget.getZ()),
				RangesKt.coerceAtMost((float) ticksRan / lifeSpan, 1f),
				interpolation.getInterpolation()) : (float) zValue;

		return new float[]{xPos, yPos, zPos};
	}

	public void addHologramLine(String text) {
		DHAPI.addHologramLine(hologram, text);
	}

	public void addItemLine(ItemStack stack) {
		DHAPI.addHologramLine(hologram, stack);
	}


}
