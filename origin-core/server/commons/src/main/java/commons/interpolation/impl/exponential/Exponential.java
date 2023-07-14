package commons.interpolation.impl.exponential;


import commons.interpolation.Interpolation;

public class Exponential implements Interpolation {

	final float value, power, min, scale;

	public Exponential(float value, float power) {
		this.value = value;
		this.power = power;
		min = (float) Math.pow(value, -power);
		scale = 1 / (1 - min);
	}

	@Override
	public float apply(float a) {
		if (a <= 0.5f) return ((float) Math.pow(value, power * (a * 2 - 1)) - min) * scale / 2;
		return (2 - ((float) Math.pow(value, -power * (a * 2 - 1)) - min) * scale) / 2;
	}
}
