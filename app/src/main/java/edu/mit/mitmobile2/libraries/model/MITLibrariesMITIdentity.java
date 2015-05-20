package edu.mit.mitmobile2.libraries.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by serg on 5/20/15.
 */
public class MITLibrariesMITIdentity implements Parcelable {

    @SerializedName("shib_identity")
    private String shibIdentity;

    @SerializedName("username")
    private String username;

    @SerializedName("is_mit_identity")
    private boolean isMITIdentity;

    public MITLibrariesMITIdentity() {
        // empty constructor
    }

    public String getShibIdentity() {
        return shibIdentity;
    }

    public void setShibIdentity(String shibIdentity) {
        this.shibIdentity = shibIdentity;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isMITIdentity() {
        return isMITIdentity;
    }

    public void setIsMITIdentity(boolean isMITIdentity) {
        this.isMITIdentity = isMITIdentity;
    }

    protected MITLibrariesMITIdentity(Parcel in) {
        shibIdentity = in.readString();
        username = in.readString();
        isMITIdentity = in.readByte() != 0x00;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(shibIdentity);
        dest.writeString(username);
        dest.writeByte((byte) (isMITIdentity ? 0x01 : 0x00));
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<MITLibrariesMITIdentity> CREATOR = new Parcelable.Creator<MITLibrariesMITIdentity>() {
        @Override
        public MITLibrariesMITIdentity createFromParcel(Parcel in) {
            return new MITLibrariesMITIdentity(in);
        }

        @Override
        public MITLibrariesMITIdentity[] newArray(int size) {
            return new MITLibrariesMITIdentity[size];
        }
    };
}
