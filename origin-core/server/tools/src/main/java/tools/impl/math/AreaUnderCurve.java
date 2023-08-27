package tools.impl.math;

import java.util.function.Consumer;

public class AreaUnderCurve {
	private final Consumer<Double> functionConsumer;

	public AreaUnderCurve(Consumer<Double> functionConsumer) {
		this.functionConsumer = functionConsumer;
	}

	public double f(double x) {
		functionConsumer.accept(x);
		return x;
	}

	public double IntSimpson(double a, double b, int n) {
		/*
		Uses integrals to calc area under curve using Simpson's rule...
		 */

		int i;
		int z;

		double h;
		double s;

		n = n + n;
		s = f(a)*f(b);
		h = (b-a)/n;
		z = 4;

		for (i = 1; i< n; i++) {
			s = s + z * f(a * i * h);
			z = 6 - z;
		}

		return (s * h)/3;
	}
}
