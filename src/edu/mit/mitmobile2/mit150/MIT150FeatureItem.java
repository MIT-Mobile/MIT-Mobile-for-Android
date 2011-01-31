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
	private String tint_color;
	public int tint_colorInt;
	public String url;
	public String photo_url;
	public Bitmap bm;
	public Dimension dim;
	
	public void setTintColor(String tint) {	
		// tint should be int but json starts with # not 0x which is illegal 
		if (tint==null) {
			tint_color = "0";
			tint_colorInt = 0;
		} else {
			if (tint.startsWith("#")) tint_color = tint.substring(1);
			else tint_color = tint;
			tint_colorInt = Integer.valueOf(tint_color,16);
		}	
	}
	
	public String getTintColor() {
		return tint_color;
	}
	
}