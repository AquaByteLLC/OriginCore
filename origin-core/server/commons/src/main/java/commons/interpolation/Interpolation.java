package commons.interpolation;

public interface Interpolation {

	/**
	 * @param a Alpha value between 0 and 1.
	 */
	float apply(float a);


	/**
	 * @param a Alpha value between 0 and 1.
	 */
	default float apply(float start, float end, float a) {
		return start + (end - start) * apply(a);
	}

}
