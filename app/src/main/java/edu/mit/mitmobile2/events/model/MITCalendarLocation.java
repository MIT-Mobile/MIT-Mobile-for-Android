package edu.mit.mitmobile2.events.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by grmartin on 4/27/15.
 */
public class MITCalendarLocation implements Parcelable {
    protected String description;
    @SerializedName("room_number")
    protected String roomNumber;
    protected Object coordinates;

    // I have a feeling this might create a cyclic reference situation, commenting out for now.
    // HashSet<MITCalendarEvent> events;

    public MITCalendarLocation() {}

    public Object getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Object coordinates) {
        this.coordinates = coordinates;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

//    public HashSet<MITCalendarEvent> getEvents() {
//        return events;
//    }
//
//    public void setEvents(HashSet<MITCalendarEvent> events) {
//        this.events = events;
//    }

    protected MITCalendarLocation(Parcel in) {
        description = in.readString();
        roomNumber = in.readString();
        coordinates = (Object) in.readValue(Object.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(description);
        dest.writeString(roomNumber);
        dest.writeValue(coordinates);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<MITCalendarLocation> CREATOR = new Parcelable.Creator<MITCalendarLocation>() {
        @Override
        public MITCalendarLocation createFromParcel(Parcel in) {
            return new MITCalendarLocation(in);
        }

        @Override
        public MITCalendarLocation[] newArray(int size) {
            return new MITCalendarLocation[size];
        }
    };
}

