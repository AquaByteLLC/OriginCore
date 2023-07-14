package commons.interpolation.impl.elastic;


import commons.math.MathUtils;

public class ElasticOut extends Elastic {
	public ElasticOut(float value, float power, int bounces, float scale) {
		super(value, power, bounces, scale);
	}

	public float apply(float a) {
		if (a == 0) return 0;
		a = 1 - a;
		return (1 - (float) Math.pow(value, power * (a - 1)) * MathUtils.sin(a * bounces) * scale);
	}
}
