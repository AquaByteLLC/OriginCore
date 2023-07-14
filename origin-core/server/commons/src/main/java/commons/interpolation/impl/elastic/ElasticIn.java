package commons.interpolation.impl.elastic;


import commons.math.MathUtils;

public class ElasticIn extends Elastic {
	public ElasticIn(float value, float power, int bounces, float scale) {
		super(value, power, bounces, scale);
	}

	@Override
	public float apply(float a) {
		if (a >= 0.99) return 1;
		return (float) Math.pow(value, power * (a - 1)) * MathUtils.sin(a * bounces) * scale;
	}
}
