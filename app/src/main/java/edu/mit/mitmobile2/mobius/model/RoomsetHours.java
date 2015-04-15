package edu.mit.mitmobile2.mobius.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by sseligma on 4/7/15.
 */
public class RoomsetHours implements Parcelable {
    public static String OPEN = "Open";
    public static String CLOSED = "Closed";
    public static final String[] DAY_ARRAY = {"Mon","Tue","Wed","Thu","Fri","Sat","Sun"};

    String roomset_id;
    String day;
    String start_time;
    String end_time;
    String status; // open or closed

    public RoomsetHours(String day, String start, String end) {
        this.day = day;
        this.start_time = start;
        this.end_time = end;
    }

    public String getRoomset_id() {
        return roomset_id;
    }

    public void setRoomset_id(String roomset_id) {
        this.roomset_id = roomset_id;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.roomset_id);
        dest.writeString(this.day);
        dest.writeString(this.start_time);
        dest.writeString(this.end_time);
        dest.writeString(this.status);
    }

    private RoomsetHours(Parcel in) {
        this.roomset_id = in.readString();
        this.day = in.readString();
        this.start_time = in.readString();
        this.end_time = in.readString();
        this.status = in.readString();
    }

    public static final Parcelable.Creator<RoomsetHours> CREATOR = new Parcelable.Creator<RoomsetHours>() {
        public RoomsetHours createFromParcel(Parcel source) {
            return new RoomsetHours(source);
        }

        public RoomsetHours[] newArray(int size) {
            return new RoomsetHours[size];
        }
    };

}
