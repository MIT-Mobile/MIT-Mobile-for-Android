package edu.mit.mitmobile2;


public class NavItem {
	private String longName = "";
	private String shortName = "";
	private int menuIcon;
	private int homeIcon;
	private String intent = "";
	private String url = "";
	
	public String getLongName() {
		return longName;
	}

	public void setLong_name(String longName) {
		this.longName = longName;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public int getMenuIcon() {
		return menuIcon;
	}

	public void setMenuIcon(int menuIcon) {
		this.menuIcon = menuIcon;
	}

	public int getHomeIcon() {
		return homeIcon;
	}

	public void setHomeIcon(int homeIcon) {
		this.homeIcon = homeIcon;
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
