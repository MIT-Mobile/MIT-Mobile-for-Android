package edu.mit.mitmobile2;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

public class MITMenuItem {

    private Drawable icon;
    private String title;
    private String id;
    private int iconResId;

    public MITMenuItem(String id, String title) {
    	this.id = id;
    	this.title = title;
    }
    
    public MITMenuItem(String id, String title, Drawable icon) {
    	this(id, title);
    	this.icon = icon;
    }
    
    public MITMenuItem(String id, String title, int iconResId) {
    	this(id, title);
    	this.iconResId = iconResId;
    }
    
    public void setTitle(String title) {
    	this.title = title;
    }
    
    public String getTitle() {
    	return this.title;
    }
    
    public String getId() {
    	return this.id;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }
    
    public Drawable getIcon() {
        return this.icon;
    }
    
    public void setIconResId(int resId) {
    	this.iconResId = resId;
    }
    
    public int getIconResId() {
    	return iconResId;
    }
    
    public View getView(Context context) {
    	LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	ImageView img = (ImageView) inflater.inflate(R.layout.titlebar_menu_item, null);

    	Drawable icon = getIcon();
    
    	if (getIconResId() != 0) {
    		img.setImageResource(getIconResId());
    	} else if (icon != null) {
    		img.setImageDrawable(icon);
    	}
    	
    	return img;
    }
}