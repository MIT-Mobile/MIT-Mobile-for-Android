package edu.mit.mitmobile2.dining.model;

import com.google.gson.annotations.SerializedName;

public class MITDiningLocation {
    protected String city;
    protected String latitude;
    @SerializedName("description")
    protected String locationDescription;
    protected String longitude;
    @SerializedName("mit_room_number")
    protected String mitRoomNumber;
    protected String state;
    protected String street;
    @SerializedName("zip_code")
    protected String zipCode;
    protected MITDiningHouseVenue houseVenue;
    protected MITDiningRetailVenue retailVenue;

	public String getCity() {
		return city;
	}

	public String getLatitude() {
		return latitude;
	}

	public String getLocationDescription() {
		return locationDescription;
	}

	public String getLongitude() {
		return longitude;
	}

	public String getMitRoomNumber() {
		return mitRoomNumber;
	}

	public String getState() {
		return state;
	}

	public String getStreet() {
		return street;
	}

	public String getZipCode() {
		return zipCode;
	}

	public MITDiningHouseVenue getHouseVenue() {
		return houseVenue;
	}

	public MITDiningRetailVenue getRetailVenue() {
		return retailVenue;
	}

	@Override
	public String toString() {
		return "MITDiningLocation{" +
			"city='" + city + '\'' +
			", latitude='" + latitude + '\'' +
			", locationDescription='" + locationDescription + '\'' +
			", longitude='" + longitude + '\'' +
			", mitRoomNumber='" + mitRoomNumber + '\'' +
			", state='" + state + '\'' +
			", street='" + street + '\'' +
			", zipCode='" + zipCode + '\'' +
			", houseVenue=" + houseVenue +
			", retailVenue=" + retailVenue +
			'}';
	}
}