package fractal;

import java.math.MathContext;

public class BigComplexDouble {
	
	public static final BigComplexDouble ONE = new BigComplexDouble(1, 0);
	public static final BigComplexDouble ZERO = new BigComplexDouble(0, 0);
	public static final int PRECISION = 30;
	public static final MathContext MC = new MathContext(30);
	public static final double TWO = 2;
	
	public double r;
	public double i;
	
	public BigComplexDouble(double r, double i) {
		this.r = r;
		this.i = i;
	}
	
	public BigComplexDouble add(BigComplexDouble augend) {
		return new BigComplexDouble(r + augend.r, i + augend.i);
	}
	
	public BigComplexDouble subtract(BigComplexDouble subtrahend) {
		return new BigComplexDouble(r-subtrahend.r, i-subtrahend.i);
	}
	
	public BigComplexDouble multiply(BigComplexDouble multiplicand) {
		return new BigComplexDouble(r*multiplicand.r-i*multiplicand.i, r*multiplicand.i+i*multiplicand.r);
	}
	
	public BigComplexDouble multiply(double multiplicand) {
		return new BigComplexDouble(r*multiplicand, i*multiplicand);
	}
	public boolean magnitudeLessThanTwo() {
		return (r*r+i*i) < 2;
	}
	public String toString() {
		return i >= 0 ? r + "+" + i + "i" : r + "" + i + "i";
	}
	
}
