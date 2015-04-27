package edu.mit.mitmobile2.events.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by grmartin on 4/27/15.
 */
public class MITCalendarSponsor extends MITCalendarContact implements Parcelable {
    @SerializedName("group_id")
    protected String groupId;

    public MITCalendarSponsor() {}

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    @Override
    public String toString() {
        return "MITCalendarSponsor{" +
            "groupId='" + groupId + '\'' +
            "} " + super.toString();
    }

    protected MITCalendarSponsor(Parcel in) {
        email = in.readString();
        location = in.readString();
        name = in.readString();
        phone = in.readString();
        websiteURL = in.readString();
        groupId = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(email);
        dest.writeString(location);
        dest.writeString(name);
        dest.writeString(phone);
        dest.writeString(websiteURL);
        dest.writeString(groupId);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<MITCalendarSponsor> CREATOR = new Parcelable.Creator<MITCalendarSponsor>() {
        @Override
        public MITCalendarSponsor createFromParcel(Parcel in) {
            return new MITCalendarSponsor(in);
        }

        @Override
        public MITCalendarSponsor[] newArray(int size) {
            return new MITCalendarSponsor[size];
        }
    };

}
