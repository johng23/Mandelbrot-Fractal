package fractal;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Arrays;

public class BigComplex2DArray {
	
	public static final int PRECISION = 30;
	public static final MathContext MC = new MathContext(30);
	public static final int ELEMENT_LENGTH = (int)(PRECISION * 3.4 / 8) + 1;
	
	public int[] data;
	public int width, length;
	
	public BigComplex2DArray(int width, int length) {
		data = new int[ELEMENT_LENGTH * 2 * width * length];
		this.width = width;
		this.length = length;
	}
	
	public void setElement(int x, int y, int[] r, int[] i, int scaleR, int scaleI) {
		System.arraycopy(r, 0, data, x * length + y, ELEMENT_LENGTH);
		System.arraycopy(i, 0, data, x * length + y + ELEMENT_LENGTH, ELEMENT_LENGTH);
		}
	public int[] getElement(int x, int y) {
		return Arrays.copyOfRange(data, x * length + y, x * length + y + ELEMENT_LENGTH * 2 + 1);
	}
}
