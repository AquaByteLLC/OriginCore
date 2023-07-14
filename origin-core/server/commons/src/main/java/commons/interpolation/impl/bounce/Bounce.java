package commons.interpolation.impl.bounce;

public class Bounce extends BounceOut {
	public Bounce(float[] widths, float[] heights) {
		super(widths, heights);
	}

	public Bounce(int bounces) {
		super(bounces);
	}

	private float out(float a) {
		float test = a + widths[0] / 2;
		if (test < widths[0]) return test / (widths[0] / 2) - 1;
		return super.apply(a);
	}

	@Override
	public float apply(float a) {
		if (a <= 0.5f) return (1 - out(1 - a * 2)) / 2;
		return out(a * 2 - 1) / 2 + 0.5f;
	}
}
