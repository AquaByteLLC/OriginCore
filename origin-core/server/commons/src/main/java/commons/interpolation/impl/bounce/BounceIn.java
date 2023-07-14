package commons.interpolation.impl.bounce;

public class BounceIn extends BounceOut {
	public BounceIn(float[] widths, float[] heights) {
		super(widths, heights);
	}

	public BounceIn(int bounces) {
		super(bounces);
	}

	public float apply(float a) {
		return 1 - super.apply(1 - a);
	}
}
