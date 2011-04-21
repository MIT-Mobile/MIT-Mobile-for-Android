package edu.mit.mitmobile2.mit150;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.provider.BaseColumns;

public class MIT150FeatureItem implements BaseColumns {
	
	public class Dimension {
		int height,width;
	}
	
	MIT150FeatureItem() {
		dim = new Dimension();
	}
	
	public long row_id;
	public String id;
	public String title;
	public String subtitle;
	private int tintColorInt = -1;
	private int titleColorInt = -1;
	private int arrowColorInt = -1;
	public String url;
	public String photo_url;
	public Bitmap bm;
	public Dimension dim;
	
	public void setTintColor(String tint) {	
		tintColorInt = colorString2int(tint);	
	}
	
	public void setTintColor(int tint) {	
		tintColorInt = tint;	
	}
	
	public void setTitleColor(String titleColor) {
		titleColorInt = colorString2int(titleColor);
	}
	
	public void setTitleColor(int titleColor) {
		titleColorInt = titleColor;
	}
	
	public void setArrowColor(String arrowColor) {
		arrowColorInt = colorString2int(arrowColor);
	}
	
	public void setArrowColor(int arrowColor) {
		arrowColorInt = arrowColor;
	}
	
	private static int colorString2int(String color) {
		String hexString;
		if (color.startsWith("#")) {
			hexString = color.substring(1);
		} else {
			hexString = color;
		}	
	    return Integer.valueOf(hexString, 16);
	}
	
	public int getTintColor() {
		return tintColorInt;
	}
	
	public int getTitleColor() {
		if(titleColorInt > -1) {
			return titleColorInt;
		} else {
			return tintColorInt;
		}
	}
	
	public int getArrowColor() {
		if(arrowColorInt > -1) {
			return arrowColorInt;
		} else {
			return tintColorInt;
		}
	}	
}