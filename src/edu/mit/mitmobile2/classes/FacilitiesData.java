package edu.mit.mitmobile2.classes;

public class FacilitiesData {

	private String problemType;
	private String locationCategory;
	private String locationId;
	private String locationName;
	private String buildingNumber;
	private String buildingRoomName;
	
	public String getBuildingRoomName() {
		return buildingRoomName;
	}

	public void setBuildingRoomName(String buildingRoomName) {
		this.buildingRoomName = buildingRoomName;
	}

	public String getBuildingNumber() {
		return buildingNumber;
	}

	public void setBuildingNumber(String buildingNumber) {
		this.buildingNumber = buildingNumber;
	}

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}
	
	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public String getLocationCategory() {
		return locationCategory;
	}

	public void setLocationCategory(String locationCategory) {
		this.locationCategory = locationCategory;
	}

	public String getProblemType() {
		return problemType;
	}

	public void setProblemType(String problemType) {
		this.problemType = problemType;
	}

}
