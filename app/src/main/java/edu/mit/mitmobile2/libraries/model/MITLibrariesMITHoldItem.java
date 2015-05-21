package edu.mit.mitmobile2.libraries.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by serg on 5/20/15.
 */
public class MITLibrariesMITHoldItem extends MITLibrariesMITItem implements Parcelable {

    @SerializedName("status")
    private String status;

    @SerializedName("pickup_location")
    private String pickupLocation;

    @SerializedName("ready_for_pickup")
    private boolean readyForPickup;

    public MITLibrariesMITHoldItem() {
        // empty constructor
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(String pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public boolean isReadyForPickup() {
        return readyForPickup;
    }

    public void setReadyForPickup(boolean readyForPickup) {
        this.readyForPickup = readyForPickup;
    }

    protected MITLibrariesMITHoldItem(Parcel in) {
        super(in);
        status = in.readString();
        pickupLocation = in.readString();
        readyForPickup = in.readByte() != 0x00;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(status);
        dest.writeString(pickupLocation);
        dest.writeByte((byte) (readyForPickup ? 0x01 : 0x00));
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<MITLibrariesMITHoldItem> CREATOR = new Parcelable.Creator<MITLibrariesMITHoldItem>() {
        @Override
        public MITLibrariesMITHoldItem createFromParcel(Parcel in) {
            return new MITLibrariesMITHoldItem(in);
        }

        @Override
        public MITLibrariesMITHoldItem[] newArray(int size) {
            return new MITLibrariesMITHoldItem[size];
        }
    };
}
