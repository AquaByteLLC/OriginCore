package commons.interpolation.impl;

import commons.interpolation.Interpolation;
import commons.interpolation.impl.bounce.Bounce;
import commons.interpolation.impl.bounce.BounceIn;
import commons.interpolation.impl.bounce.BounceOut;
import commons.interpolation.impl.elastic.Elastic;
import commons.interpolation.impl.elastic.ElasticIn;
import commons.interpolation.impl.elastic.ElasticOut;
import commons.interpolation.impl.exponential.Exponential;
import commons.interpolation.impl.exponential.ExponentialIn;
import commons.interpolation.impl.exponential.ExponentialOut;
import commons.interpolation.impl.pow.Pow;
import commons.interpolation.impl.pow.PowIn;
import commons.interpolation.impl.pow.PowOut;
import commons.interpolation.impl.swing.Swing;
import commons.interpolation.impl.swing.SwingIn;
import commons.interpolation.impl.swing.SwingOut;
import commons.math.MathUtils;
import lombok.Getter;

import javax.annotation.Nullable;

public enum InterpolationType {

	linear(a -> a),
	smoothStep(a -> a * a * (3 - 2 * a)),
	smoothStep2(a -> {
		a = a * a * (3 - 2 * a);
		return a * a * (3 - 2 * a);
	}),
	smoother(a -> a * a * a * (a * (a * 6 - 15) + 10)),
	pow2(new Pow(2)),
	pow2In(new PowIn(2)),
	slowFast(new PowIn(2)),
	pow2Out(new PowOut(2)),
	pow2InInverse(a -> {
		if (a < MathUtils.FLOAT_ROUNDING_ERROR) return 0;
		return (float) Math.sqrt(a);
	}),
	pow2OutInverse(a -> {
		if (a < MathUtils.FLOAT_ROUNDING_ERROR) return 0;
		if (a > 1) return 1;
		return 1 - (float) Math.sqrt(-(a - 1));
	}),
	pow3(new Pow(3)),
	pow3In(new PowIn(3)),
	pow3Out(new PowOut(3)),
	pow3InInverse(a -> (float) Math.cbrt(a)),
	pow3OutInverse(a -> 1 - (float) Math.cbrt(-(a - 1))),
	pow4(new Pow(4)),
	pow4In(new PowIn(4)),
	pow4Out(new PowOut(4)),
	pow5(new Pow(5)),
	pow5In(new PowIn(5)),
	pow5Out(new PowOut(5)),
	sine(a -> (1 - MathUtils.cos(a * MathUtils.PI)) / 2),
	sineIn(a -> 1 - MathUtils.cos(a * MathUtils.HALF_PI)),
	sineOut(a -> MathUtils.sin(a * MathUtils.HALF_PI)),
	cosine(a -> (1 - MathUtils.sin(a * MathUtils.PI)) / 2),
	cosineIn(a -> 1 - MathUtils.sin(a * MathUtils.HALF_PI)),
	cosineOut(a -> MathUtils.cos(a * MathUtils.HALF_PI)),
	exponential10(new Exponential(2, 10)),
	exponential10In(new ExponentialIn(2, 10)),
	exponential10Out(new ExponentialOut(2, 10)),

	exponential5(new Exponential(2, 5)),
	exponential5In(new ExponentialIn(2, 5)),
	exponential5Out(new ExponentialOut(2, 5)),
	circle(a -> {
		if (a <= 0.5f) {
			a *= 2;
			return (1 - (float) Math.sqrt(1 - a * a)) / 2;
		}
		a--;
		a *= 2;
		return ((float) Math.sqrt(1 - a * a) + 1) / 2;
	}),

	circleIn(a -> 1 - (float) Math.sqrt(1 - a * a)),

	circleOut(a -> {
		a--;
		return (float) Math.sqrt(1 - a * a);
	}),

	elastic(new Elastic(2, 10, 7, 1)),
	elasticIn(new ElasticIn(2, 10, 6, 1)),
	elasticOut(new ElasticOut(2, 10, 7, 1)),
	swing(new Swing(1.5f)),
	swingIn(new SwingIn(2f)),
	swingOut(new SwingOut(2f)),
	bounce(new Bounce(4)),
	bounceIn(new BounceIn(4)),
	bounceOut(new BounceOut(4)),
	;

	@Getter
	private final Interpolation interpolation;

	InterpolationType(Interpolation interpolation) {
		this.interpolation = interpolation;
	}

	public static @Nullable InterpolationType fromString(String name) {
		for (InterpolationType value : values())
			if (value.name().equalsIgnoreCase(name))
				return value;
		return null;
	}
}
