package edu.mit.mitmobile2.dining.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MITDiningLocation implements Parcelable {
    @SerializedName("latitude")
    protected String latitude;

    @SerializedName("longitude")
    protected String longitude;

    @SerializedName("description")
    protected String locationDescription;

    @SerializedName("street")
    protected String street;

    @SerializedName("city")
    protected String city;

    @SerializedName("mit_room_number")
    protected String mitRoomNumber;

	@SerializedName("state")
    protected String state;

	@SerializedName("zip_code")
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

    protected MITDiningLocation(Parcel in) {
        city = in.readString();
        latitude = in.readString();
        locationDescription = in.readString();
        longitude = in.readString();
        mitRoomNumber = in.readString();
        state = in.readString();
        street = in.readString();
        zipCode = in.readString();
        houseVenue = (MITDiningHouseVenue) in.readValue(MITDiningHouseVenue.class.getClassLoader());
        retailVenue = (MITDiningRetailVenue) in.readValue(MITDiningRetailVenue.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(city);
        dest.writeString(latitude);
        dest.writeString(locationDescription);
        dest.writeString(longitude);
        dest.writeString(mitRoomNumber);
        dest.writeString(state);
        dest.writeString(street);
        dest.writeString(zipCode);
        dest.writeValue(houseVenue);
        dest.writeValue(retailVenue);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<MITDiningLocation> CREATOR = new Parcelable.Creator<MITDiningLocation>() {
        @Override
        public MITDiningLocation createFromParcel(Parcel in) {
            return new MITDiningLocation(in);
        }

        @Override
        public MITDiningLocation[] newArray(int size) {
            return new MITDiningLocation[size];
        }
    };
}