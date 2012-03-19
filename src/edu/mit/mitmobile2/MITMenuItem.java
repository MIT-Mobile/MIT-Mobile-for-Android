package edu.mit.mitmobile2;

import android.graphics.drawable.Drawable;

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
    
    public int getIconResId() {
    	return iconResId;
    }
}