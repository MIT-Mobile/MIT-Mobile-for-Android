package edu.mit.mitmobile2;

import android.graphics.drawable.Drawable;

public class CusMenuItem {

    private Drawable icon;
    private String title;
    private int id;
    private int iconResId;

    public CusMenuItem(int id, String title) {
    	this.id = id;
    	this.title = title;
    }
    
    public CusMenuItem(int id, String title, Drawable icon) {
    	this(id, title);
    	this.icon = icon;
    }
    
    public CusMenuItem(int id, String title, int iconResId) {
    	this(id, title);
    	this.iconResId = iconResId;
    }
    
    public void setTitle(String title) {
    	this.title = title;
    }
    
    public String getTitle() {
    	return this.title;
    }
    
    public int getId() {
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