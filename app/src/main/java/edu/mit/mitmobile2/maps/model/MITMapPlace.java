package edu.mit.mitmobile2.maps.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import edu.mit.mitmobile2.R;

/**
 * Created by serg on 5/18/15.
 */
public class MITMapPlace implements Parcelable {

    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("lat_wgs84")
    private double latitude;

    @SerializedName("long_wgs84")
    private double longitude;

    @SerializedName("bldgnum")
    private String buildingNumber;

    @SerializedName("bldgimg")
    private String buildingImage;

    @SerializedName("street")
    private String street;

    @SerializedName("architect")
    private String architect;

    @SerializedName("mailing")
    private String mailing;

    @SerializedName("viewangle")
    private String viewangle;

    // TODO: add fields:
    // category
    // contents


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getBuildingNumber() {
        return buildingNumber;
    }

    public void setBuildingNumber(String buildingNumber) {
        this.buildingNumber = buildingNumber;
    }

    public String getBuildingImage() {
        return buildingImage;
    }

    public void setBuildingImage(String buildingImage) {
        this.buildingImage = buildingImage;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getArchitect() {
        return architect;
    }

    public void setArchitect(String architect) {
        this.architect = architect;
    }

    public String getMailing() {
        return mailing;
    }

    public void setMailing(String mailing) {
        this.mailing = mailing;
    }

    public String getViewangle() {
        return viewangle;
    }

    public void setViewangle(String viewangle) {
        this.viewangle = viewangle;
    }

    /* Helpers */

    public String getTitle(Context context) {
        if (TextUtils.isEmpty(buildingNumber)) {
            return name;
        } else {
            return context.getString(R.string.map_categories_detail_place_title, buildingNumber);
        }
    }

    public String getSubtitle(Context context) {
        if (!name.equals(getTitle(context))) {
            return name;
        }

        return "";
    }

    /* Parcelable */

    protected MITMapPlace(Parcel in) {
        id = in.readString();
        name = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        buildingNumber = in.readString();
        buildingImage = in.readString();
        street = in.readString();
        architect = in.readString();
        mailing = in.readString();
        viewangle = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(buildingNumber);
        dest.writeString(buildingImage);
        dest.writeString(street);
        dest.writeString(architect);
        dest.writeString(mailing);
        dest.writeString(viewangle);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<MITMapPlace> CREATOR = new Parcelable.Creator<MITMapPlace>() {
        @Override
        public MITMapPlace createFromParcel(Parcel in) {
            return new MITMapPlace(in);
        }

        @Override
        public MITMapPlace[] newArray(int size) {
            return new MITMapPlace[size];
        }
    };
}
