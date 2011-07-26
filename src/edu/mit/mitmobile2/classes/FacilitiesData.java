package edu.mit.mitmobile2.classes;

public class FacilitiesData {

	private String problemType;
	private String locationCategory;
	private String locationId;
	private String locationName;
	private String userAssignedLocationName;
	private String buildingNumber;
	private String buildingRoomName;
	private String userAssignedRoomName;
	
	
	public String getBuildingRoomName() {
		return buildingRoomName;
	}

	public void setBuildingRoomName(String buildingRoomName) {
		this.buildingRoomName = buildingRoomName;
	}

	public String getUserAssignedRoomName() {
		return userAssignedRoomName;
	}

	public void setUserAssignedRoomName(String userAssignedRoomName) {
		this.userAssignedRoomName = userAssignedRoomName;
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
	
	public String getUserAssignedLocationName() {
		return userAssignedLocationName;
	}

	public void setUserAssignedLocationName(String userAssignedLocationName) {
		this.userAssignedLocationName = userAssignedLocationName;
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
