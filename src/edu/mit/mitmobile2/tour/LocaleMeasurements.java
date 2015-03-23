package edu.mit.mitmobile2.tour;

import java.util.Locale;

public class LocaleMeasurements {
	
	private static final float MILE = 1609.344f; // meters in a mile
	private static final float YARD = 0.9144f; // meters in a yard
	private static final float SMOOT = 1.7018f; // meters in a smoot
	
	public static String getDistance(float meters) {
		Locale locale = Locale.getDefault();
		
		//  really small distances, just mean that the distance is not correct
		if(meters < 1) {
			return null;
		}
		
		int smoots = Math.round(meters/SMOOT);
		String smootText = "(" + smoots + " smoots)";
		
		if(Locale.US.equals(locale)) {
			float yards = meters/YARD;
			if(yards < 1000) {
				return Math.round(yards) + " yards " + smootText;
			}
			
			float miles = meters/MILE;
			if(miles < 2) {
				return String.format("%.1f", miles) + " miles " + smootText;
			}
		
			return null;
		} else {
			if(meters < 1000) {
				return Math.round(meters) + " meters " + smootText;
			}
			
			float kilometers = meters/1000;
			if(kilometers < 5) {
				return String.format("%.1f", kilometers) + " kilometers " + smootText;
			}
			
			return null;
		}
	}
}
