package com.fullwall.SkyPirates;

public class RangeHandler {

	public static double range(double value, double max, double min) {
        double result = value;

		if (result > max) {
			result = max;
		} else if (result < min) {
			result = min;
		}
		
		return result;
	}
}
