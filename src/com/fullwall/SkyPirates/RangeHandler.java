package com.fullwall.SkyPirates;

public class RangeHandler {

	public static double range(double value, double max, double min) {
		if (value > max) {
			value = max;
		} else if (value < min) {
			value = min;
		}
		
		return value;
	}
}
