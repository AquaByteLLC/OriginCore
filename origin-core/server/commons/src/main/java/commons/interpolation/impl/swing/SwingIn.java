package commons.interpolation.impl.swing;


import commons.interpolation.Interpolation;

public class SwingIn implements Interpolation {
	private final float scale;

	public SwingIn(float scale) {
		this.scale = scale;
	}

	public float apply(float a) {
		return a * a * ((scale + 1) * a - scale);
	}
}
