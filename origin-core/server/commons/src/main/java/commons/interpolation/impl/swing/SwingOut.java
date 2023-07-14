package commons.interpolation.impl.swing;


import commons.interpolation.Interpolation;

public class SwingOut implements Interpolation {
	private final float scale;

	public SwingOut(float scale) {
		this.scale = scale;
	}

	public float apply(float a) {
		a--;
		return a * a * ((scale + 1) * a + scale) + 1;
	}
}
