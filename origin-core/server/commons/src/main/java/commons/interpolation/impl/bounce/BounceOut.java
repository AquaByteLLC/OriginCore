package commons.interpolation.impl.bounce;


import commons.interpolation.Interpolation;

public class BounceOut implements Interpolation {
	final float[] widths, heights;

	public BounceOut(float[] widths, float[] heights) {
		if (widths.length != heights.length)
			throw new IllegalArgumentException("Must be the same number of widths and heights.");
		this.widths = widths;
		this.heights = heights;
	}

	public BounceOut(int bounces) {
		if (bounces < 2 || bounces > 5) throw new IllegalArgumentException("Bounces cannot be < 2 or > 5: " + bounces);
		widths = new float[bounces];
		heights = new float[bounces];
		heights[0] = 1;
		switch (bounces) {
			case 2 -> {
				widths[0] = 0.6f;
				widths[1] = 0.4f;
				heights[1] = 0.33f;
			}
			case 3 -> {
				widths[0] = 0.4f;
				widths[1] = 0.4f;
				widths[2] = 0.2f;
				heights[1] = 0.33f;
				heights[2] = 0.1f;
			}
			case 4 -> {
				widths[0] = 0.34f;
				widths[1] = 0.34f;
				widths[2] = 0.2f;
				widths[3] = 0.15f;
				heights[1] = 0.26f;
				heights[2] = 0.11f;
				heights[3] = 0.03f;
			}
			case 5 -> {
				widths[0] = 0.3f;
				widths[1] = 0.3f;
				widths[2] = 0.2f;
				widths[3] = 0.1f;
				widths[4] = 0.1f;
				heights[1] = 0.45f;
				heights[2] = 0.3f;
				heights[3] = 0.15f;
				heights[4] = 0.06f;
			}
		}
		widths[0] *= 2;
	}

	@Override
	public float apply(float a) {
		if (a == 1) return 1;
		a += widths[0] / 2;
		float width = 0, height = 0;
		for (int i = 0, n = widths.length; i < n; i++) {
			width = widths[i];
			if (a <= width) {
				height = heights[i];
				break;
			}
			a -= width;
		}
		a /= width;
		float z = 4 / width * height * a;
		return 1 - (z - z * a) * width;
	}
}
