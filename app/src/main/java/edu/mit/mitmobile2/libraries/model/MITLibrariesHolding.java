package edu.mit.mitmobile2.libraries.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by serg on 5/20/15.
 */
public class MITLibrariesHolding implements Parcelable {

    @SerializedName("code")
    private String code;

    @SerializedName("library")
    private String library;

    @SerializedName("address")
    private String address;

    @SerializedName("count")
    private int count;

    @SerializedName("item_request_url")
    private String requestUrl;

    @SerializedName("availability")
    private List<MITLibrariesAvailability> availability;

    public MITLibrariesHolding() {
        // empty constructor
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLibrary() {
        return library;
    }

    public void setLibrary(String library) {
        this.library = library;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public List<MITLibrariesAvailability> getAvailability() {
        return availability;
    }

    public void setAvailability(List<MITLibrariesAvailability> availability) {
        this.availability = availability;
    }

    protected MITLibrariesHolding(Parcel in) {
        code = in.readString();
        library = in.readString();
        address = in.readString();
        count = in.readInt();
        requestUrl = in.readString();
        if (in.readByte() == 0x01) {
            availability = new ArrayList<MITLibrariesAvailability>();
            in.readList(availability, MITLibrariesAvailability.class.getClassLoader());
        } else {
            availability = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(code);
        dest.writeString(library);
        dest.writeString(address);
        dest.writeInt(count);
        dest.writeString(requestUrl);
        if (availability == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(availability);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<MITLibrariesHolding> CREATOR = new Parcelable.Creator<MITLibrariesHolding>() {
        @Override
        public MITLibrariesHolding createFromParcel(Parcel in) {
            return new MITLibrariesHolding(in);
        }

        @Override
        public MITLibrariesHolding[] newArray(int size) {
            return new MITLibrariesHolding[size];
        }
    };
}
