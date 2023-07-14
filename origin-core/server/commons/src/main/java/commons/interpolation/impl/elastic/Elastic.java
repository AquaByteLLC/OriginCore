package commons.interpolation.impl.elastic;


import commons.interpolation.Interpolation;
import commons.math.MathUtils;

public class Elastic implements Interpolation {
	final float value, power, scale, bounces;

	public Elastic(float value, float power, int bounces, float scale) {
		this.value = value;
		this.power = power;
		this.scale = scale;
		this.bounces = bounces * MathUtils.PI * (bounces % 2 == 0 ? 1 : -1);
	}

	@Override
	public float apply(float a) {
		if (a <= 0.5f) {
			a *= 2;
			return (float) Math.pow(value, power * (a - 1)) * MathUtils.sin(a * bounces) * scale / 2;
		}
		a = 1 - a;
		a *= 2;
		return 1 - (float) Math.pow(value, power * (a - 1)) * MathUtils.sin((a) * bounces) * scale / 2;
	}
}
