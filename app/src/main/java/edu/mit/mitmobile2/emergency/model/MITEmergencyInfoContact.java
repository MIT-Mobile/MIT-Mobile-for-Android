package edu.mit.mitmobile2.emergency.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by grmartin on 4/16/15.
 */
public class MITEmergencyInfoContact implements Parcelable {

    @SerializedName("name")
    private String name;

    @SerializedName("phone")
    private String phone;

    @SerializedName("description")
    private String description;

    public MITEmergencyInfoContact() {
        // empty constructor
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "MITEmergencyInfoContact{" +
                "description='" + description + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }

    /* Parcelable */

    protected MITEmergencyInfoContact(Parcel in) {
        name = in.readString();
        phone = in.readString();
        description = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(phone);
        dest.writeString(description);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<MITEmergencyInfoContact> CREATOR = new Parcelable.Creator<MITEmergencyInfoContact>() {
        @Override
        public MITEmergencyInfoContact createFromParcel(Parcel in) {
            return new MITEmergencyInfoContact(in);
        }

        @Override
        public MITEmergencyInfoContact[] newArray(int size) {
            return new MITEmergencyInfoContact[size];
        }
    };

}
