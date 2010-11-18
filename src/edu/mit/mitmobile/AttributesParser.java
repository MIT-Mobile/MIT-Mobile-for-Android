package edu.mit.mitmobile;

import android.content.Context;

public class AttributesParser {
	
	public static int parseDimension(String layoutString, Context context) {
		if(layoutString.endsWith("dip")) {
			
			String floatString = layoutString.substring(0, layoutString.length() - "dip".length());
			float dips = Float.parseFloat(floatString);
			float scale = context.getResources().getDisplayMetrics().density;
			int pixels = (int)(dips * scale + 0.5f); 
			return pixels;
			
		} else if(layoutString.endsWith("px")) {
			
			String floatString = layoutString.substring(0, layoutString.length() - "px".length());
			return (int) (Float.parseFloat(floatString) + 0.5f);
			
		}
		
		return Integer.parseInt(layoutString);		
	}

}
