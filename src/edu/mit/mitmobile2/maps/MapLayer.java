package edu.mit.mitmobile2.maps;

public class MapLayer {
	
	private String layerIdentifier;
    private String displayName;
    private String url;
    
	public String getLayerIdentifier() {
		return layerIdentifier;
	}
	public void setLayerIdentifier(String layerIdentifier) {
		this.layerIdentifier = layerIdentifier;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}

}
