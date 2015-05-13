package edu.mit.mitmobile2.dining.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class MITDiningLocation implements Serializable {

	@SerializedName("latitude")
	protected String latitude;

	@SerializedName("longitude")
	protected String longitude;

	@SerializedName("description")
	protected String locationDescription;

	@SerializedName("street")
	protected String street;

	@Expose
    protected String city;

    @Expose
    protected String mitRoomNumber;

	@Expose
    protected String state;

	@Expose
    protected String zipCode;

	@Expose
    protected MITDiningHouseVenue houseVenue;

	@Expose
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