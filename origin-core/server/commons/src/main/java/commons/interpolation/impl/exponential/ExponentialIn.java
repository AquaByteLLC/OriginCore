package commons.interpolation.impl.exponential;

public class ExponentialIn extends Exponential {

	public ExponentialIn(float value, float power) {
		super(value, power);
	}

	public float apply(float a) {
		return ((float) Math.pow(value, power * (a - 1)) - min) * scale;
	}
}
