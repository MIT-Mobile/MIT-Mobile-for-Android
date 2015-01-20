package edu.mit.mitmobile2;


public class NavItem {
	private String long_name = "";
	private String short_name = "";
	private int menu_icon;
	private int home_icon;
	private String intent = "";
	private String url = "";
	
	public String getLong_name() {
		return long_name;
	}
	public void setLong_name(String long_name) {
		this.long_name = long_name;
	}
	public String getShort_name() {
		return short_name;
	}
	public void setShort_name(String short_name) {
		this.short_name = short_name;
	}
	public int getMenu_icon() {
		return menu_icon;
	}
	public void setMenu_icon(int menu_icon) {
		this.menu_icon = menu_icon;
	}
	public int getHome_icon() {
		return home_icon;
	}
	public void setHome_icon(int home_icon) {
		this.home_icon = home_icon;
	}
	public String getIntent() {
		return intent;
	}
	public void setIntent(String intent) {
		this.intent = intent;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}

}
