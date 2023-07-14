package commons.interpolation.impl.exponential;

public class ExponentialOut extends Exponential {
	public ExponentialOut(float value, float power) {
		super(value, power);
	}

	public float apply(float a) {
		return 1 - ((float) Math.pow(value, -power * a) - min) * scale;
	}
}
