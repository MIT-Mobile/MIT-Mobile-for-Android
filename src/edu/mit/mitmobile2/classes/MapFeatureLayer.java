package edu.mit.mitmobile2.classes;

public class MapFeatureLayer extends MapLayer {

	private boolean isTiledLayer;
	private boolean isDataLayer;
    private MapEntity entity;
	public boolean isTiledLayer() {
		return isTiledLayer;
	}
	public void setTiledLayer(boolean isTiledLayer) {
		this.isTiledLayer = isTiledLayer;
	}
	public boolean isDataLayer() {
		return isDataLayer;
	}
	public void setDataLayer(boolean isDataLayer) {
		this.isDataLayer = isDataLayer;
	}
	public MapEntity getEntity() {
		return entity;
	}
	public void setEntity(MapEntity entity) {
		this.entity = entity;
	}
    
}
