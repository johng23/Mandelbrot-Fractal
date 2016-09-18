package fractal;

import java.math.BigDecimal;
import java.math.MathContext;

public class BigComplex {
	
	public static final BigComplex ONE = new BigComplex(BigDecimal.ONE, BigDecimal.ZERO);
	public static final BigComplex ZERO = new BigComplex(BigDecimal.ZERO, BigDecimal.ZERO);
	public static final int PRECISION = 30;
	public static final MathContext MC = new MathContext(30);
	public static final BigDecimal TWO = new BigDecimal("2");
	public static final int ARRAY_LENGTH = (int)(PRECISION * 3.4) + 1;
	
	public int[] r;
	public int[] i;
	public int scale;
	
	public BigComplex(int[] r, int[] i, int scale) {
		this.r = r;
		this.i = i;
		this.scale = scale;
	}
	
	public BigComplex add(BigComplex augend) {
		return new BigComplex(r.add(augend.r, MC), i.add(augend.i, MC));
	}
	
	public BigComplex subtract(BigComplex subtrahend) {
		return new BigComplex(r.subtract(subtrahend.r, MC), i.subtract(subtrahend.i, MC));
	}
	
	public BigComplex multiply(BigComplex multiplicand) {
		return new BigComplex(r.multiply(multiplicand.r, MC).subtract(i.multiply(multiplicand.i, MC)),
						   r.multiply(multiplicand.i, MC).add(i.multiply(multiplicand.r, MC)));
	}
	
	public BigComplex multiply(BigDecimal multiplicand) {
		return new BigComplex(r.multiply(multiplicand, MC), i.multiply(multiplicand, MC));
	}
	public boolean magnitudeLessThanTwo() {
		return r.multiply(r, MC).add(i.multiply(i, MC)).compareTo(TWO) <= 0;
	}
	public String toString() {
		return i.compareTo(BigDecimal.ZERO) >= 0 ? r + "+" + i + "i" : r + "" + i + "i";
	}
	
}
