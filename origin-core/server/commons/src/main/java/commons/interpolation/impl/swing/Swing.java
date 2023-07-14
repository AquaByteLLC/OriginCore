package commons.interpolation.impl.swing;


import commons.interpolation.Interpolation;

public class Swing implements Interpolation {
	private final float scale;

	public Swing(float scale) {
		this.scale = scale * 2;
	}

	public float apply(float a) {
		if (a <= 0.5f) {
			a *= 2;
			return a * a * ((scale + 1) * a - scale) / 2;
		}
		a--;
		a *= 2;
		return a * a * ((scale + 1) * a + scale) / 2 + 1;
	}
}
